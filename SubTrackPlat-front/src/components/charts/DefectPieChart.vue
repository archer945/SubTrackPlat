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

// 组件挂载时初始化图表
onMounted(() => {
    if (chartRef.value) {
        // 初始化图表
        chart = echarts.init(chartRef.value);
        renderChart();
    }
});

// 组件销毁时释放图表实例
onBeforeUnmount(() => {
    if (chart) {
        chart.dispose();
        chart = null;
    }
    window.removeEventListener('resize', handleResize);
});

// 监听数据变化
watch(() => props.data, 
    (newVal) => {
        if (chart && newVal?.typeDistribution) {
            renderChart();
        }
    }, 
    { deep: true }
);

// 渲染图表
const renderChart = () => {
    if (!chart) {
        return;
    }
    
    if (!props.data || !props.data.typeDistribution || !Array.isArray(props.data.typeDistribution)) {
        console.error('缺陷类型分布数据无效:', props.data);
        return;
    }
    
    const typeData = props.data.typeDistribution;
    
    // 定义饼图颜色
    const colors = [
        '#2ec7c9', '#b6a2de', '#5ab1ef', '#ffb980', '#d87a80',
        '#8d98b3', '#e5cf0d', '#97b552', '#95706d', '#dc69aa'
    ];
    
    // 构建饼图配置
    const option = {
        tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)',
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            borderColor: 'rgba(0, 147, 255, 0.7)',
            borderWidth: 1,
            textStyle: {
                color: '#ffffff',
                fontSize: 12
            }
        },
        legend: {
            orient: 'vertical',
            left: '2%',
            top: 'middle',
            itemWidth: 10,
            itemHeight: 10,
            itemGap: 10, // 减小图例项之间的间距
            textStyle: {
                color: '#9eb1c8',
                fontSize: 12,
                rich: {
                    name: {
                        width: 60,
                        fontSize: 12
                    },
                    percent: {
                        fontSize: 12
                    }
                }
            },
            formatter: function(name) {
                // 查找对应的数据项
                const item = typeData.find(item => item.type === name);
                if (item) {
                    // 显示类型名称和百分比
                    return `${name} (${item.ratio.toFixed(1)}%)`;
                }
                return name;
            }
        },
        series: [
            {
                name: '缺陷类型',
                type: 'pie',
                radius: ['30%', '75%'], // 扩大环形图尺寸
                center: ['60%', '50%'], // 将饼图稍微向左移动
                avoidLabelOverlap: true,
                itemStyle: {
                    borderRadius: 6, // 增加圆角
                    borderColor: 'rgba(0, 0, 0, 0.15)',
                    borderWidth: 1.5
                },
                label: {
                    show: false
                },
                emphasis: {
                    label: {
                        show: true,
                        fontSize: 14, // 增加字体大小
                        fontWeight: 'bold',
                        formatter: '{b}: {d}%',
                        color: '#ffffff'
                    },
                    itemStyle: {
                        shadowBlur: 12,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 180, 255, 0.5)'
                    }
                },
                labelLine: {
                    show: false
                },
                data: typeData.map((item, index) => ({
                    name: item.type,
                    value: item.count,
                    itemStyle: {
                        color: colors[index % colors.length]
                    }
                }))
            }
        ],
        color: colors,
        backgroundColor: 'transparent',
        textStyle: {
            color: '#ffffff'
        },
        title: {
            text: '缺陷类型分布',
            left: '50%',  // 居中
            top: 5,
            textStyle: {
                color: '#9eb1c8',
                fontSize: 0,  // 设为0，不显示标题（因为已经在App.vue中有标题）
                fontWeight: 'normal'
            }
        }
    };
    
    // 设置图表配置
    try {
        chart.setOption(option, true);
    } catch (error) {
        console.error('渲染饼图失败:', error);
    }
};

// 处理窗口大小变化
const handleResize = () => {
    if (chart) {
        chart.resize();
    }
};

// 监听窗口大小变化
window.addEventListener('resize', handleResize);
</script>

<style scoped>
</style>
