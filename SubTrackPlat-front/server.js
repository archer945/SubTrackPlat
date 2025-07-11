// 简单的开发服务器，用于代理API请求
const express = require('express')
const { createProxyMiddleware } = require('http-proxy-middleware')
const cors = require('cors')
const path = require('path')

const app = express()
const port = process.env.PORT || 3000

app.use(cors())
app.use(express.json())
app.use(express.static(path.join(__dirname, 'dist')))

// 允许所有跨域请求
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*')
  res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')
  next()
})

// 代理具体的API请求到后端8085端口
app.use('/dashboard/defect-overview', createProxyMiddleware({
  target: 'http://localhost:8085',
  changeOrigin: true,
  onProxyRes: (proxyRes, req, res) => {
    proxyRes.headers['Access-Control-Allow-Origin'] = '*'
  }
}))

app.use('/dashboard/defect-stats', createProxyMiddleware({
  target: 'http://localhost:8085',
  changeOrigin: true,
  onProxyRes: (proxyRes, req, res) => {
    proxyRes.headers['Access-Control-Allow-Origin'] = '*'
  }
}))

app.use('/dashboard/inspectSummary', createProxyMiddleware({
  target: 'http://localhost:8085',
  changeOrigin: true,
  onProxyRes: (proxyRes, req, res) => {
    proxyRes.headers['Access-Control-Allow-Origin'] = '*'
  }
}))

app.use('/dashboard/inspectTrend', createProxyMiddleware({
  target: 'http://localhost:8085',
  changeOrigin: true,
  onProxyRes: (proxyRes, req, res) => {
    proxyRes.headers['Access-Control-Allow-Origin'] = '*'
  }
}))

app.use('/dashboard/inspection-tasks', createProxyMiddleware({
  target: 'http://localhost:8085',
  changeOrigin: true,
  onProxyRes: (proxyRes, req, res) => {
    proxyRes.headers['Access-Control-Allow-Origin'] = '*'
  }
}))

// 处理所有其他请求，包括/dashboard路由，返回SPA入口
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'dist', 'index.html'))
})

// 提供模拟数据作为备用（当后端不可用时）
app.get('/mock-api/inspectSummary', (req, res) => {
  // 返回模拟数据
  res.json({
    "code": 10000,
    "message": "操作成功",
    "data": {
      "todayCount": 0,
      "yesterdayCount": 0,
      "growthRate": 0.0,
      "completedCount": 23,
      "completionRate": 0.0,
      "todayDistance": 0.0,
      "yesterdayDistance": 0.0,
      "distanceGrowthRate": null,
      "totalDistance": 6892.0,
      "todayInspect": [
        {
          "id": 20,
          "lineName": "1号线",
          "startPoint": "L站",
          "endPoint": "M站",
          "status": "已取消",
          "actualStart": "2025-07-04T09:05:00",
          "plannedEnd": "2025-07-29T11:00:00",
          "name": "俺是一个美国人"
        },
        {
          "id": 12,
          "lineName": "2号线",
          "startPoint": "J站",
          "endPoint": "K站",
          "status": "已完成",
          "actualStart": "2025-07-04T08:05:00",
          "plannedEnd": "2025-07-21T10:00:00",
          "name": "深情男大于永勋"
        },
        {
          "id": 15,
          "lineName": "3号线",
          "startPoint": "P站",
          "endPoint": "Q站",
          "status": "进行中",
          "actualStart": "2025-07-04T10:15:00",
          "plannedEnd": "2025-07-04T15:30:00",
          "name": "帅气总裁姬凯然"
        }
      ],
      "typeDistribution": [
        {
          "type": "维护",
          "count": 21,
          "ratio": null
        },
        {
          "type": "巡检",
          "count": 15,
          "ratio": null
        }
      ],
      "statusDistribution": [
        {
          "type": "维护",
          "count": 1,
          "ratio": null,
          "status": "已取消"
        },
        {
          "type": "巡检",
          "count": 5,
          "ratio": null,
          "status": "已完成"
        },
        {
          "type": "巡检",
          "count": 3,
          "ratio": null,
          "status": "进行中"
        },
        {
          "type": "巡检",
          "count": 1,
          "ratio": null,
          "status": "已暂停"
        },
        {
          "type": "巡检",
          "count": 6,
          "ratio": null,
          "status": "待执行"
        },
        {
          "type": "维护",
          "count": 18,
          "ratio": null,
          "status": "已完成"
        },
        {
          "type": "维护",
          "count": 2,
          "ratio": null,
          "status": "进行中"
        }
      ],
      "executorDistribution": [
        {
          "executorId": 8,
          "executorName": "帅气总裁姬凯然",
          "taskCount": 5
        },
        {
          "executorId": 6,
          "executorName": "深情男大于永勋",
          "taskCount": 16
        },
        {
          "executorId": 27,
          "executorName": "小羊崽",
          "taskCount": 1
        },
        {
          "executorId": 28,
          "executorName": "铝价新",
          "taskCount": 1
        },
        {
          "executorId": 25,
          "executorName": "宇将军",
          "taskCount": 1
        },
        {
          "executorId": 26,
          "executorName": "陈泽",
          "taskCount": 1
        },
        {
          "executorId": 7,
          "executorName": "俺是一个美国人",
          "taskCount": 11
        }
      ],
      "recentTrend": [],
      "totalInspections": 36,
      "monthlyInspections": [
        {
          "yearMonth": "2025-07",
          "count": 19
        },
        {
          "yearMonth": "2025-06",
          "count": 8
        },
        {
          "yearMonth": "2025-05",
          "count": 5
        },
        {
          "yearMonth": "2025-04",
          "count": 1
        },
        {
          "yearMonth": "2025-03",
          "count": 1
        },
        {
          "yearMonth": "2025-02",
          "count": 1
        },
        {
          "yearMonth": "2025-01",
          "count": 1
        }
      ]
    }
  });
});

