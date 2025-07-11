<template>
    <div class="h-full">
        <div ref="target" class="w-full h-full"></div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from "vue";
import * as echarts from 'echarts'

const props = defineProps({
    data: {
        type: Object,
        required: true
    },
})

// 当前显示的批次索引
const currentBatchIndex = ref(0);
// 计时器
let rotationTimer = null;

// 初始化 echarts 实例
let mChart = null;
const target = ref(null)

// 存储所有数据的最大值
const allDataMaxValue = ref(0);

onMounted(() => {
    if (target.value) {
        mChart = echarts.init(target.value)
        // 设置一个小延迟，确保容器已经完全渲染
        setTimeout(() => {
        renderChart()
            // 监听窗口大小变化
            window.addEventListener('resize', resizeHandler)
        }, 100)
    }
})

// 组件销毁时释放图表实例
onBeforeUnmount(() => {
    if (mChart) {
        mChart.dispose()
        mChart = null
    }
    
    // 清除轮播定时器
    if (rotationTimer) {
        clearInterval(rotationTimer)
        rotationTimer = null
    }
    
    // 移除窗口大小变化监听
    window.removeEventListener('resize', resizeHandler)
})

// 启动数据轮播
const startDataRotation = (allData) => {
    // 如果数据少于等于6个月，就不需要轮播
    if (allData.length <= 6) {
        return
    }
    
    // 清除可能存在的旧定时器
    if (rotationTimer) {
        clearInterval(rotationTimer)
    }
    
    // 设置定时器，每3秒切换一次显示的数据批次
    rotationTimer = setInterval(() => {
        // 计算总批次数（向上取整）
        const totalBatches = Math.ceil(allData.length / 6)
        
        // 更新当前批次索引，循环显示
        currentBatchIndex.value = (currentBatchIndex.value + 1) % totalBatches
        
        // 重新渲染图表
        renderChart()
    }, 3000)
}

// 监听数据变化重新渲染图表
watch(() => props.data, 
    (newVal) => {
        if (mChart && newVal?.monthlyInspections) {
            // 计算后端返回的所有数据的最大值
            if (Array.isArray(newVal.monthlyInspections) && newVal.monthlyInspections.length > 0) {
                allDataMaxValue.value = Math.max(...newVal.monthlyInspections.map(item => item.count));
            }
            
            // 重置批次索引
            currentBatchIndex.value = 0
            
            // 渲染第一批数据
            renderChart()
            
            // 启动数据轮播
            startDataRotation(newVal.monthlyInspections)
        }
    },
    { deep: true }
)

// 渲染图表
const renderChart = () => {
    if (!mChart) {
        return
    }
    
    if (!props.data) {
        return
    }
    
    if (!props.data.monthlyInspections) {
        return
    }
    
    if (!Array.isArray(props.data.monthlyInspections) || props.data.monthlyInspections.length === 0) {
        return
    }
    
    // 按日期排序（从早到晚）
    const sortedData = [...props.data.monthlyInspections].sort((a, b) => {
        return new Date(a.yearMonth) - new Date(b.yearMonth)
    })
    
    // 获取当前应该显示的数据批次
    const pageSize = 6  // 每页显示6个月
    const startIndex = currentBatchIndex.value * pageSize
    const currentPageData = sortedData.slice(startIndex, startIndex + pageSize)
    
    // 如果当前批次没有数据，重置为第一批次
    if (currentPageData.length === 0) {
        currentBatchIndex.value = 0
        const resetPageData = sortedData.slice(0, pageSize)
        updateChart(resetPageData)
        return
    }
    
    updateChart(currentPageData)
}

