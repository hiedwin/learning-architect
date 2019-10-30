package com.edwin.smartdevelop.modules.sys.mapper;

import com.edwin.smartdevelop.core.persistence.BaseMapper;
import com.edwin.smartdevelop.modules.sys.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 检查账号密码
    User validataLogin(User user);

    int updatePassword(User user);

    int deleteUserRole(User user);

    // 添加用户权限
    int insertUserRole(@Param("id") String id, @Param("userId") String userId, @Param("roleId") String roleId);

}
