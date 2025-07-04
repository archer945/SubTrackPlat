package com.defectmanager.service.impl;

import com.defectmanager.entity.DefectImage;
import com.defectmanager.mapper.DefectImageMapper;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.service.ImageService;
import com.defectmanager.utils.AliOSSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private AliOSSUtils ossUtils;
    @Autowired
    private DefectImageMapper imageMapper;
    @Autowired
    private DefectMapper defectMapper;

    /*
    * 上传单张图片
    * */
    @Override
    public DefectImage uploadImage(Long defectId, MultipartFile file) throws IOException {
        // 1. 上传到OSS
        String imageUrl = ossUtils.upload(file);

        // 2. 构建缩略图URL（简单处理，实际可用OSS图片处理服务）
        String thumbnailUrl = imageUrl + "?x-oss-process=image/resize,h_200";

        // 3. 保存记录
        DefectImage image = new DefectImage();
        image.setDefectId(defectId);
        image.setImageUrl(imageUrl);
        image.setThumbnailUrl(thumbnailUrl);
        image.setUploadedAt(LocalDateTime.now());

        imageMapper.insert(image);
        return image;
    }


    /*
    * 批量上传图片
    * */
    @Override
    @Transactional
    public List<DefectImage> batchUploadImages(Long defectId, List<MultipartFile> files) {
        // 先验证缺陷是否存在
        if (defectMapper.selectById(defectId) == null) {
            throw new RuntimeException("缺陷ID " + defectId + " 不存在");
        }

        return files.stream()
                .map(file -> {
                    try {
                        return uploadImage(defectId, file);
                    } catch (IOException e) {
                        throw new RuntimeException("图片上传失败: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());
    }


    /*
    * 根据id获取图片
    * */
    @Override
    public List<DefectImage> getImagesByDefectId(Long defectId) {
        return imageMapper.selectByDefectId(defectId);
    }

}
