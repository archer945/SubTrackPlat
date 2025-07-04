package com.systemManager.mapper.ms;

import com.common.domain.dto.systemManager.DeptDTO;
import com.common.domain.vo.systemManager.DeptTreeVO;
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

    /**
     * 将实体类转换为VO
     * @param entity 实体类
     * @return 转换结果
     */
    DeptTreeVO doToVo(Dept entity);
}
