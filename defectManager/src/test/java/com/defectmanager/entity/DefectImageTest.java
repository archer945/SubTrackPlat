package com.defectmanager.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DefectImageTest {

    @Test
    void testNoArgsConstructor() {
        DefectImage defectImage = new DefectImage();
        assertNotNull(defectImage);
    }

    @Test
    void testAllArgsConstructor() {
        Long id = 1L;
        Long defectId = 100L;
        String imageUrl = "http://example.com/image.jpg";
        String thumbnailUrl = "http://example.com/thumbnail.jpg";
        LocalDateTime uploadedAt = LocalDateTime.now();

        DefectImage defectImage = new DefectImage(id, defectId, imageUrl, thumbnailUrl, uploadedAt);

        assertEquals(id, defectImage.getId());
        assertEquals(defectId, defectImage.getDefectId());
        assertEquals(imageUrl, defectImage.getImageUrl());
        assertEquals(thumbnailUrl, defectImage.getThumbnailUrl());
        assertEquals(uploadedAt, defectImage.getUploadedAt());
    }

    @Test
    void testGetterAndSetter() {
        DefectImage defectImage = new DefectImage();

        Long id = 1L;
        defectImage.setId(id);
        assertEquals(id, defectImage.getId());

        Long defectId = 100L;
        defectImage.setDefectId(defectId);
        assertEquals(defectId, defectImage.getDefectId());

        String imageUrl = "http://example.com/image.jpg";
        defectImage.setImageUrl(imageUrl);
        assertEquals(imageUrl, defectImage.getImageUrl());

        String thumbnailUrl = "http://example.com/thumbnail.jpg";
        defectImage.setThumbnailUrl(thumbnailUrl);
        assertEquals(thumbnailUrl, defectImage.getThumbnailUrl());

        LocalDateTime uploadedAt = LocalDateTime.now();
        defectImage.setUploadedAt(uploadedAt);
        assertEquals(uploadedAt, defectImage.getUploadedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        DefectImage image1 = new DefectImage();
        image1.setId(1L);
        image1.setDefectId(100L);
        image1.setImageUrl("http://example.com/image.jpg");

        DefectImage image2 = new DefectImage();
        image2.setId(1L);
        image2.setDefectId(100L);
        image2.setImageUrl("http://example.com/image.jpg");

        DefectImage image3 = new DefectImage();
        image3.setId(2L);
        image3.setDefectId(200L);
        image3.setImageUrl("http://example.com/other.jpg");

        // 测试equals
        assertEquals(image1, image1); // 自反性
        assertEquals(image1, image2); // 对称性
        assertNotEquals(image1, image3);
        assertNotEquals(image1, null);
        assertNotEquals(image1, new Object());

        // 测试hashCode
        assertEquals(image1.hashCode(), image2.hashCode());
    }

    @Test
    void testToString() {
        DefectImage defectImage = new DefectImage();
        defectImage.setId(1L);
        defectImage.setDefectId(100L);
        defectImage.setImageUrl("http://example.com/image.jpg");

        String toString = defectImage.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("defectId=100"));
        assertTrue(toString.contains("imageUrl=http://example.com/image.jpg"));
    }
} 