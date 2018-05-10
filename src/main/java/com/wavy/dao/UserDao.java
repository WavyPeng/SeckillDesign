package com.wavy.dao;

import com.wavy.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {

    @Select("select * from t_user where id = #{id}")
    public User getById(@Param("id")long id);
}