<template>
    <div class="h-full">
        <div ref="chartRef" class="w-full h-full"></div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
    data: {
        type: Object,
        required: true
    }
});

// echarts实例
let chart = null;
const chartRef = ref(null);

// 当前显示的批次索引
const currentBatchIndex = ref(0);
// 计时器
let rotationTimer = null;

// 组件挂载时初始化图表
onMounted(() => {
    if (chartRef.value) {
        // 初始化图表
        chart = echarts.init(chartRef.value);
        // 设置一个小延迟，确保容器已经完全渲染
        setTimeout(() => {
            renderChart();
            // 监听窗口大小变化
            window.addEventListener('resize', handleResize);
        }, 100);
    }
});

// 组件销毁时释放图表实例
onBeforeUnmount(() => {
    if (chart) {
        chart.dispose();
        chart = null;
    }
    // 清除轮播定时器
    if (rotationTimer) {
        clearInterval(rotationTimer);
        rotationTimer = null;
    }
    window.removeEventListener('resize', handleResize);
});

// 启动数据轮播
const startDataRotation = (allData) => {
    // 如果数据少于等于5条，就不需要轮播
    if (allData.length <= 5) {
        return;
    }
    
    // 清除可能存在的旧定时器
    if (rotationTimer) {
        clearInterval(rotationTimer);
    }
    
    // 设置定时器，每3秒切换一次显示的数据批次
    rotationTimer = setInterval(() => {
        // 计算总批次数（向上取整）
        const totalBatches = Math.ceil(allData.length / 5);
        
        // 更新当前批次索引，循环显示
        currentBatchIndex.value = (currentBatchIndex.value + 1) % totalBatches;
        
        // 重新渲染图表
        renderChart();
    }, 3000);
};

// 监听数据变化
watch(() => props.data, 
    (newVal) => {
        if (chart && newVal?.recentTrend) {
            // 重置批次索引
            currentBatchIndex.value = 0;
            
            // 渲染第一批数据
            renderChart();
            
            // 启动数据轮播
            startDataRotation(newVal.recentTrend);
        }
    }, 
    { deep: true }
);

// 渲染图表
const renderChart = () => {
    if (!chart) {
        return;
    }
    
    if (!props.data || !props.data.recentTrend || !Array.isArray(props.data.recentTrend)) {
        console.error('缺陷趋势数据无效:', props.data);
        return;
    }
    
    // 按日期排序（从早到晚）
    const sortedTrendData = [...props.data.recentTrend].sort((a, b) => {
        return new Date(a.date) - new Date(b.date);
    });
    
    // 获取当前应该显示的数据批次
    const pageSize = 5;  // 每页显示5条数据
    const startIndex = currentBatchIndex.value * pageSize;
    const currentPageData = sortedTrendData.slice(startIndex, startIndex + pageSize);
    
    // 如果当前批次没有数据，重置为第一批次
    if (currentPageData.length === 0) {
        currentBatchIndex.value = 0;
        const resetPageData = sortedTrendData.slice(0, pageSize);
        updateChart(resetPageData);
        return;
    }
    
    updateChart(currentPageData);
};

// 更新图表的函数
const updateChart = (displayData) => {
    // 提取日期和数量
    const dates = displayData.map(item => formatDate(item.date));
    const counts = displayData.map(item => item.count);
    
    // 计算y轴最大值
    const maxCount = Math.max(...counts);
    const yAxisMax = Math.max(Math.ceil(maxCount * 1.3), maxCount + 2);
    
    // 构建折线图配置
    const option = {
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'line',
                lineStyle: {
                    color: 'rgba(0, 147, 255, 0.5)',
                    width: 2
                }
            },
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            borderColor: 'rgba(0, 147, 255, 0.7)',
            borderWidth: 1,
            textStyle: {
                color: '#ffffff',
                fontSize: 12
            },
            formatter: function(params) {
                const data = params[0].data;
                return `${params[0].axisValue}<br/>${params[0].marker}缺陷数量: ${data}`;
            }
        },
        grid: {
            top: 40,
            right: 20,
            bottom: 40,
            left: 40,
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: dates,
            boundaryGap: false,
            axisLine: {
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)'
                }
            },
            axisTick: {
                alignWithLabel: true,
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)'
                }
            },
            axisLabel: {
                color: '#9eb1c8',
                fontSize: 10,
                rotate: 30,
                margin: 14
            }
        },
        yAxis: {
            type: 'value',
            name: '数量',
            nameTextStyle: {
                color: '#9eb1c8',
                fontSize: 11,
                padding: [0, 0, 0, 15]
            },
            min: 0,
            max: yAxisMax,
            minInterval: 1,
            splitNumber: 4,
            splitLine: {
                show: true,
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.1)',
                    type: 'dashed',
                    width: 1
                }
            },
            axisLine: {
                show: true,
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)'
                }
            },
            axisLabel: {
                color: '#9eb1c8',
                fontSize: 10
            }
        },
        series: [
            {
                name: '缺陷数量',
                type: 'line',
                data: counts,
                smooth: true,  // 平滑曲线
                symbol: 'circle',
                symbolSize: 8,
                itemStyle: {
                    color: '#00b0ff'
                },
                lineStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                        { offset: 0, color: 'rgba(0, 176, 255, 0.8)' },
                        { offset: 1, color: 'rgba(0, 119, 255, 0.8)' }
                    ]),
                    width: 3,
                    shadowColor: 'rgba(0, 150, 255, 0.5)',
                    shadowBlur: 10
                },
                areaStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                        { offset: 0, color: 'rgba(0, 176, 255, 0.5)' },
                        { offset: 1, color: 'rgba(0, 119, 255, 0.05)' }
                    ])
                },
                emphasis: {
                    itemStyle: {
                        color: '#ffffff',
                        borderColor: '#00b0ff',
                        borderWidth: 2,
                        shadowColor: 'rgba(0, 176, 255, 0.8)',
                        shadowBlur: 12
                    }
                }
            }
        ],
        backgroundColor: 'transparent',
        textStyle: {
            color: '#ffffff'
        },
        animation: true,
        animationDuration: 800,
        animationEasing: 'cubicOut'
    };
    
    // 添加批次指示器（如果有多批次数据）
    const totalItems = props.data.recentTrend.length;
    if (totalItems > 5) {
        const totalBatches = Math.ceil(totalItems / 5);
        
        // 只有多于1页数据时才显示页码指示器
        if (totalBatches > 1) {
            option.title = {
                text: `${currentBatchIndex.value + 1}/${totalBatches}`,
                right: '5px',
                top: '5px',
                textStyle: {
                    color: 'rgba(255, 255, 255, 0.5)',
                    fontSize: 12,
                    fontWeight: 'normal'
                }
            };
        }
    }
    
    // 设置图表配置
    try {
        chart.setOption(option, true);
    } catch (error) {
        console.error('渲染折线图失败:', error);
    }
};

// 格式化日期显示
const formatDate = (dateStr) => {
    const date = new Date(dateStr);
    const month = date.getMonth() + 1;
    const day = date.getDate();
    return `${month}月${day}日`;
};

// 处理窗口大小变化
const handleResize = () => {
    if (chart) {
        chart.resize();
    }
};
</script>

<style scoped>
</style>
