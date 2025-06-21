package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.systemManager.entity.Dept;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

}
