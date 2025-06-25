package com.systemManager.mapper;

import com.common.domain.dto.systemManager.MenuDTO;
import com.systemManager.entity.Menu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsMenuMapper {
    /**
     * 将MenuDTO转换为实体类
     * @param addDto Add DTO
     * @return 转换结果
     */
    Menu dtoToDo(MenuDTO addDto);
}
