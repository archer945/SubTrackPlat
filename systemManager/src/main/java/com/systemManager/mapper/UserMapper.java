package com.systemManager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.domain.query.systemManager.UserQuery;
import com.common.domain.vo.systemManager.UserVO;
import com.systemManager.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author yuyu
 * @since 2025-06-20
 */
public interface UserMapper extends BaseMapper<User> {

    Page<UserVO> selectUser(@Param("query") UserQuery query, Page<UserVO> page);

    boolean selectUserByUsername(@NotBlank(message = "用户名不能为空") String username);

    int batchUpdateStatus(@Param("userIds") List<Long> userIds, @Param("status") int status);
}
