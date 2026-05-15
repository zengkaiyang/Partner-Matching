package com.zzkkyy.usercenter.service;

import com.zzkkyy.usercenter.model.domain.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 标签统计服务
 */
public interface TagService extends IService<Tag> {

    /**
     * 从user表统计并更新tag表
     */
    void syncTagsFromUsers();

    /**
     * 获取热门标签
     * @param limit 返回数量
     * @return 标签列表
     */
    List<Tag> getHotTags(int limit);

    /**
     * 获取所有标签统计
     * @return 标签统计信息
     */
    List<Map<String, Object>> getAllTagStats();
}
