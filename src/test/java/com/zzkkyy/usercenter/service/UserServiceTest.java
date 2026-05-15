package com.zzkkyy.usercenter.service;

import com.zzkkyy.usercenter.mapper.TagMapper;
import com.zzkkyy.usercenter.model.domain.Tag;
import com.zzkkyy.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 曾凯阳
 * 无敌！
 */
@Slf4j
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Resource
    private TagService tagService;

    @Resource
    private TagMapper tagMapper;

    /**
     * 最简单的标签统计方法
     * 从user表读取所有标签 -> 统计数量 -> 存入tag表
     */
    @Test
    void testSyncTags() {
        log.info("========== 开始同步标签统计 ==========");
        // 1. 调用同步方法
        tagService.syncTagsFromUsers();

        // 2. 查询结果验证
        List<Tag> allTags = tagService.list();

        log.info("================ 同步标签数量 ===================" + allTags.size());

        // 3. 打印所有标签及数量
        for (Tag tag : allTags) {
            log.info("标签: {}, 用户数: {}", tag.getTagName(), tag.getUserCount());
        }
    }


    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("zzkkyy");
        user.setUserAccount("zzkkyy");
        user.setAvatarUrl("https://i1.hdslb.com/bfs/archive/ac7edcb6a0cd83bc3a7a7a0a3730ba69c17f47ab.png");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        assertTrue(result);
    }

    // ... existing code ...
    // ... existing code ...
    @Test
    public void addUser(){
        String candidatesFile = "src/test/java/com/zzkkyy/usercenter/candidates.csv";
        String jobsFile = "src/test/java/com/zzkkyy/usercenter/jobs.csv";

        int planetCode = 10000;
        int successCount = 0;
        int failCount = 0;

        System.out.println("开始导入candidates.csv...");

        try (BufferedReader br = new BufferedReader(new FileReader(candidatesFile, StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("candidates.csv文件为空");
                return;
            }

            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = parseCsvLine(line);

                    if (values.length < 11) {
                        System.out.println("跳过行 " + lineNum + ": 字段数不足 (实际: " + values.length + ")");
                        continue;
                    }

                    String candidateId = values[0].trim();
                    String skills = values[9].trim();
                    String tagsJson = convertSkillsToJsonArray(skills);

                    System.out.println("候选人 " + candidateId + " - skills原始值: [" + skills + "] -> JSON: " + tagsJson);

                    Long userId = userService.userRegister(
                            candidateId,
                            "12345678",
                            "12345678",
                            String.valueOf(planetCode),
                            tagsJson
                    );

                    if (userId != null && userId > 0) {
                        successCount++;
                    } else {
                        failCount++;
                    }

                    planetCode++;
                    lineNum++;

                } catch (Exception e) {
                    failCount++;
                    System.err.println("处理候选人数据时出错 (行 " + lineNum + "): " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("读取candidates.csv文件失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("candidates.csv导入完成 - 成功: " + successCount + ", 失败: " + failCount);

        System.out.println("\n开始导入jobs.csv...");
        int jobSuccessCount = 0;
        int jobFailCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(jobsFile, StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                System.out.println("jobs.csv文件为空");
                return;
            }

            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                try {
                    String[] values = parseCsvLine(line);

                    if (values.length < 13) {
                        System.out.println("跳过行 " + lineNum + ": 字段数不足 (实际: " + values.length + ")");
                        continue;
                    }

                    String jobId = values[0].trim();
                    String skills = values[12].trim();
                    String tagsJson = convertSkillsToJsonArray(skills);

                    System.out.println("职位 " + jobId + " - skills原始值: [" + skills + "] -> JSON: " + tagsJson);

                    Long userId = userService.userRegister(
                            jobId,
                            "12345678",
                            "12345678",
                            String.valueOf(planetCode),
                            tagsJson
                    );

                    if (userId != null && userId > 0) {
                        jobSuccessCount++;
                    } else {
                        jobFailCount++;
                    }

                    planetCode++;
                    lineNum++;

                } catch (Exception e) {
                    jobFailCount++;
                    System.err.println("处理职位数据时出错 (行 " + lineNum + "): " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.err.println("读取jobs.csv文件失败: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("jobs.csv导入完成 - 成功: " + jobSuccessCount + ", 失败: " + jobFailCount);
        System.out.println("全部导入完成！总计成功: " + (successCount + jobSuccessCount) + ", 失败: " + (failCount + jobFailCount));
    }
// ... existing code ...


    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        result.add(currentField.toString());

        return result.toArray(new String[0]);
    }

    private String convertSkillsToJsonArray(String skills) {
        if (skills == null || skills.trim().isEmpty()) {
            return "[]";
        }

        skills = skills.trim();

        if (skills.isEmpty()) {
            return "[]";
        }

        String[] skillArray = skills.split(",");

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < skillArray.length; i++) {
            String skill = skillArray[i].trim();
            if (!skill.isEmpty()) {
                if (i > 0) {
                    jsonBuilder.append(",");
                }
                skill = skill.replace("\\", "\\\\")
                        .replace("\"", "\\\"");
                jsonBuilder.append("\"").append(skill).append("\"");
            }
        }
        jsonBuilder.append("]");

        return jsonBuilder.toString();
    }
// ... existing code ...


    @Test
    void searchUserByUserTags() {
        List<String> tagNameList = Arrays.asList("java","python");
        List<User> userList = userService.searchUsersByTags(tagNameList);
        Assertions.assertNotNull(userList);
    }


}