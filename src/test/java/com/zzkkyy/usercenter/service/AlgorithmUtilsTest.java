package com.zzkkyy.usercenter.service;

import com.zzkkyy.usercenter.utils.AlgorithmUtils;
import io.lettuce.core.ScriptOutputType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class AlgorithmUtilsTest {

//    @Test
//    void test(){
//        String str1 = "zky是狗";
//        String str2 = "zky不是狗";
//        String str3 = "zky是鱼不是狗";
//        int dist1 = AlgorithmUtils.minDistance(str1, str2);
//        int dist2 = AlgorithmUtils.minDistance(str1, str3);
//        System.out.println("dist1 = " + dist1);
//        System.out.println("dist2 = " + dist2);
//    }

    @Test
    void testTags(){
        List<String> tagList1 = Arrays.asList("Java", "大一", "男");
        List<String> tagList2 = Arrays.asList("Java", "大二", "女");
        List<String> tagList3 = Arrays.asList("Python", "大二", "女");
        int dist1= AlgorithmUtils.minDistance(tagList1, tagList2);
        int dist2 = AlgorithmUtils.minDistance(tagList1, tagList3);
        System.out.println("dist1 = " + dist1);
        System.out.println("dist2 = " + dist2);
    }


}
