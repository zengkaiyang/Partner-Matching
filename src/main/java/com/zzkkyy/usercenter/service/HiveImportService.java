package com.zzkkyy.usercenter.service;

import com.alibaba.fastjson.JSON;
import com.zzkkyy.usercenter.mapper.UserMapper;
import com.zzkkyy.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Hive 数据导入服务 - 将清洗后的数据导入 Hive
 */
@Slf4j
@Service
public class HiveImportService {

    @Autowired
    private UserMapper userMapper;

    @Value("${hive.jdbc.url:jdbc:hive2://192.168.26.202:10000/default}")
    private String hiveUrl;

    @Value("${hive.jdbc.username:}")
    private String hiveUsername;

    @Value("${hive.jdbc.password:}")
    private String hivePassword;

    private static final int BATCH_SIZE = 100; // 批量插入大小

    /**
     * 执行完整的数据导入流程
     */
    public void importToHive() {
        log.info("========== 开始导入数据到 Hive ==========");
        
        Connection conn = null;
        try {
            // 1. 连接 Hive
            log.info("步骤1: 正在连接 Hive...");
            conn = getHiveConnection();
            log.info("✅ Hive 连接成功");
            
            // 2. 获取清洗后的用户数据
            log.info("步骤2: 正在获取清洗后的用户数据...");
            List<User> users = getCleanedUsers();
            log.info("📊 获取到 {} 个用户数据", users.size());
            
            if (users.isEmpty()) {
                log.warn("⚠️ 没有数据需要导入");
                return;
            }
            
            // 3. 清空目标表（可选）
            log.info("步骤3: 正在清空目标表...");
            truncateTables(conn);
            
            // 4. 导入用户基础信息表
            log.info("步骤4: 正在导入用户基础信息表...");
            importUserInfo(conn, users);
            
            // 5. 导入用户标签明细表
            log.info("步骤5: 正在导入用户标签明细表...");
            importUserTagsDetail(conn, users);
            
            // 6. 导入标签统计表
            log.info("步骤6: 正在导入标签统计表...");
            importTagStatistics(conn, users);
            
            // 7. 导入技术栈分类统计
            log.info("步骤7: 正在导入技术栈分类统计...");
            importTechCategoryStats(conn, users);
            
            // 8. 记录任务日志（可选，已禁用）
            log.info("步骤8: 跳过任务日志记录");
            // logJobSuccess(conn, users.size());
            
            log.info("========== 数据导入 Hive 完成 ==========");
            
        } catch (Exception e) {
            log.error("❌ 数据导入 Hive 失败: {}", e.getMessage(), e);
            log.error("错误类型: {}", e.getClass().getName());
            log.error("错误堆栈:", e);
            try {
                logJobFailed(conn, e.getMessage());
            } catch (Exception ex) {
                log.error("记录失败日志失败: {}", ex.getMessage());
            }
            throw new RuntimeException("数据导入 Hive 失败: " + e.getMessage(), e);
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * 清空目标表（可选）
     */
    private void truncateTables(Connection conn) throws SQLException {
        log.info("🧹 清空目标表数据...");
        
        try {
            Statement stmt = conn.createStatement();
            
            // 检查表是否存在再清空
            String[] tables = {"user_info", "user_tags_detail", "tag_statistics", "tech_category_stats"};
            for (String table : tables) {
                try {
                    stmt.execute("TRUNCATE TABLE " + table);
                    log.info("   已清空表: {}", table);
                } catch (SQLException e) {
                    log.warn("   清空表 {} 失败（可能不存在）: {}", table, e.getMessage());
                }
            }
            stmt.close();
        } catch (Exception e) {
            log.error("清空表失败: {}", e.getMessage(), e);
        }
        
        log.info("✅ 目标表已清空");
    }

    /**
     * 获取 Hive 连接
     */
    private Connection getHiveConnection() throws SQLException {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Hive JDBC 驱动未找到: " + e.getMessage());
        }
        
        Properties props = new Properties();
        if (hiveUsername != null && !hiveUsername.trim().isEmpty()) {
            props.setProperty("user", hiveUsername);
        }
        if (hivePassword != null) {
            props.setProperty("password", hivePassword);
        }
        
        // 添加额外的连接参数以提高稳定性
        props.setProperty("hive.server2.thrift.resultset.serialize.in.tasks", "true");
        
        log.info("正在连接 Hive: {}", hiveUrl);
        Connection conn = DriverManager.getConnection(hiveUrl, props);
        log.info("Hive 连接成功");
        
        // 设置 Hive 会话参数，使用 MapReduce on YARN
        try {
            Statement stmt = conn.createStatement();
            
            // 使用 MapReduce 引擎（默认）
            stmt.execute("SET hive.execution.engine=mr");
            log.info("✅ 已设置 Hive 执行引擎为 MapReduce");
            
            // 禁用本地模式，强制使用 YARN
            stmt.execute("SET hive.exec.mode.local.auto=false");
            log.info("✅ 已禁用本地模式，将使用 YARN");
            
            // 设置 MapReduce 框架为 YARN
            stmt.execute("SET mapreduce.framework.name=yarn");
            log.info("✅ 已设置 MapReduce 框架为 YARN");
            
            // 设置 ResourceManager 地址
            stmt.execute("SET yarn.resourcemanager.address=192.168.26.202:8032");
            log.info("✅ 已设置 ResourceManager 地址");
            
            // 禁用严格模式
            stmt.execute("SET hive.mapred.mode=nonstrict");
            
            // 检查当前配置
            java.sql.ResultSet rs = stmt.executeQuery("SET hive.execution.engine");
            if (rs.next()) {
                String engine = rs.getString(1);
                log.info("🔧 当前 Hive 执行引擎: {}", engine);
            }
            rs.close();
            
            rs = stmt.executeQuery("SET mapreduce.framework.name");
            if (rs.next()) {
                String framework = rs.getString(1);
                log.info("🔧 当前 MapReduce 框架: {}", framework);
            }
            rs.close();
            
            stmt.close();
            log.info("✅ Hive 会话参数配置完成");
        } catch (SQLException e) {
            log.warn("⚠️ 设置 Hive 会话参数失败: {}", e.getMessage());
        }
        
        return conn;
    }

    /**
     * 获取清洗后的用户数据
     */
    private List<User> getCleanedUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(User::getTags)
               .ne(User::getTags, "")
               .ne(User::getTags, "[]");
        
        return userMapper.selectList(wrapper);
    }

