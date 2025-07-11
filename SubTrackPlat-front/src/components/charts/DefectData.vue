<template>
    <div class="flex flex-col h-full">
        <!-- 顶部数字卡片 -->
        <div class="grid grid-cols-3 gap-2 mb-3">
            <div class="bg-blue-900/40 rounded-lg p-3 flex flex-col items-center backdrop-blur-sm border border-blue-500/20 shadow-lg shadow-blue-500/10">
                <div class="text-white text-2xl font-bold">{{ totalDefects }}</div>
                <div class="text-xs text-white/80">累计缺陷数</div>
            </div>
            <div class="bg-blue-900/40 rounded-lg p-3 flex flex-col items-center backdrop-blur-sm border border-blue-500/20 shadow-lg shadow-blue-500/10">
                <div class="text-white text-2xl font-bold">{{ confirmedDefects }}</div>
                <div class="text-xs text-white/80">确认缺陷数</div>
            </div>
            <div class="bg-blue-900/40 rounded-lg p-3 flex flex-col items-center backdrop-blur-sm border border-blue-500/20 shadow-lg shadow-blue-500/10">
                <div class="text-white text-2xl font-bold">{{ falseReportDefects }}</div>
                <div class="text-xs text-white/80">误检缺陷数</div>
            </div>
        </div>
        
        <!-- 双环形图和底部图例 -->
        <div class="flex-1 flex flex-col relative">
            <div ref="chartRef" class="flex-1"></div>
            
            <!-- 底部图例，固定在底部 -->
            <div class="absolute bottom-0 w-full flex justify-center items-center gap-5 mb-1">
                <div class="flex items-center">
                    <div class="w-3 h-3 rounded-full bg-blue-500 mr-2"></div>
                    <span class="text-white text-xs">确认缺陷</span>
                </div>
                <div class="flex items-center">
                    <div class="w-3 h-3 rounded-full bg-red-500 mr-2"></div>
                    <span class="text-white text-xs">误检缺陷</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount, computed } from 'vue';
import * as echarts from 'echarts';

const props = defineProps({
    data: {
        type: Object,
        required: true
    }
});

// 计算数据值
const totalDefects = computed(() => props.data?.totalDefects || 0);
const confirmedDefects = computed(() => {
    if (props.data?.validityDistribution) {
        const validItem = props.data.validityDistribution.find(item => item.isValid === true);
        return validItem ? validItem.count : 0;
    }
    return 0;
});
const falseReportDefects = computed(() => {
    if (props.data?.validityDistribution) {
        const invalidItem = props.data.validityDistribution.find(item => item.isValid === false);
        return invalidItem ? invalidItem.count : 0;
    }
    return 0;
});

