package com.defectmanager.query;

import com.common.domain.query.PageQuery;
import com.defectmanager.enmu.DefectStatusEnum;
import com.defectmanager.enmu.DefectTypeEnum;
import com.defectmanager.enmu.SeverityLevelEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
    public class DefectQuery extends PageQuery {
        private String type;
        private String status;
        private String severity;
        private Boolean isValid;
        private String taskId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public DefectTypeEnum getTypeEnum() {
        return type != null ? DefectTypeEnum.fromDbValue(type) : null;
    }

    public
    DefectStatusEnum getStatusEnum() {
        return status != null ? DefectStatusEnum.fromDbValue(status) : null;
    }

    public SeverityLevelEnum getSeverityEnum() {
        return severity != null ? SeverityLevelEnum.fromDbValue(severity) : null;
    }

}

