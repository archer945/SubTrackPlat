package com.systemManager.config;

import com.common.domain.vo.JsonVO;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testExceptionHandler_HttpMediaTypeException() {
        // 创建异常
        HttpMediaTypeNotSupportedException exception = new HttpMediaTypeNotSupportedException("不支持的媒体类型");
        
        // 调用处理方法
        JsonVO<String> response = exceptionHandler.exceptionHandler(exception);
        
        // 验证结果
        assertEquals(9996, response.getCode());
        assertEquals("不支持的媒体类型", response.getData());
    }
    
    @Test
    void testExceptionHandler_GenericException() {
        // 创建异常
        RuntimeException exception = new RuntimeException("服务器错误");
        
        // 调用处理方法
        JsonVO<String> response = exceptionHandler.exceptionHandler(exception);
        
        // 验证结果
        assertEquals(9994, response.getCode());
        assertEquals("服务器错误", response.getData());
    }
    
    @Test
    void testMethodArgumentNotValidHandler_MethodArgumentNotValidException() {
        // 模拟绑定结果
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "错误消息");
        when(bindingResult.getFieldError()).thenReturn(fieldError);
        
        // 创建异常
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        // 调用处理方法
        JsonVO<String> response = exceptionHandler.methodArgumentNotValidHandler(exception);
        
        // 验证结果
        assertEquals(9995, response.getCode());
        assertEquals("[fieldName]错误消息", response.getData());
    }
    
    @Test
    void testMethodArgumentNotValidHandler_BindException() {
        // 模拟绑定结果
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "错误消息");
        when(bindingResult.getFieldError()).thenReturn(fieldError);
        
        // 创建异常
        BindException exception = mock(BindException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        // 调用处理方法
        JsonVO<String> response = exceptionHandler.methodArgumentNotValidHandler(exception);
        
        // 验证结果
        assertEquals(9995, response.getCode());
        assertEquals("[fieldName]错误消息", response.getData());
    }
    
    @Test
    void testMethodArgumentNotValidHandler_NoFieldError() {
        // 模拟绑定结果
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(null);
        
        // 创建异常
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        // 调用处理方法
        JsonVO<String> response = exceptionHandler.methodArgumentNotValidHandler(exception);
        
        // 验证结果
        assertEquals(9995, response.getCode());
        assertEquals("", response.getData());
    }
    
    @Test
    void testConstraintViolationHandler_MissingServletRequestParameterException() {
        // 创建异常
        MissingServletRequestParameterException exception = 
            new MissingServletRequestParameterException("paramName", "String");
        
        // 调用处理方法
        JsonVO<String> response = exceptionHandler.constraintViolationHandler(exception);
        
        // 验证结果
        assertEquals(9995, response.getCode());
        assertEquals("[paramName]参数值缺失", response.getData());
    }
} 