// 计算百分比
const confirmedRate = computed(() => {
    return totalDefects.value > 0 ? (confirmedDefects.value / totalDefects.value * 100).toFixed(1) : 0;
});
const falseReportRate = computed(() => {
    return totalDefects.value > 0 ? (falseReportDefects.value / totalDefects.value * 100).toFixed(1) : 0;
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
        
        // 监听窗口大小变化
        window.addEventListener('resize', handleResize);
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
    () => {
        if (chart) {
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
    
    if (!props.data) {
        return;
    }
    
    // 构建双环形图配置
    const option = {
        tooltip: {
            trigger: 'item',
            formatter: function(params) {
                if (params.seriesName === '累计缺陷') {
                    return `<div style="padding: 8px;">
                            <div style="text-align:center;font-weight:bold;margin-bottom:5px;">累计缺陷数</div>
                            <div style="text-align:center;font-size:20px;color:#fff;">${totalDefects.value}</div>
                            </div>`;
                } else if (params.seriesName === '确认缺陷') {
                    return `<div style="padding: 8px;">
                            <div style="font-weight:bold;margin-bottom:5px;">确认缺陷</div>
                            <div>数量: ${confirmedDefects.value}</div>
                            <div>占比: ${confirmedRate.value}%</div>
                            </div>`;
                } else {
                    return `<div style="padding: 8px;">
                            <div style="font-weight:bold;margin-bottom:5px;">误检缺陷</div>
                            <div>数量: ${falseReportDefects.value}</div>
                            <div>占比: ${falseReportRate.value}%</div>
                            </div>`;
                }
            },
            backgroundColor: 'rgba(0, 20, 50, 0.8)',
            borderColor: 'rgba(0, 147, 255, 0.7)',
            borderWidth: 1,
            textStyle: {
                color: '#ffffff'
            },
            extraCssText: 'border-radius: 8px; box-shadow: 0 0 10px rgba(0, 147, 255, 0.3);'
        },
        legend: {
            show: false // 隐藏默认图例，使用自定义图例
        },
        series: [
            {
                name: '误检缺陷',
                type: 'pie',
                radius: ['70%', '85%'],
                center: ['50%', '40%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 6,
                    borderColor: 'rgba(0, 0, 0, 0.2)',
                    borderWidth: 1,
                    shadowBlur: 10,
                    shadowColor: 'rgba(255, 56, 56, 0.3)'
                },
                label: {
                    show: false
                },
                emphasis: {
                    scale: true,
                    scaleSize: 10,
                    itemStyle: {
                        shadowBlur: 20,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(255, 56, 56, 0.6)'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [
                    { 
                        value: falseReportDefects.value, 
                        name: '误检缺陷',
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#FF7070' },
                                { offset: 1, color: '#FF3838' }
                            ])
                        }
                    },
                    {
                        value: confirmedDefects.value,
                        name: '空白',
                        itemStyle: {
                            color: 'transparent'
                        },
                        tooltip: { show: false },
                        emphasis: { scale: false }
                    }
                ]
            },
            {
                name: '确认缺陷',
                type: 'pie',
                radius: ['50%', '65%'],
                center: ['50%', '40%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderRadius: 6,
                    borderColor: 'rgba(0, 0, 0, 0.2)',
                    borderWidth: 1,
                    shadowBlur: 10,
                    shadowColor: 'rgba(0, 137, 255, 0.3)'
                },
                label: {
                    show: false
                },
                emphasis: {
                    scale: true,
                    scaleSize: 10,
                    itemStyle: {
                        shadowBlur: 20,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 137, 255, 0.6)'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [
                    { 
                        value: confirmedDefects.value, 
                        name: '确认缺陷',
                        itemStyle: {
                            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: '#36DBFF' },
                                { offset: 1, color: '#0089FF' }
                            ])
                        }
                    },
                    {
                        value: falseReportDefects.value,
                        name: '空白',
                        itemStyle: {
                            color: 'transparent'
                        },
                        tooltip: { show: false },
                        emphasis: { scale: false }
                    }
                ]
            },
            {
                name: '累计缺陷',
                type: 'pie',
                radius: ['30%', '31%'],
                center: ['50%', '40%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    borderColor: 'rgba(0, 183, 255, 0.3)',
                    borderWidth: 2,
                    shadowBlur: 15,
                    shadowColor: 'rgba(0, 183, 255, 0.5)'
                },
                label: {
                    show: true,
                    position: 'center',
                    formatter: function() {
                        return `{a|${totalDefects.value}}\n{b|累计缺陷}`;
                    },
                    rich: {
                        a: {
                            color: '#ffffff',
                            fontSize: 20,
                            fontWeight: 'bold',
                            lineHeight: 30
                        },
                        b: {
                            color: 'rgba(255, 255, 255, 0.8)',
                            fontSize: 12,
                            lineHeight: 16
                        }
                    }
                },
                labelLine: {
                    show: false
                },
                data: [
                    { 
                        value: totalDefects.value, 
                        name: '累计缺陷',
                        itemStyle: {
                            color: new echarts.graphic.RadialGradient(0.5, 0.5, 0.5, [
                                { offset: 0, color: 'rgba(0, 183, 255, 0.9)' },
                                { offset: 1, color: 'rgba(0, 119, 255, 0.5)' }
                            ])
                        }
                    }
                ]
            }
        ],
        backgroundColor: 'transparent'
    };
    
    // 设置图表配置
    try {
        chart.setOption(option, true);
    } catch (error) {
        console.error('渲染双环形图失败:', error);
    }
};

// 处理窗口大小变化
const handleResize = () => {
    if (chart) {
        chart.resize();
    }
};
</script>

<style scoped>
/* 添加一些美观的过渡效果 */
.grid-cols-3 {
    transition: all 0.3s ease;
}

.bg-blue-900\/40 {
    position: relative;
    overflow: hidden;
}

.bg-blue-900\/40::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(0, 147, 255, 0.2), transparent);
    animation: shimmer 2s infinite;
}

@keyframes shimmer {
    100% {
        left: 100%;
    }
}
</style> 