// 更新图表的函数
const updateChart = (displayData) => {
    // 提取月份标签和数据值
    const months = displayData.map(item => formatMonth(item.yearMonth))
    const counts = displayData.map(item => item.count)
    
    // 使用所有数据的最大值计算y轴上限，而不仅仅是当前显示的数据
    const maxCount = allDataMaxValue.value * 1.2
    // 根据最大值动态计算y轴上限
    const yAxisMax = Math.max(Math.ceil(maxCount), allDataMaxValue.value + 2)
    
    // 设置图表配置
    const options = {
        title: {
            text: '',
            textStyle: {
                color: '#ffffff',
                fontSize: 12, // 缩小字体
                fontWeight: 'normal'
            },
            left: 'center',
            top: 5
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            borderColor: 'rgba(0, 147, 255, 0.7)',
            borderWidth: 1,
            textStyle: {
                color: '#ffffff',
                fontSize: 10 // 缩小提示框字体
            },
            formatter: '{b}: {c} 次'
        },
        grid: {
            top: 32,
            right: 16,
            bottom: 8,
            left: 32,
            containLabel: true
        },
        xAxis: {
            type: 'category',
            data: months,
            axisLine: {
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)',
                    width: 1 // 缩小线宽
                }
            },
            axisTick: {
                alignWithLabel: true,
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)',
                    width: 1 // 缩小线宽
                }
            },
            axisLabel: {
                color: '#9eb1c8',
                interval: 0,
                fontSize: 8, // 缩小字体
                margin: 12, // 调整边距
                align: 'center',
                rotate: 30, // 旋转角度
                rich: {
                    value: {
                        lineHeight: 11, // 缩小行高
                        align: 'center'
                    }
                },
                formatter: function(value) {
                    const [year, month] = value.split('年');
                    return year + '年\n' + month;
                }
            }
        },
        yAxis: {
            type: 'value',
            name: '次数',
            nameTextStyle: {
                color: '#9eb1c8',
                fontSize: 9, // 缩小字体
                padding: [0, 0, 0, 12] // 调整内边距
            },
            min: 0,
            max: yAxisMax,
            minInterval: 1,
            splitNumber: 6, // 适当减少分割数
            boundaryGap: [0, 0.1],
            splitLine: {
                show: true,
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.1)',
                    type: 'solid',
                    width: 0.8 // 缩小线宽
                }
            },
            axisLine: {
                show: true,
                lineStyle: {
                    color: 'rgba(255, 255, 255, 0.3)',
                    width: 1 // 缩小线宽
                }
            },
            axisLabel: {
                color: '#9eb1c8',
                fontSize: 8, // 缩小字体
                margin: 16,
                formatter: '{value}'
            }
        },
        series: [
            {
                type: 'bar',
                data: counts.map((value, index) => {
                    return {
                        value: value,
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: 'rgba(0, 174, 255, 1)' },
                                { offset: 1, color: 'rgba(0, 113, 206, 1)' }
                            ]),
                            borderRadius: [2, 2, 0, 0] // 缩小圆角
                        }
                    }
                }),
                barWidth: '32%', // 缩小宽度
                barGap: '8%', // 缩小间距
                barCategoryGap: '24%', // 缩小类目间距
                showBackground: true,
                backgroundStyle: {
                    color: 'rgba(40, 40, 40, 0.2)',
                    borderRadius: [2, 2, 0, 0] // 缩小圆角
                },
                itemStyle: {
                    shadowColor: 'rgba(0, 147, 255, 0.3)',
                    shadowBlur: 4 // 缩小阴影
                },
                label: {
                    show: true,
                    position: 'top',
                    formatter: '{c}',
                    fontSize: 8, // 缩小字体
                    fontWeight: 'normal',
                        color: '#ffffff',
                    distance: 1
                }
            }
        ],
        animation: true,
        animationDuration: 800,
        animationEasing: 'cubicOut'
    }
    
    // 添加批次指示器（如果有多批次数据）
    const totalItems = props.data.monthlyInspections.length
    if (totalItems > 6) {
        const totalBatches = Math.ceil(totalItems / 6)
        
        // 只有多于1页数据时才显示页码指示器
        if (totalBatches > 1) {
            options.title = {
                text: `${currentBatchIndex.value + 1}/${totalBatches}`,
                right: '4px',
                top: '4px',
                textStyle: {
                    color: 'rgba(255, 255, 255, 0.5)',
                    fontSize: 10, // 缩小字体
                    fontWeight: 'normal'
                }
            }
        }
    }
    
    // 设置图表配置
    try {
        mChart.setOption(options, true)
    } catch (error) {
        console.error('图表渲染失败:', error)
    }
}

// 格式化月份为更友好的显示格式
const formatMonth = (yearMonth) => {
    // 将 "2025-07" 转换为 "25年7月" 格式
    const [year, month] = yearMonth.split('-')
    return `${year.slice(2)}年${parseInt(month)}月` // 只显示年份的后两位
}

// 监听窗口大小变化，重新调整图表大小
const resizeHandler = () => {
    if (mChart) {
        mChart.resize()
    }
}
</script>

<style scoped>
</style>
