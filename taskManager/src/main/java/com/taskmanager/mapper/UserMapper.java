package com.taskmanager.mapper;
import com.taskmanager.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;


@Mapper
public interface UserMapper {

    /**
     * 根据用户 ID 查询用户信息
     * @param id 用户 ID
     * @return User 实体对象
     */
    @Select("SELECT * FROM sub_track_plat.user WHERE user_id = #{id}")
    User selectById(Long id);

    /**
     * 根据用户 ID 查询用户姓名
     * @param id 用户 ID
     * @return 用户姓名
     */
    @Select("SELECT real_name FROM sub_track_plat.user WHERE user_id = #{id}")
    String getName(Long id);
    List<User> selectByIds(@Param("ids") List<Long> ids);
}
