package com.nowcoder.dao;

import com.nowcoder.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by snow on 2017/12/18.
 */
@Mapper
public interface UserDAO {
    String TABLE_NAME = "user";
    String INSERT_FIELDS = " name, password, salt, head_url ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{name}, #{password}, #{salt}, #{headUrl})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where id = #{id}"})
    User selectById(int id);

    @Select({"select ", SELECT_FIELDS, "from", TABLE_NAME, "where name = #{name}"})
    User selectByName(String name);


    @Update({"update ", TABLE_NAME, "set password=#{password} where id=#{id}"})
    void updatePassword(String password);

}
