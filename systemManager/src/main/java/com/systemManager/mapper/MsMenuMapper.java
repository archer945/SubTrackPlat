package com.systemManager.mapper;

import com.common.domain.dto.systemManager.AddMenuDTO;
import com.common.domain.dto.systemManager.UpdateMenuDTO;
import com.systemManager.entity.Menu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MsMenuMapper {
    /**
     * 将AddMenuDTO转换为实体类
     * @param addDto Add DTO
     * @return 转换结果
     */
    Menu addDtoToDo(AddMenuDTO addDto);

    /**
     * 将UpdateMenuDTO转换为实体类
     * @param dto DTO
     * @return 转换结果
     */
    Menu dtoToDo(UpdateMenuDTO dto);
}
