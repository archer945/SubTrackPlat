package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.DeptDTO;
import com.systemManager.entity.Dept;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeptMsMapper {
    /**
     * 将DeptDTO转换为实体类
     * @param dto DTO
     * @return 转换结果
     */
    Dept dtoToDo(DeptDTO dto);
}
