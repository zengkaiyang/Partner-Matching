-- 创建队伍标签表
CREATE TABLE IF NOT EXISTS team_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    team_id BIGINT NOT NULL COMMENT '队伍ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete TINYINT DEFAULT 0 COMMENT '是否删除 0-未删除 1-已删除',
    INDEX idx_team_id (team_id),
    INDEX idx_tag_name (tag_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='队伍标签关联表';
