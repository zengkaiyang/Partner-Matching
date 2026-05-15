package com.zzkkyy.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzkkyy.usercenter.model.domain.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 标签统计Mapper
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 从user表中统计所有标签的数量
     * @return 标签及其数量的列表
     */
    @Select("SELECT " +
            "SUBSTRING_INDEX(SUBSTRING_INDEX(REPLACE(REPLACE(tags, '[', ''), ']', ''), ',', numbers.n), ',', -1) as tag_name, " +
            "COUNT(*) as count " +
            "FROM user " +
            "INNER JOIN ( " +
            "    SELECT 1 as n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 " +
            "    UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 " +
            ") numbers " +
            "ON CHAR_LENGTH(tags) - CHAR_LENGTH(REPLACE(tags, ',', '')) >= numbers.n - 1 " +
            "WHERE tags IS NOT NULL AND tags != '' " +
            "GROUP BY tag_name " +
            "ORDER BY count DESC")
    List<Map<String, Object>> countTagsFromUsers();

    /**
     * 批量更新或插入标签统计
     * @param tagName 标签名
     * @param userCount 用户数量
     */
    @Select("INSERT INTO tag (tag_name, user_count) VALUES (#{tagName}, #{userCount}) " +
            "ON DUPLICATE KEY UPDATE user_count = #{userCount}, update_time = NOW()")
    void upsertTag(@Param("tagName") String tagName, @Param("userCount") Integer userCount);
}
