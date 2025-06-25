package com.defectmanager.service;

import com.defectmanager.entity.DefectImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {
    /**
     * 上传单张图片
     */
    DefectImage uploadImage(Long defectId, MultipartFile file) throws IOException, IOException;

    /**
     * 批量上传图片
     */
//    List<DefectImage> batchUploadImages(Long defectId, List<MultipartFile> files);

    /**
     * 获取缺陷关联的图片
     */
//    List<DefectImage> getImagesByDefectId(Long defectId);





}
