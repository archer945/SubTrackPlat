package com.defectmanager.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AliOSSUtilsTest {

    @InjectMocks
    private AliOSSUtils aliOSSUtils;

    @BeforeEach
    void setUp() {
        // 设置私有字段的值
        ReflectionTestUtils.setField(aliOSSUtils, "endpoint", "https://oss-cn-hangzhou.aliyuncs.com");
        ReflectionTestUtils.setField(aliOSSUtils, "accessKeyId", "test-access-key-id");
        ReflectionTestUtils.setField(aliOSSUtils, "accessKeySecret", "test-access-key-secret");
        ReflectionTestUtils.setField(aliOSSUtils, "bucketName", "test-bucket");
    }

    @Test
    void testUploadWithRealImplementation() throws IOException {
        // 测试上传功能的实际实现
        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.jpg",
                "image/jpeg", 
                "test image content".getBytes()
        );
        
        // 模拟UUID生成
        UUID mockUUID = UUID.fromString("12345678-1234-1234-1234-123456789012");
        try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(mockUUID);
            
            // 使用MockedConstruction模拟OSSClientBuilder
            try (MockedConstruction<OSSClientBuilder> mockedBuilder = Mockito.mockConstruction(OSSClientBuilder.class, 
                    (mock, context) -> {
                        OSS ossClient = mock(OSS.class);
                        when(mock.build(anyString(), anyString(), anyString())).thenReturn(ossClient);
                    })) {
                
                // 执行测试
                String url = aliOSSUtils.upload(file);
                
                // 验证结果
                assertEquals("https://test-bucket.oss-cn-hangzhou.aliyuncs.com/12345678-1234-1234-1234-123456789012.jpg", url);
                
                // 验证OSS客户端的方法调用
                OSSClientBuilder mockBuilder = mockedBuilder.constructed().get(0);
                OSS mockOssClient = mockBuilder.build("", "", "");
                verify(mockOssClient).putObject(eq("test-bucket"), eq("12345678-1234-1234-1234-123456789012.jpg"), any(InputStream.class));
                verify(mockOssClient).shutdown();
            }
        }
    }

    @Test
    void testUploadWithIOException() throws IOException {
        // 测试上传时发生IOException的情况
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("模拟IO异常"));
        
        // 验证异常被正确传播
        assertThrows(IOException.class, () -> aliOSSUtils.upload(file));
    }

    @Test
    void testUploadWithNullOriginalFilename() throws IOException {
        // 测试原始文件名为null的情况
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        
        // 模拟UUID生成
        UUID mockUUID = UUID.fromString("12345678-1234-1234-1234-123456789012");
        try (MockedStatic<UUID> mockedUUID = Mockito.mockStatic(UUID.class)) {
            mockedUUID.when(UUID::randomUUID).thenReturn(mockUUID);
            
            // 使用MockedConstruction模拟OSSClientBuilder
            try (MockedConstruction<OSSClientBuilder> mockedBuilder = Mockito.mockConstruction(OSSClientBuilder.class, 
                    (mock, context) -> {
                        OSS ossClient = mock(OSS.class);
                        when(mock.build(anyString(), anyString(), anyString())).thenReturn(ossClient);
                    })) {
                
                // 验证异常被正确处理
                assertThrows(NullPointerException.class, () -> aliOSSUtils.upload(file));
            }
        }
    }

    @Test
    void testDeleteWithHttpUrl() {
        // 测试删除带http前缀的URL
        try (MockedConstruction<OSSClientBuilder> mockedBuilder = Mockito.mockConstruction(OSSClientBuilder.class, 
                (mock, context) -> {
                    OSS ossClient = mock(OSS.class);
                    when(mock.build(anyString(), anyString(), anyString())).thenReturn(ossClient);
                })) {
            
            // 执行测试
            String fileUrl = "https://test-bucket.oss-cn-hangzhou.aliyuncs.com/test-file.jpg";
            boolean result = aliOSSUtils.delete(fileUrl);
            
            // 验证结果
            assertTrue(result);
            
            // 验证OSS客户端的方法调用
            OSSClientBuilder mockBuilder = mockedBuilder.constructed().get(0);
            OSS mockOssClient = mockBuilder.build("", "", "");
            verify(mockOssClient).deleteObject(eq("test-bucket"), eq("test-file.jpg"));
            verify(mockOssClient).shutdown();
        }
    }

    @Test
    void testDeleteWithObjectKey() {
        // 测试直接使用对象键删除
        try (MockedConstruction<OSSClientBuilder> mockedBuilder = Mockito.mockConstruction(OSSClientBuilder.class, 
                (mock, context) -> {
                    OSS ossClient = mock(OSS.class);
                    when(mock.build(anyString(), anyString(), anyString())).thenReturn(ossClient);
                })) {
            
            // 执行测试
            String objectKey = "test-file.jpg";
            boolean result = aliOSSUtils.delete(objectKey);
            
            // 验证结果
            assertTrue(result);
            
            // 验证OSS客户端的方法调用
            OSSClientBuilder mockBuilder = mockedBuilder.constructed().get(0);
            OSS mockOssClient = mockBuilder.build("", "", "");
            verify(mockOssClient).deleteObject(eq("test-bucket"), eq("test-file.jpg"));
            verify(mockOssClient).shutdown();
        }
    }

    @Test
    void testDeleteWithException() {
        // 测试删除时发生异常的情况
        try (MockedConstruction<OSSClientBuilder> mockedBuilder = Mockito.mockConstruction(OSSClientBuilder.class, 
                (mock, context) -> {
                    OSS ossClient = mock(OSS.class);
                    doThrow(new RuntimeException("模拟删除异常")).when(ossClient).deleteObject(anyString(), anyString());
                    when(mock.build(anyString(), anyString(), anyString())).thenReturn(ossClient);
                })) {
            
            // 执行测试
            String fileUrl = "test-file.jpg";
            boolean result = aliOSSUtils.delete(fileUrl);
            
            // 验证结果
            assertFalse(result);
            
            // 验证OSS客户端的方法调用
            OSSClientBuilder mockBuilder = mockedBuilder.constructed().get(0);
            OSS mockOssClient = mockBuilder.build("", "", "");
            verify(mockOssClient).shutdown();
        }
    }

    @Test
    void testDeleteWithNullOssClient() {
        // 测试OSS客户端为null的情况
        try (MockedConstruction<OSSClientBuilder> mockedBuilder = Mockito.mockConstruction(OSSClientBuilder.class, 
                (mock, context) -> {
                    when(mock.build(anyString(), anyString(), anyString())).thenReturn(null);
                })) {
            
            // 执行测试
            String fileUrl = "test-file.jpg";
            boolean result = aliOSSUtils.delete(fileUrl);
            
            // 验证结果 - 应该返回false，因为ossClient为null会导致NullPointerException
            assertFalse(result);
        }
    }
} 