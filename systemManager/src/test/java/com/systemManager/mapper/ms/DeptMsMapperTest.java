package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.vo.systemManager.DeptTreeVO;
import com.systemManager.entity.Dept;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DeptMsMapperTest {

    private DeptMsMapper deptMsMapper = Mappers.getMapper(DeptMsMapper.class);

    @Test
    void testDtoToDo() {
        // 创建DTO对象
        DeptDTO dto = new DeptDTO();
        dto.setDeptName("测试部门");
        dto.setDeptCode("test_dept");
        dto.setParentId(0L);
        dto.setOrderNum(1);
        dto.setStatus(1);
        dto.setLeader("测试领导");
        dto.setTel("13800138000");
        dto.setEmail("test@example.com");
        
        // 转换为实体对象
        Dept dept = deptMsMapper.dtoToDo(dto);
        
        // 验证转换结果
        assertNotNull(dept);
        assertEquals("测试部门", dept.getDeptName());
        assertEquals("test_dept", dept.getDeptCode());
        assertEquals(0L, dept.getParentId());
        assertEquals(1, dept.getOrderNum());
        assertEquals(1, dept.getStatus());
        assertEquals("测试领导", dept.getLeader());
        assertEquals("13800138000", dept.getTel());
        assertEquals("test@example.com", dept.getEmail());
    }

    @Test
    void testDoToVo() {
        // 创建实体对象
        Dept dept = new Dept();
        dept.setDeptId(1L);
        dept.setDeptName("测试部门");
        dept.setDeptCode("test_dept");
        dept.setParentId(0L);
        dept.setOrderNum(1);
        dept.setStatus(1);
        dept.setLeader("测试领导");
        dept.setTel("13800138000");
        dept.setEmail("test@example.com");
        
        // 转换为VO对象
        DeptTreeVO vo = deptMsMapper.doToVo(dept);
        
        // 验证转换结果
        assertNotNull(vo);
        assertEquals(1L, vo.getDeptId());
        assertEquals("测试部门", vo.getDeptName());
        assertEquals("test_dept", vo.getDeptCode());
        assertEquals(0L, vo.getParentId());
        assertEquals(1, vo.getOrderNum());
        assertEquals(1, vo.getStatus());
        assertEquals("测试领导", vo.getLeader());
        assertEquals("13800138000", vo.getTel());
        assertEquals("test@example.com", vo.getEmail());
    }
} 