package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.RoleDTO;
import com.systemManager.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMsMapper {
    /**
     * 将MenuDTO转换为实体类
     * @param dto dTO
     * @return 转换结果
     */
    Role dtoToDo(RoleDTO dto);
}
