package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.MenuDTO;
import com.systemManager.entity.Menu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMsMapper {
    /**
     * 将MenuDTO转换为实体类
     * @param dto DTO
     * @return 转换结果
     */
    Menu dtoToDo(MenuDTO dto);
}
