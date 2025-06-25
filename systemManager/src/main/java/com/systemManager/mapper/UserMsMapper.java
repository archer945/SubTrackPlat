package com.systemManager.mapper;

import com.common.domain.dto.systemManager.AddUserDTO;
import com.common.domain.dto.systemManager.UpdateUserDTO;
import com.systemManager.entity.User;
import org.mapstruct.Mapper;

/**
 * <p>
 * 描述：用户数据类型转换映射接口
 * </p>
 * @author yuyu
 * @version 1.0.0
 */
@Mapper(componentModel = "spring")
public interface UserMsMapper {

    /**
     * 将AddUserDTO转换为实体类
     * @param addDto Add DTO
     * @return 转换结果
     */
    User addDtoToDo(AddUserDTO addDto);

    /**
     * 将UpdateUserDTO转换为实体类
     * @param dto DTO
     * @return 转换结果
     */
    User dtoToDo(UpdateUserDTO dto);
}
