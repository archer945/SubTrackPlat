package com.dashboard.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DefectImageTest {

    @Test
    void testDefectImageGettersAndSetters() {
        // 创建测试对象
        DefectImage defectImage = new DefectImage();
        
        // 设置所有字段值
        Long id = 1L;
        Long defectId = 101L;
        String imageUrl = "http://example.com/images/defect1.jpg";
        String thumbnailUrl = "http://example.com/images/defect1_thumb.jpg";
        LocalDateTime uploadedAt = LocalDateTime.now();
        
        defectImage.setId(id);
        defectImage.setDefectId(defectId);
        defectImage.setImageUrl(imageUrl);
        defectImage.setThumbnailUrl(thumbnailUrl);
        defectImage.setUploadedAt(uploadedAt);
        
        // 验证getter方法
        assertEquals(id, defectImage.getId());
        assertEquals(defectId, defectImage.getDefectId());
        assertEquals(imageUrl, defectImage.getImageUrl());
        assertEquals(thumbnailUrl, defectImage.getThumbnailUrl());
        assertEquals(uploadedAt, defectImage.getUploadedAt());
    }
    
    @Test
    void testAllArgsConstructor() {
        // 创建所有参数并测试全参构造函数
        Long id = 1L;
        Long defectId = 101L;
        String imageUrl = "http://example.com/images/defect1.jpg";
        String thumbnailUrl = "http://example.com/images/defect1_thumb.jpg";
        LocalDateTime uploadedAt = LocalDateTime.now();
        
        DefectImage defectImage = new DefectImage(id, defectId, imageUrl, thumbnailUrl, uploadedAt);
        
        // 验证所有字段
        assertEquals(id, defectImage.getId());
        assertEquals(defectId, defectImage.getDefectId());
        assertEquals(imageUrl, defectImage.getImageUrl());
        assertEquals(thumbnailUrl, defectImage.getThumbnailUrl());
        assertEquals(uploadedAt, defectImage.getUploadedAt());
    }
    
    @Test
    void testNoArgsConstructor() {
        // 测试无参构造函数
        DefectImage defectImage = new DefectImage();
        assertNotNull(defectImage);
    }
    
    @Test
    void testEqualsAndHashCode() {
        // 测试equals和hashCode方法
        DefectImage image1 = new DefectImage();
        image1.setId(1L);
        image1.setDefectId(101L);
        image1.setImageUrl("http://example.com/image1.jpg");
        image1.setThumbnailUrl("http://example.com/image1_thumb.jpg");
        image1.setUploadedAt(LocalDateTime.of(2025, 1, 1, 10, 30));
        
        DefectImage image2 = new DefectImage();
        image2.setId(1L);
        image2.setDefectId(101L);
        image2.setImageUrl("http://example.com/image1.jpg");
        image2.setThumbnailUrl("http://example.com/image1_thumb.jpg");
        image2.setUploadedAt(LocalDateTime.of(2025, 1, 1, 10, 30));
        
        DefectImage image3 = new DefectImage();
        image3.setId(2L);
        image3.setDefectId(102L);
        image3.setImageUrl("http://example.com/image2.jpg");
        image3.setThumbnailUrl("http://example.com/image2_thumb.jpg");
        image3.setUploadedAt(LocalDateTime.of(2025, 1, 2, 10, 30));
        
        // 测试equals方法
        assertEquals(image1, image2);
        assertNotEquals(image1, image3);
        assertNotEquals(image1, null);
        assertNotEquals(image1, new Object());
        
        // 测试自反性
        assertEquals(image1, image1);
        
        // 测试对称性
        assertEquals(image1.equals(image2), image2.equals(image1));
        
        // 测试传递性
        DefectImage image4 = new DefectImage();
        image4.setId(1L);
        image4.setDefectId(101L);
        image4.setImageUrl("http://example.com/image1.jpg");
        image4.setThumbnailUrl("http://example.com/image1_thumb.jpg");
        image4.setUploadedAt(LocalDateTime.of(2025, 1, 1, 10, 30));
        
        assertEquals(image1, image4);
        assertEquals(image2, image4);
        
        // 测试hashCode方法
        assertEquals(image1.hashCode(), image2.hashCode());
        assertNotEquals(image1.hashCode(), image3.hashCode());
        
        // 测试一致性
        assertEquals(image1.hashCode(), image1.hashCode());
    }
    
    @Test
    void testToString() {
        // 测试toString方法
        DefectImage defectImage = new DefectImage();
        defectImage.setId(1L);
        defectImage.setDefectId(101L);
        defectImage.setImageUrl("http://example.com/image1.jpg");
        defectImage.setThumbnailUrl("http://example.com/image1_thumb.jpg");
        LocalDateTime uploadTime = LocalDateTime.of(2025, 1, 1, 10, 30);
        defectImage.setUploadedAt(uploadTime);
        
        String toStringResult = defectImage.toString();
        
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("defectId=101"));
        assertTrue(toStringResult.contains("imageUrl=http://example.com/image1.jpg"));
        assertTrue(toStringResult.contains("thumbnailUrl=http://example.com/image1_thumb.jpg"));
        assertTrue(toStringResult.contains(uploadTime.toString()));
    }
    
    @Test
    void testNullValues() {
        // 测试null值处理
        DefectImage defectImage = new DefectImage();
        
        defectImage.setImageUrl(null);
        assertNull(defectImage.getImageUrl());
        
        defectImage.setThumbnailUrl(null);
        assertNull(defectImage.getThumbnailUrl());
        
        defectImage.setUploadedAt(null);
        assertNull(defectImage.getUploadedAt());
    }
    
    @Test
    void testEdgeCases() {
        // 测试边缘情况
        DefectImage defectImage = new DefectImage();
        
        // 测试极大值
        defectImage.setId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, defectImage.getId());
        
        defectImage.setDefectId(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, defectImage.getDefectId());
        
        // 测试空字符串
        defectImage.setImageUrl("");
        assertEquals("", defectImage.getImageUrl());
        
        defectImage.setThumbnailUrl("");
        assertEquals("", defectImage.getThumbnailUrl());
        
        // 测试最小和最大时间
        LocalDateTime minDate = LocalDateTime.of(1900, 1, 1, 0, 0);
        defectImage.setUploadedAt(minDate);
        assertEquals(minDate, defectImage.getUploadedAt());
        
        LocalDateTime maxDate = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
        defectImage.setUploadedAt(maxDate);
        assertEquals(maxDate, defectImage.getUploadedAt());
    }
} 