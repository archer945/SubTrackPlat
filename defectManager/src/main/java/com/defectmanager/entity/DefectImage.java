package com.defectmanager.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("defect_image")
public class DefectImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long defectId;        // 关联缺陷ID
    private String imageUrl;      // 图片地址
    private String thumbnailUrl;  // 缩略图地址
    private LocalDateTime uploadedAt; // 上传时间
}