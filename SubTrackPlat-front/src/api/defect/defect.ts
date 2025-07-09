import axios from 'axios'

// =============================================================
// 枚举
// =============================================================
export const enum DefectStatusEnum {
    PENDING        = '待确认',
    CONFIRMED      = '已确认',
    REJECTED       = '已驳回',
    PROCESSING     = '处理中',
    FIXED          = '已整改',
    REVIEW_NEEDED  = '需复查',
    CLOSED         = '已关闭',
}

export const enum DefectTypeEnum {
    STRUCTURAL_CRACK     = '结构裂缝',
    LEAKAGE              = '渗水',
    EQUIPMENT_FAILURE    = '设备故障',
    LIGHTING_ISSUE       = '照明问题',
    DETACHMENT           = '脱落',
    CORROSION            = '腐蚀',
    SEEPAGE              = '渗漏',
    EQUIPMENT_ABNORMALITY= '设备异常',
}

export const enum FoundMethodEnum {
    MANUAL_INSPECTION = '人工巡检',
    EQUIPMENT_DETECTION= '设备检测',
    ROUTINE_CHECK      = '例行检查',
}

export const enum SeverityLevelEnum {
    HIGH   = '高',
    MEDIUM = '中',
    LOW    = '低',
}

// =============================================================
// 通用接口类型
// =============================================================
export interface DefectQuery {
    current?: number
    size?: number
    status?: DefectStatusEnum | string
    type?: DefectTypeEnum   | string
    severity?: SeverityLevelEnum | string
    foundMethod?: FoundMethodEnum | string
    keyword?: string
    beginTime?: string
    endTime?: string
}

export interface Defect {
    id?: number
    code?: string
    title?: string
    description?: string
    location?: string
    longitude?: number
    latitude?: number
    status?: DefectStatusEnum | string
    severity?: SeverityLevelEnum | string
    type?: DefectTypeEnum | string
    foundMethod?: FoundMethodEnum | string
    foundTime?: string
    repairDeadline?: string
    costEstimate?: number
    images?: DefectImage[]
    // ……根据需要补充
}

export interface DefectImage {
    id: number
    defectId: number
    imageUrl: string
    thumbnailUrl: string
    uploadTime: string
}

export interface PageResult<T = any> {
    records: T[]
    current: number
    pages: number
    size: number
    total: number
}

// =============================================================
// 核心 API
// =============================================================
/** 分页查询缺陷列表 */
export function getDefectList(data: DefectQuery) {
    return axios.post<PageResult<Defect>>('/api/defects/page', data)
}

/** 新增缺陷 */
export function addDefect(data: Defect) {
    return axios.post<Defect>('/api/defects/add', data)
}


/** 删除缺陷 */
export function deleteDefect(id: number) {
    return axios.delete<boolean>(`/api/defects/${id}`)
}

/**
 * 更新缺陷状态
 * @param operatorId 操作人用户 ID（放在 header 里；可选）
 */
export function updateDefectStatus(
    id: number,
    status: DefectStatusEnum | string,
    operatorId?: number,
) {
    return axios.put<string>(`/api/defects/${id}/status`, undefined, {
        params: { status },
        headers: { 'X-User-Id': operatorId ?? 0 },
    })
}

/** 获取缺陷详情（含图片列表） */
export function getDefectDetail(id: number) {
    return axios.get<Defect>(`/api/defects/${id}`)
}

/**
 * 导出缺陷数据为 Excel (Blob)
 * 调用示例：exportDefects(query).then(blob => downloadFile(blob))
 */
export function exportDefects(params: DefectQuery) {
    return axios.get<Blob>('/api/defects/export', {
        params,
        responseType: 'blob',
    })
}

// =============================================================
// 👇 ★ 新增包装函数：标记“已整改” ★
// =============================================================
/**
 * 标记缺陷为「已整改」
 */
export function markDefectRectified(id: number, operatorId?: number) {
    return updateDefectStatus(id, DefectStatusEnum.FIXED, operatorId)
}

// =============================================================
// 图片上传接口
// =============================================================
export function uploadDefectImage(defectId: number, file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return axios.post<DefectImage[]>(`/api/defectimage/${defectId}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

export function uploadDefectImages(defectId: number, files: File[]) {
    const formData = new FormData()
    files.forEach((f) => formData.append('files', f))
    return axios.post<DefectImage[]>(`/api/defectimage/batch/${defectId}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
    })
}

