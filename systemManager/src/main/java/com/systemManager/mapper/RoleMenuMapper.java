package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.systemManager.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    int insertBatch(@Param("list") List<RoleMenu> list);
}
