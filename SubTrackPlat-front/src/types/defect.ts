export interface DefectQueryParams {
    pageIndex?: number
    pageSize?: number
    taskName?: string
    type?: string
    isValid?: string
    severity?: string
    startTime?: string
    endTime?: string
}

export interface Defect {
    id: string
    code: string
    type: string
    description: string
    severity: string
    isValid: boolean
    defectLength?: number
    defectArea?: number
    defectCount?: number
    suggestion?: string
    foundTime: string
    foundBy: string
    taskId: string
    taskName: string
    foundMethod: string
    location: number
    status: string
    confirmBy?: string
    confirmTime?: string
    handleBy?: string
    handleStart?: string
    handleEnd?: string
    result?: string
    createTime: string
    updateTime: string
    images: DefectImage[]
}

export interface DefectImage {
    id: string
    defectId: string
    imageUrl: string
    thumbnailUrl: string
    uploadedAt: string
}