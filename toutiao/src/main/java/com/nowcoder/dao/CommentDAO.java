package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by snow on 2017/12/24.
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content, user_id, entity_id, entity_type, created_date, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{content}, #{userId}, #{entityId}, #{entityType}, #{createdDate}, #{status}) "})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, "from ", TABLE_NAME, "where entity_id=#{entityId} and entity_type=#{entityType} order by id desc"})
    List<Comment> selectByEntity(@Param("entityId")int entityId,
                               @Param("entityType")int entityType);

    @Select({"select count(id) from ", TABLE_NAME, "where entity_id=#{entityID} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId")int entityId,
                        @Param("entityType")int entityType);
}