    /**
     * 导入用户基础信息表（使用 LOAD DATA 方式，避免 MapReduce）
     */
    private void importUserInfo(Connection conn, List<User> users) throws SQLException {
        log.info("📥 开始导入用户基础信息表... ({} 条)", users.size());
        
        // 创建临时 CSV 文件（使用系统临时目录）
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFile = tempDir + "user_info_" + System.currentTimeMillis() + ".csv";
        java.io.FileWriter writer = null;
        
        try {
            writer = new java.io.FileWriter(tempFile);
            
            // 写入 CSV 数据
            for (User user : users) {
                StringBuilder line = new StringBuilder();
                line.append(user.getId()).append(",");
                line.append(escapeCsv(user.getUsername())).append(",");
                line.append(escapeCsv(user.getUserAccount())).append(",");
                line.append(escapeCsv(user.getAvatarUrl())).append(",");
                line.append(user.getGender() != null ? user.getGender() : 0).append(",");
                line.append(escapeCsv(user.getPhone())).append(",");
                line.append(escapeCsv(user.getEmail())).append(",");
                line.append(user.getUserStatus() != null ? user.getUserStatus() : 0).append(",");
                line.append(user.getUserRole() != null ? user.getUserRole() : 0).append(",");
                line.append(escapeCsv(user.getPlanetCode())).append(",");
                line.append(escapeCsv(user.getTags() != null ? user.getTags() : "[]")).append(",");
                line.append(escapeCsv(user.getBio())).append(",");
                line.append(user.getCreateTime() != null ? formatTimestamp(user.getCreateTime()) : "\\N").append(",");
                line.append(user.getUpdateTime() != null ? formatTimestamp(user.getUpdateTime()) : "\\N").append(",");
                line.append(user.getIsDelete() != null ? user.getIsDelete() : 0);
                line.append("\n");
                
                writer.write(line.toString());
            }
            
            writer.flush();
            writer.close();
            
            log.info("✅ CSV 文件已创建: {}", tempFile);
            
            // 上传文件到 HDFS
            String hdfsPath = uploadToHdfs(tempFile, "user_info");
            log.info("✅ 文件已上传到 HDFS: {}", hdfsPath);
            
            // 使用 LOAD DATA 从 HDFS 导入（不使用 LOCAL）
            Statement stmt = conn.createStatement();
            String loadSql = "LOAD DATA INPATH '" + hdfsPath + "' INTO TABLE user_info";
            log.info("执行 LOAD DATA: {}", loadSql);
            
            stmt.execute(loadSql);
            stmt.close();
            
            log.info("✅ 用户基础信息表导入完成，共 {} 条", users.size());
            
        } catch (Exception e) {
            log.error("❌ 导入用户基础信息表失败: {}", e.getMessage(), e);
            throw new SQLException("导入用户基础信息表失败: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (Exception e) {}
            }
            // 删除临时文件
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(tempFile));
                log.info("🗑️ 临时文件已删除: {}", tempFile);
            } catch (Exception e) {
                log.warn("⚠️ 删除临时文件失败: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 上传文件到 HDFS
     */
    private String uploadToHdfs(String localFile, String tableName) throws Exception {
        // 使用 WebHDFS API 上传文件
        String hdfsHost = "192.168.26.202";
        int hdfsPort = 9870; // NameNode WebHDFS 端口
        String hdfsDestPath = "/tmp/hive_import_" + tableName + "_" + System.currentTimeMillis() + ".csv";
        
        // 第一步：创建文件（获取重定向 URL）
        String createUrl = String.format("http://%s:%d/webhdfs/v1%s?op=CREATE&overwrite=true",
                hdfsHost, hdfsPort, hdfsDestPath);
        
        java.net.URL url = new java.net.URL(createUrl);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setInstanceFollowRedirects(false);
        
        int responseCode = conn.getResponseCode();
        String redirectUrl = conn.getHeaderField("Location");
        conn.disconnect();
        
        if (redirectUrl == null) {
            throw new Exception("无法获取 HDFS 上传 URL");
        }
        
        log.info("📤 WebHDFS 重定向 URL: {}", redirectUrl);
        
        // 将重定向 URL 中的主机名替换为 IP 地址
        redirectUrl = redirectUrl.replace("DKSKT-OZHOONXPQ.localdomain", hdfsHost);
        log.info("📤 修正后的 URL: {}", redirectUrl);
        
        // 第二步：上传文件内容
        java.net.URL uploadUrl = new java.net.URL(redirectUrl);
        java.net.HttpURLConnection uploadConn = (java.net.HttpURLConnection) uploadUrl.openConnection();
        uploadConn.setRequestMethod("PUT");
        uploadConn.setDoOutput(true);
        
        // 读取本地文件并上传
        byte[] fileContent = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(localFile));
        uploadConn.getOutputStream().write(fileContent);
        uploadConn.getOutputStream().flush();
        uploadConn.getOutputStream().close();
        
        responseCode = uploadConn.getResponseCode();
        uploadConn.disconnect();
        
        if (responseCode != 201 && responseCode != 200) {
            throw new Exception("HDFS 上传失败，响应码: " + responseCode);
        }
        
        log.info("✅ 文件上传成功: {}", hdfsDestPath);
        return hdfsDestPath;
    }
    
    /**
     * 转义 CSV 字段
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        // 如果包含逗号、引号或换行符，需要用引号包裹
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    /**
     * 格式化时间戳为 Hive 接受的格式
     */
    private String formatTimestamp(java.util.Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 导入用户标签明细表（使用 LOAD DATA 方式）
     */
    private void importUserTagsDetail(Connection conn, List<User> users) throws SQLException {
        log.info("📥 开始导入用户标签明细表...");
        
        // 创建临时 CSV 文件
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFile = tempDir + "user_tags_detail_" + System.currentTimeMillis() + ".csv";
        java.io.FileWriter writer = null;
        
        try {
            writer = new java.io.FileWriter(tempFile);
            
            long recordId = System.currentTimeMillis();
            int count = 0;
            
            for (User user : users) {
                try {
                    if (user.getTags() == null || user.getTags().trim().isEmpty() || "[]".equals(user.getTags())) {
                        continue;
                    }
                    
                    List<String> tags = JSON.parseArray(user.getTags(), String.class);
                    if (tags == null || tags.isEmpty()) {
                        continue;
                    }
                    
                    for (String tag : tags) {
                        StringBuilder line = new StringBuilder();
                        line.append(recordId++).append(",");
                        line.append(user.getId()).append(",");
                        line.append(escapeCsv(user.getUsername())).append(",");
                        line.append(tag.toLowerCase()).append(",");
                        line.append("Quotes,");
                        line.append(formatTimestamp(new java.util.Date()));
                        line.append("\n");
                        
                        writer.write(line.toString());
                        count++;
                    }
                } catch (Exception e) {
                    log.error("处理用户 {} 标签失败: {}", user.getId(), e.getMessage());
                }
            }
            
            writer.flush();
            writer.close();
            
            log.info("✅ CSV 文件已创建: {} ({} 条记录)", tempFile, count);
            
            // 上传到 HDFS
            String hdfsPath = uploadToHdfs(tempFile, "user_tags_detail");
            log.info("✅ 文件已上传到 HDFS: {}", hdfsPath);
            
            // 使用 LOAD DATA 导入
            Statement stmt = conn.createStatement();
            String loadSql = "LOAD DATA INPATH '" + hdfsPath + "' INTO TABLE user_tags_detail";
            log.info("执行 LOAD DATA: {}", loadSql);
            
            stmt.execute(loadSql);
            stmt.close();
            
            log.info("✅ 用户标签明细表导入完成，共 {} 条", count);
            
        } catch (Exception e) {
            log.error("❌ 导入用户标签明细表失败: {}", e.getMessage(), e);
            throw new SQLException("导入用户标签明细表失败: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (Exception e) {}
            }
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(tempFile));
                log.info("🗑️ 临时文件已删除: {}", tempFile);
            } catch (Exception e) {
                log.warn("⚠️ 删除临时文件失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 导入标签统计表（使用 LOAD DATA 方式）
     */
    private void importTagStatistics(Connection conn, List<User> users) throws SQLException {
        log.info("📥 开始导入标签统计表...");
        
        // 统计每个标签的使用次数
        Map<String, Long> tagCountMap = new HashMap<>();
        Map<String, Set<Long>> tagUserMap = new HashMap<>();
        
        for (User user : users) {
            try {
                List<String> tags = JSON.parseArray(user.getTags(), String.class);
                if (tags == null) continue;
                
                for (String tag : tags) {
                    String normalizedTag = tag.toLowerCase();
                    tagCountMap.merge(normalizedTag, 1L, Long::sum);
                    tagUserMap.computeIfAbsent(normalizedTag, k -> new HashSet<>()).add(user.getId());
                }
            } catch (Exception e) {
                log.error("统计用户 {} 标签失败: {}", user.getId(), e.getMessage());
            }
        }
        
        // 创建临时 CSV 文件
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFile = tempDir + "tag_statistics_" + System.currentTimeMillis() + ".csv";
        java.io.FileWriter writer = null;
        
        try {
            writer = new java.io.FileWriter(tempFile);
            
            LocalDate today = LocalDate.now();
            int count = 0;
            
            for (Map.Entry<String, Long> entry : tagCountMap.entrySet()) {
                String tag = entry.getKey();
                long totalCount = entry.getValue();
                long userCount = tagUserMap.get(tag).size();
                String category = categorizeTag(tag);
                
                StringBuilder line = new StringBuilder();
                line.append(escapeCsv(tag)).append(",");
                line.append(totalCount).append(",");
                line.append(userCount).append(",");
                line.append(escapeCsv(category)).append(",");
                line.append(today.toString());
                line.append("\n");
                
                writer.write(line.toString());
                count++;
            }
            
            writer.flush();
            writer.close();
            
            log.info("✅ CSV 文件已创建: {} ({} 个标签)", tempFile, count);
            
            // 上传到 HDFS
            String hdfsPath = uploadToHdfs(tempFile, "tag_statistics");
            log.info("✅ 文件已上传到 HDFS: {}", hdfsPath);
            
            // 使用 LOAD DATA 导入
            Statement stmt = conn.createStatement();
            String loadSql = "LOAD DATA INPATH '" + hdfsPath + "' INTO TABLE tag_statistics";
            log.info("执行 LOAD DATA: {}", loadSql);
            
            stmt.execute(loadSql);
            stmt.close();
            
            log.info("✅ 标签统计表导入完成，共 {} 个标签", count);
            
        } catch (Exception e) {
            log.error("❌ 导入标签统计表失败: {}", e.getMessage(), e);
            throw new SQLException("导入标签统计表失败: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (Exception e) {}
            }
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(tempFile));
                log.info("🗑️ 临时文件已删除: {}", tempFile);
            } catch (Exception e) {
                log.warn("⚠️ 删除临时文件失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 导入技术栈分类统计（使用 LOAD DATA 方式）
     */
    private void importTechCategoryStats(Connection conn, List<User> users) throws SQLException {
        log.info("📥 开始导入技术栈分类统计...");
        
        Map<String, Set<String>> categoryTechMap = new HashMap<>();
        Map<String, Set<Long>> categoryUserMap = new HashMap<>();
        
        for (User user : users) {
            try {
                List<String> tags = JSON.parseArray(user.getTags(), String.class);
                if (tags == null) continue;
                
                for (String tag : tags) {
                    String category = categorizeTag(tag);
                    categoryTechMap.computeIfAbsent(category, k -> new HashSet<>()).add(tag.toLowerCase());
                    categoryUserMap.computeIfAbsent(category, k -> new HashSet<>()).add(user.getId());
                }
            } catch (Exception e) {
                log.error("分类用户 {} 标签失败: {}", user.getId(), e.getMessage(), e);
            }
        }
        
        // 计算总数用于百分比
        long totalUsers = users.size();
        
        // 创建临时 CSV 文件
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFile = tempDir + "tech_category_stats_" + System.currentTimeMillis() + ".csv";
        java.io.FileWriter writer = null;
        
        try {
            writer = new java.io.FileWriter(tempFile);
            
            LocalDate today = LocalDate.now();
            int count = 0;
            
            for (Map.Entry<String, Set<String>> entry : categoryTechMap.entrySet()) {
                String category = entry.getKey();
                long techCount = entry.getValue().size();
                long userCount = categoryUserMap.get(category).size();
                double percentage = totalUsers > 0 ? (userCount * 100.0 / totalUsers) : 0;
                
                StringBuilder line = new StringBuilder();
                line.append(escapeCsv(category)).append(",");
                line.append(techCount).append(",");
                line.append(userCount).append(",");
                line.append(Math.round(percentage * 100.0) / 100.0).append(",");
                line.append(today.toString());
                line.append("\n");
                
                writer.write(line.toString());
                count++;
            }
            
            writer.flush();
            writer.close();
            
            log.info("✅ CSV 文件已创建: {} ({} 个分类)", tempFile, count);
            
            // 上传到 HDFS
            String hdfsPath = uploadToHdfs(tempFile, "tech_category_stats");
            log.info("✅ 文件已上传到 HDFS: {}", hdfsPath);
            
            // 使用 LOAD DATA 导入
            Statement stmt = conn.createStatement();
            String loadSql = "LOAD DATA INPATH '" + hdfsPath + "' INTO TABLE tech_category_stats";
            log.info("执行 LOAD DATA: {}", loadSql);
            
            stmt.execute(loadSql);
            stmt.close();
            
            log.info("✅ 技术栈分类统计导入完成，共 {} 个分类", count);
            
        } catch (Exception e) {
            log.error("❌ 导入技术栈分类统计失败: {}", e.getMessage(), e);
            throw new SQLException("导入技术栈分类统计失败: " + e.getMessage());
        } finally {
            if (writer != null) {
                try { writer.close(); } catch (Exception e) {}
            }
            try {
                java.nio.file.Files.deleteIfExists(java.nio.file.Paths.get(tempFile));
                log.info("🗑️ 临时文件已删除: {}", tempFile);
            } catch (Exception e) {
                log.warn("⚠️ 删除临时文件失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 标签分类
     */
    private String categorizeTag(String tag) {
        String lowerTag = tag.toLowerCase();
        
        if (lowerTag.matches(".*(java|spring|mybatis|hibernate|backend).*")) {
            return "后端开发";
        } else if (lowerTag.matches(".*(javascript|typescript|vue|react|angular|html|css|frontend).*")) {
            return "前端开发";
        } else if (lowerTag.matches(".*(mysql|postgres|mongodb|redis|elasticsearch|database).*")) {
            return "数据库";
        } else if (lowerTag.matches(".*(docker|kubernetes|jenkins|git|linux|nginx|devops).*")) {
            return "DevOps";
        } else if (lowerTag.matches(".*(python|go|rust|cpp|csharp|php|node|language).*")) {
            return "编程语言";
        } else {
            return "其他";
        }
    }

    /**
     * 记录成功日志
     */
    private void logJobSuccess(Connection conn, int count) throws SQLException {
        try {
            String sql = "INSERT INTO crawl_job_log VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            String jobId = "JOB_" + System.currentTimeMillis();
            pstmt.setString(1, jobId);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis() - 60000));
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, "success");
            pstmt.setInt(5, count);
            pstmt.setInt(6, count);
            pstmt.setString(7, ""); // 使用空字符串而不是 null
            
            pstmt.executeUpdate();
            pstmt.close();
            
            log.info("✅ 任务日志记录成功: {}", jobId);
        } catch (Exception e) {
            log.warn("⚠️ 记录任务日志失败（不影响数据导入）: {}", e.getMessage());
        }
    }

    /**
     * 记录失败日志
     */
    private void logJobFailed(Connection conn, String errorMessage) throws SQLException {
        if (conn == null || conn.isClosed()) return;
        
        try {
            String sql = "INSERT INTO crawl_job_log VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            String jobId = "JOB_" + System.currentTimeMillis();
            pstmt.setString(1, jobId);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, "failed");
            pstmt.setInt(5, 0);
            pstmt.setInt(6, 0);
            pstmt.setString(7, errorMessage != null ? errorMessage : "");
            
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            log.warn("⚠️ 记录失败日志失败: {}", e.getMessage());
        }
    }

    /**
     * 关闭连接
     */
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("关闭 Hive 连接失败: {}", e.getMessage());
            }
        }
    }
}
