package com.systemManager.controller;

import com.common.domain.dto.PageDTO;
import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.query.systemManager.DeptQuery;
import com.common.domain.vo.JsonVO;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.systemManager.service.IDeptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DeptControllerTest {

    @Mock
    private IDeptService deptService;

    @InjectMocks
    private DeptController deptController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testQueryDeptList() {
        // 准备测试数据
        DeptQuery deptQuery = new DeptQuery();
        
        // 创建一个模拟的PageDTO对象
        PageDTO<DeptTreeVO> pageDTO = mock(PageDTO.class);
        
        // 模拟服务方法
        when(deptService.listDept(any(DeptQuery.class))).thenReturn(pageDTO);
        
        // 执行测试
        JsonVO<PageDTO<DeptTreeVO>> result = deptController.queryDeptList(deptQuery);
        
        // 验证结果
        assertEquals(pageDTO, result.getData());
        assertEquals(10000, result.getCode());
        verify(deptService).listDept(deptQuery);
    }

    @Test
    void testAddDept() {
        // 准备测试数据
        DeptDTO deptDTO = new DeptDTO();
        
        // 模拟服务方法
        when(deptService.saveDept(any(DeptDTO.class))).thenReturn("添加成功");
        
        // 执行测试
        JsonVO<String> result = deptController.addDept(deptDTO);
        
        // 验证结果
        assertEquals("添加成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(deptService).saveDept(deptDTO);
    }

    @Test
    void testUpdateDept() {
        // 准备测试数据
        Long deptId = 1L;
        DeptDTO deptDTO = new DeptDTO();
        
        // 模拟服务方法
        when(deptService.updateDept(eq(deptId), any(DeptDTO.class))).thenReturn("更新成功");
        
        // 执行测试
        JsonVO<String> result = deptController.updateDept(deptId, deptDTO);
        
        // 验证结果
        assertEquals("更新成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(deptService).updateDept(deptId, deptDTO);
    }

    @Test
    void testRemoveDept() {
        // 准备测试数据
        Long deptId = 1L;
        
        // 模拟服务方法
        when(deptService.removeDept(deptId)).thenReturn("删除成功");
        
        // 执行测试
        JsonVO<String> result = deptController.removeDept(deptId);
        
        // 验证结果
        assertEquals("删除成功", result.getData());
        assertEquals(10000, result.getCode());
        verify(deptService).removeDept(deptId);
    }

    @Test
    void testGetDeptTree() {
        // 准备测试数据
        List<DeptTreeVO> deptTree = new ArrayList<>();
        DeptTreeVO dept = mock(DeptTreeVO.class);
        deptTree.add(dept);
        
        // 模拟服务方法
        when(deptService.getDeptTree()).thenReturn(deptTree);
        
        // 执行测试
        JsonVO<List<DeptTreeVO>> result = deptController.getDeptTree();
        
        // 验证结果
        assertEquals(deptTree, result.getData());
        assertEquals(10000, result.getCode());
        verify(deptService).getDeptTree();
    }
} 