// 添加一个模拟的地铁巡视任务API接口
app.get('/dashboard/inspectSummary', (req, res) => {
  // 返回模拟数据
  res.json({
    "code": 10000,
    "message": "操作成功",
    "data": {
      "todayCount": 6,
      "yesterdayCount": 4,
      "growthRate": 50.0,
      "completedCount": 23,
      "completionRate": 78.3,
      "todayDistance": 42.5,
      "yesterdayDistance": 35.2,
      "distanceGrowthRate": 20.7,
      "totalDistance": 6892.0,
      "todayInspect": [
        {
          "id": 20,
          "lineName": "1号线",
          "startPoint": "A站",
          "endPoint": "E站",
          "status": "进行中",
          "actualStart": "2025-07-04T09:05:00",
          "plannedEnd": "2025-07-04T14:00:00",
          "name": "俺是一个美国人"
        },
        {
          "id": 12,
          "lineName": "2号线",
          "startPoint": "J站",
          "endPoint": "K站",
          "status": "已完成",
          "actualStart": "2025-07-04T08:05:00",
          "plannedEnd": "2025-07-04T10:00:00",
          "name": "深情男大于永勋"
        },
        {
          "id": 15,
          "lineName": "3号线",
          "startPoint": "P站",
          "endPoint": "Q站",
          "status": "进行中",
          "actualStart": "2025-07-04T10:15:00",
          "plannedEnd": "2025-07-04T15:30:00",
          "name": "帅气总裁姬凯然"
        },
        {
          "id": 18,
          "lineName": "1号线",
          "startPoint": "C站",
          "endPoint": "G站",
          "status": "待执行",
          "actualStart": "2025-07-04T13:00:00",
          "plannedEnd": "2025-07-04T16:30:00",
          "name": "小羊崽"
        },
        {
          "id": 22,
          "lineName": "2号线",
          "startPoint": "H站",
          "endPoint": "L站",
          "status": "已取消",
          "actualStart": "2025-07-04T07:30:00",
          "plannedEnd": "2025-07-04T09:45:00",
          "name": "宇将军"
        },
        {
          "id": 25,
          "lineName": "3号线",
          "startPoint": "N站",
          "endPoint": "S站",
          "status": "已完成",
          "actualStart": "2025-07-04T06:00:00",
          "plannedEnd": "2025-07-04T11:30:00",
          "name": "陈泽"
        }
      ],
      "typeDistribution": [
        {
          "type": "维护",
          "count": 21,
          "ratio": null
        },
        {
          "type": "巡检",
          "count": 15,
          "ratio": null
        }
      ],
      "statusDistribution": [
        {
          "type": "维护",
          "count": 1,
          "ratio": null,
          "status": "已取消"
        },
        {
          "type": "巡检",
          "count": 5,
          "ratio": null,
          "status": "已完成"
        },
        {
          "type": "巡检",
          "count": 3,
          "ratio": null,
          "status": "进行中"
        },
        {
          "type": "巡检",
          "count": 1,
          "ratio": null,
          "status": "已暂停"
        },
        {
          "type": "巡检",
          "count": 6,
          "ratio": null,
          "status": "待执行"
        },
        {
          "type": "维护",
          "count": 18,
          "ratio": null,
          "status": "已完成"
        },
        {
          "type": "维护",
          "count": 2,
          "ratio": null,
          "status": "进行中"
        }
      ],
      "executorDistribution": [
        {
          "executorId": 8,
          "executorName": "帅气总裁姬凯然",
          "taskCount": 5
        },
        {
          "executorId": 6,
          "executorName": "深情男大于永勋",
          "taskCount": 16
        },
        {
          "executorId": 27,
          "executorName": "小羊崽",
          "taskCount": 1
        },
        {
          "executorId": 28,
          "executorName": "铝价新",
          "taskCount": 1
        },
        {
          "executorId": 25,
          "executorName": "宇将军",
          "taskCount": 1
        },
        {
          "executorId": 26,
          "executorName": "陈泽",
          "taskCount": 1
        },
        {
          "executorId": 7,
          "executorName": "俺是一个美国人",
          "taskCount": 11
        }
      ],
      "recentTrend": [],
      "totalInspections": 36,
      "monthlyInspections": [
        {
          "yearMonth": "2025-07",
          "count": 19
        },
        {
          "yearMonth": "2025-06",
          "count": 8
        },
        {
          "yearMonth": "2025-05",
          "count": 5
        },
        {
          "yearMonth": "2025-04",
          "count": 1
        },
        {
          "yearMonth": "2025-03",
          "count": 1
        },
        {
          "yearMonth": "2025-02",
          "count": 1
        },
        {
          "yearMonth": "2025-01",
          "count": 1
        }
      ]
    }
  });
});

