package com.wavy.dao;

import com.wavy.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserDao {

    @Select("select * from t_user where id = #{id}")
    public User getById(@Param("id")long id);

    @Update("update t_user set password = #{password} where id = #{id}")
    public void update(User user);
}