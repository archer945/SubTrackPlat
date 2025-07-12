package com.defectmanager.service;

import com.defectmanager.entity.DefectImage;
import com.defectmanager.mapper.DefectImageMapper;
import com.defectmanager.mapper.DefectMapper;
import com.defectmanager.service.impl.ImageServiceImpl;
import com.defectmanager.utils.AliOSSUtils;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Mock
    private DefectImageMapper imageMapper;
    
    @Mock
    private DefectMapper defectMapper;
    
    @Mock
    private AliOSSUtils ossUtils;
    
    @InjectMocks
    private ImageServiceImpl imageService;
    
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
        
        // 模拟依赖方法的行为
        when(ossUtils.upload(any(MultipartFile.class))).thenReturn("http://example.com/test.jpg");
        when(imageMapper.insert(any(DefectImage.class))).thenReturn(1);
        
        // 执行测试
        DefectImage result = imageService.uploadImage(defectId, file);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(defectId, result.getDefectId());
        assertEquals("http://example.com/test.jpg", result.getImageUrl());
        assertEquals("http://example.com/test.jpg?x-oss-process=image/resize,h_200", result.getThumbnailUrl());
        assertNotNull(result.getUploadedAt());
        
        verify(ossUtils).upload(any(MultipartFile.class));
        verify(imageMapper).insert(any(DefectImage.class));
    }
    
    @Test
    void testBatchUploadImages() throws IOException {
        // 准备测试数据
        Long defectId = 1L;
        MockMultipartFile file1 = new MockMultipartFile("file1", "test1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file2", "test2.jpg", "image/jpeg", "test image 2".getBytes());
        List<MultipartFile> files = Arrays.asList(file1, file2);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(new com.defectmanager.entity.Defect());
        when(ossUtils.upload(any(MultipartFile.class)))
            .thenReturn("http://example.com/test1.jpg")
            .thenReturn("http://example.com/test2.jpg");
        when(imageMapper.insert(any(DefectImage.class))).thenReturn(1);
        
        // 执行测试
        List<DefectImage> results = imageService.batchUploadImages(defectId, files);
        
        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("http://example.com/test1.jpg", results.get(0).getImageUrl());
        assertEquals("http://example.com/test2.jpg", results.get(1).getImageUrl());
        
        verify(defectMapper).selectById(defectId);
        verify(ossUtils, times(2)).upload(any(MultipartFile.class));
        verify(imageMapper, times(2)).insert(any(DefectImage.class));
    }
    
    @Test
    void testBatchUploadImagesWithInvalidDefectId() throws IOException {
        // 准备测试数据
        Long defectId = 999L;
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());
        List<MultipartFile> files = Arrays.asList(file);
        
        // 模拟依赖方法的行为
        when(defectMapper.selectById(defectId)).thenReturn(null);
        
        // 执行测试并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageService.batchUploadImages(defectId, files);
        });
        
        assertTrue(exception.getMessage().contains("不存在"));
        verify(defectMapper).selectById(defectId);
        verify(ossUtils, never()).upload(any(MultipartFile.class));
        verify(imageMapper, never()).insert(any(DefectImage.class));
    }
    
    @Test
    void testGetImagesByDefectId() {
        // 准备测试数据
        Long defectId = 1L;
        List<DefectImage> expectedImages = new ArrayList<>();
        DefectImage image1 = new DefectImage();
        image1.setId(1L);
        image1.setDefectId(defectId);
        image1.setImageUrl("http://example.com/image1.jpg");
        image1.setUploadedAt(LocalDateTime.now());
        
        DefectImage image2 = new DefectImage();
        image2.setId(2L);
        image2.setDefectId(defectId);
        image2.setImageUrl("http://example.com/image2.jpg");
        image2.setUploadedAt(LocalDateTime.now());
        
        expectedImages.add(image1);
        expectedImages.add(image2);
        
        // 模拟依赖方法的行为
        when(imageMapper.selectByDefectId(defectId)).thenReturn(expectedImages);
        
        // 执行测试
        List<DefectImage> results = imageService.getImagesByDefectId(defectId);
        
        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("http://example.com/image1.jpg", results.get(0).getImageUrl());
        assertEquals("http://example.com/image2.jpg", results.get(1).getImageUrl());
        
        verify(imageMapper).selectByDefectId(defectId);
    }
    
    @Test
    void testGetImagesByDefectIdEmpty() {
        // 准备测试数据
        Long defectId = 999L;
        
        // 模拟依赖方法的行为
        when(imageMapper.selectByDefectId(defectId)).thenReturn(new ArrayList<>());
        
        // 执行测试
        List<DefectImage> results = imageService.getImagesByDefectId(defectId);
        
        // 验证结果
        assertNotNull(results);
        assertTrue(results.isEmpty());
        
        verify(imageMapper).selectByDefectId(defectId);
    }
} 