// 添加缺陷统计接口
app.get('/dashboard/defect-stats', (req, res) => {
  res.json({
    "code": 10000,
    "message": "操作成功",
    "data": {
      "totalDefects": 153,
      "newDefectsToday": 12,
      "resolvedToday": 8,
      "criticalDefects": 15,
      "typeDistribution": [
        {
          "type": "轨道变形",
          "count": 42,
          "percentage": 27.5
        },
        {
          "type": "设备故障",
          "count": 38,
          "percentage": 24.8
        },
        {
          "type": "隧道渗水",
          "count": 25,
          "percentage": 16.3
        },
        {
          "type": "电气系统",
          "count": 20,
          "percentage": 13.1
        },
        {
          "type": "其他问题",
          "count": 28,
          "percentage": 18.3
        }
      ],
      "severityDistribution": [
        {
          "severity": "严重",
          "count": 15,
          "percentage": 9.8
        },
        {
          "severity": "中度",
          "count": 48,
          "percentage": 31.4
        },
        {
          "severity": "轻微",
          "count": 90,
          "percentage": 58.8
        }
      ],
      "monthlyTrend": [
        {
          "month": "2025-01",
          "count": 8
        },
        {
          "month": "2025-02",
          "count": 12
        },
        {
          "month": "2025-03",
          "count": 15
        },
        {
          "month": "2025-04",
          "count": 10
        },
        {
          "month": "2025-05",
          "count": 18
        },
        {
          "month": "2025-06",
          "count": 25
        },
        {
          "month": "2025-07",
          "count": 20
        }
      ],
      "lineDistribution": [
        {
          "line": "1号线",
          "count": 58,
          "percentage": 37.9
        },
        {
          "line": "2号线",
          "count": 45,
          "percentage": 29.4
        },
        {
          "line": "3号线",
          "count": 50,
          "percentage": 32.7
        }
      ]
    }
  });
});

// 添加巡视任务详情接口
app.get('/dashboard/inspection-tasks', (req, res) => {
  res.json({
    "code": 10000,
    "message": "操作成功",
    "data": {
      "tasks": [
        {
          "id": 1001,
          "name": "1号线A至G段巡检",
          "startStation": "A站",
          "endStation": "G站",
          "lineId": 1,
          "lineName": "1号线",
          "status": "进行中",
          "progress": 0.65,
          "inspector": "俺是一个美国人",
          "startTime": "2025-07-04T08:30:00",
          "estimatedEndTime": "2025-07-04T12:30:00"
        },
        {
          "id": 1002,
          "name": "2号线H至M段巡检",
          "startStation": "H站",
          "endStation": "M站",
          "lineId": 2,
          "lineName": "2号线",
          "status": "待开始",
          "progress": 0,
          "inspector": "深情男大于永勋",
          "startTime": "2025-07-04T13:00:00",
          "estimatedEndTime": "2025-07-04T17:00:00"
        },
        {
          "id": 1003,
          "name": "3号线N至S段巡检",
          "startStation": "N站",
          "endStation": "S站",
          "lineId": 3,
          "lineName": "3号线",
          "status": "已完成",
          "progress": 1,
          "inspector": "帅气总裁姬凯然",
          "startTime": "2025-07-03T09:00:00",
          "estimatedEndTime": "2025-07-03T14:00:00"
        }
      ]
    }
  });
});

// 启动服务器
app.listen(port, () => {
  console.log(`服务器运行在 http://localhost:${port}，代理到后端 http://localhost:8080`);
}); 