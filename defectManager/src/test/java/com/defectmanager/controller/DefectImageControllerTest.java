package com.defectmanager.controller;

import com.common.domain.vo.JsonVO;
import com.defectmanager.entity.DefectImage;
import com.defectmanager.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DefectImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private DefectImageController defectImageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadImage() throws IOException {
        // 准备测试数据
        Long defectId = 1L;
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );
        
        DefectImage image = new DefectImage();
        image.setId(1L);
        image.setDefectId(defectId);
        image.setImageUrl("http://example.com/test.jpg");
        image.setUploadedAt(LocalDateTime.now());
        
        // 模拟依赖方法的行为
        when(imageService.uploadImage(eq(defectId), any(MultipartFile.class))).thenReturn(image);
        
        // 执行测试
        JsonVO<DefectImage> result = defectImageController.uploadImage(defectId, file);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(10000, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        assertEquals(defectId, result.getData().getDefectId());
        assertEquals("http://example.com/test.jpg", result.getData().getImageUrl());
        
        verify(imageService).uploadImage(eq(defectId), any(MultipartFile.class));
    }

    @Test
    void testUploadImages() throws IOException {
        // 准备测试数据
        Long defectId = 1L;
        MockMultipartFile file1 = new MockMultipartFile("file1", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file2", "test2.jpg", "image/jpeg", "test image 2".getBytes());
        MultipartFile[] files = {file1, file2};
        
        List<DefectImage> images = new ArrayList<>();
        DefectImage image1 = new DefectImage();
        image1.setId(1L);
        image1.setDefectId(defectId);
        image1.setImageUrl("http://example.com/test1.jpg");
        image1.setUploadedAt(LocalDateTime.now());
        
        DefectImage image2 = new DefectImage();
        image2.setId(2L);
        image2.setDefectId(defectId);
        image2.setImageUrl("http://example.com/test2.jpg");
        image2.setUploadedAt(LocalDateTime.now());
        
        images.add(image1);
        images.add(image2);
        
        // 模拟依赖方法的行为
        when(imageService.batchUploadImages(eq(defectId), anyList())).thenReturn(images);
        
        // 执行测试
        JsonVO<List<DefectImage>> result = defectImageController.uploadImages(defectId, files);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(10000, result.getCode());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        assertEquals("http://example.com/test1.jpg", result.getData().get(0).getImageUrl());
        assertEquals("http://example.com/test2.jpg", result.getData().get(1).getImageUrl());
        
        verify(imageService).batchUploadImages(eq(defectId), anyList());
    }
} 