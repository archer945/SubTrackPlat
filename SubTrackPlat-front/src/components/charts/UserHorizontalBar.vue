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
console.log('接收到的数据:', props.data);

// 当前显示的批次索引
const currentBatchIndex = ref(0);
// 计时器
let rotationTimer = null;
// 存储所有数据的最大值
const allDataMaxValue = ref(0);

//1.初始化 echarts 实例
let mChart = null;
const target = ref(null)
onMounted(() => {
   if (target.value) {
       mChart = echarts.init(target.value)
       renderChart()
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
})

// 启动数据轮播
const startDataRotation = (allData) => {
    // 如果数据少于等于5条，就不需要轮播
    if (allData.length <= 5) {
        return
    }
    
    // 清除可能存在的旧定时器
    if (rotationTimer) {
        clearInterval(rotationTimer)
    }
    
    // 设置定时器，每3秒切换一次显示的数据批次
    rotationTimer = setInterval(() => {
        // 计算总批次数（向上取整）
        const totalBatches = Math.ceil(allData.length / 5)
        
        // 更新当前批次索引，循环显示
        currentBatchIndex.value = (currentBatchIndex.value + 1) % totalBatches
        
        // 重新渲染图表
        renderChart()
    }, 3000)
}

//2.构建 option 配置对象
watch(() => props.data, 
      (newVal) => {
          console.log('数据变化:', newVal)
          if (mChart && newVal?.executorDistribution) {
              // 计算后端返回的所有数据的最大值
              if (Array.isArray(newVal.executorDistribution) && newVal.executorDistribution.length > 0) {
                  allDataMaxValue.value = Math.max(...newVal.executorDistribution.map(item => item.taskCount || 0));
                  console.log('计算得到的所有数据最大值:', allDataMaxValue.value);
              }
              
              // 重置批次索引
              currentBatchIndex.value = 0
              
              // 渲染第一批数据
              renderChart()
              
              // 启动数据轮播
              startDataRotation(newVal.executorDistribution)
          }
      },
      { deep: true }
)

const renderChart = () => {
    console.log('尝试渲染图表，数据:', props.data)
    
    if (!mChart) {
        console.error('图表实例未初始化')
        return
    }
    
    if (!props.data) {
        console.error('未提供数据')
        return
    }
    
    if (!props.data.executorDistribution) {
        console.error('数据中缺少executorDistribution字段:', props.data)
        return
    }
    
    if (!Array.isArray(props.data.executorDistribution) || props.data.executorDistribution.length === 0) {
        console.warn('executorDistribution不是数组或为空数组:', props.data.executorDistribution)
        return
    }
    
    console.log('开始渲染图表，executorDistribution:', props.data.executorDistribution)
    
    // 获取当前应该显示的数据批次
    const allData = props.data.executorDistribution
    const pageSize = 5  // 每页显示5条数据
    const startIndex = currentBatchIndex.value * pageSize
    const currentPageData = allData.slice(startIndex, startIndex + pageSize)
    
    // 如果当前批次没有数据，重置为第一批次
    if (currentPageData.length === 0) {
        currentBatchIndex.value = 0
        const resetPageData = allData.slice(0, pageSize)
        updateChart(resetPageData)
        return
    }
    
    updateChart(currentPageData)
}

// 更新图表的函数
const updateChart = (displayData) => {
    const options = {
        xAxis: {
            show: false,
            type: 'value',
            max: function (value) {
                // 使用所有数据的最大值计算x轴上限，而不仅是当前显示的数据
                return parseInt(allDataMaxValue.value * 1.3)
            }
        },
        yAxis: {
            type: 'category',
            data: displayData.map((item) => item.executorName),
            inverse: true,
            axisLine: {
                show: false
            },
            axisTick:{
                show: false
            },
            axisLabel: {
                color: '#9eb1c8'
            }
        },
        grid: {
            top: 15,
            right: 70, // 增加右侧空间以容纳数值标签
            bottom: 15,
            left: 5, // 减少左侧空白
            containLabel: true
        },
        series: [
            {
                type: 'bar',
                data: displayData.map((item) => ({
                    name: item.executorName,
                    value: item.taskCount
                })),
                showBackground: true,
                backgroundStyle: {
                    color: 'rgba(180,180,180,0.2)'
                },
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                        { offset: 0, color: 'rgba(0, 147, 255, 0.8)' },
                        { offset: 1, color: 'rgba(0, 219, 255, 0.8)' }
                    ]),
                    barBorderRadius: 6,
                    shadowColor: 'rgba(0, 147, 255, 0.5)',
                    shadowBlur: 10
                },
                barWidth: 16, // 增加条形图宽度
                barGap: '40%',
                label: {
                    show: true,
                    position: 'right',
                    formatter: '{c}',
                    distance: 15, // 增加标签与条形图的距离
                    textStyle: {
                        color: '#ffffff',
                        fontSize: 14,
                        fontWeight: 'bold'
                    }
                }
            }
        ],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            },
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            borderColor: 'rgba(0, 147, 255, 0.7)',
            borderWidth: 1,
            textStyle: {
                color: '#ffffff'
            }
        },
        animation: true,
        animationDuration: 800,
        animationEasing: 'cubicOut'
    }
    
    // 添加批次指示器（如果有多批次数据）
    const totalItems = props.data.executorDistribution.length
    if (totalItems > 5) {
        const totalBatches = Math.ceil(totalItems / 5)
        
        // 只有多于1页数据时才显示页码指示器
        if (totalBatches > 1) {
            options.title = {
                text: `${currentBatchIndex.value + 1}/${totalBatches}`,
                right: '10px',
                top: '0',
                textStyle: {
                    color: 'rgba(255, 255, 255, 0.5)',
                    fontSize: 12,
                    fontWeight: 'normal'
                }
            }
        }
    }
    
    // 设置图表配置
    try {
        mChart.setOption(options, true) // 使用true参数完全覆盖之前的设置
        console.log('图表渲染成功')
    } catch (error) {
        console.error('图表渲染失败:', error)
    }
}

// 监听窗口大小变化，重新调整图表大小
const resizeHandler = () => {
    if (mChart) {
        mChart.resize()
    }
}

window.addEventListener('resize', resizeHandler)

// 确保在组件卸载时移除事件监听
onBeforeUnmount(() => {
    window.removeEventListener('resize', resizeHandler)
})

</script>

<style lang="scss" scoped>
</style>

