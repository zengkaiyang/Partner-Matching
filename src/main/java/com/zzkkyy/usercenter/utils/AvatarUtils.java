package com.zzkkyy.usercenter.utils;

import java.util.Random;

/**
 * 头像工具类
 */
public class AvatarUtils {

    // 完整的DiceBear头像列表，包含多种风格
    private static final String[] AVATAR_LIST = {
            // Adventurer 风格
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Felix",
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Aneka",
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Zoe",
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Jack",
            "https://api.dicebear.com/7.x/adventurer/svg?seed=Mia",

            // Adventurer Neutral 风格
            "https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=Alex",
            "https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=Sara",
            "https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=Tom",
            "https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=Emma",
            "https://api.dicebear.com/7.x/adventurer-neutral/svg?seed=John",

            // Avataaars 风格
            "https://api.dicebear.com/7.x/avataaars/svg?seed=Oliver",
            "https://api.dicebear.com/7.x/avataaars/svg?seed=Sophie",
            "https://api.dicebear.com/7.x/avataaars/svg?seed=Liam",
            "https://api.dicebear.com/7.x/avataaars/svg?seed=Emma",
            "https://api.dicebear.com/7.x/avataaars/svg?seed=Noah",

            // Avataaars Neutral 风格
            "https://api.dicebear.com/7.x/avataaars-neutral/svg?seed=Anna",
            "https://api.dicebear.com/7.x/avataaars-neutral/svg?seed=Mark",
            "https://api.dicebear.com/7.x/avataaars-neutral/svg?seed=Lisa",
            "https://api.dicebear.com/7.x/avataaars-neutral/svg?seed=David",
            "https://api.dicebear.com/7.x/avataaars-neutral/svg?seed=Lucy",

            // Bottts 风格（机器人）
            "https://api.dicebear.com/7.x/bottts/svg?seed=Robot1",
            "https://api.dicebear.com/7.x/bottts/svg?seed=Robot2",
            "https://api.dicebear.com/7.x/bottts/svg?seed=Robot3",
            "https://api.dicebear.com/7.x/bottts/svg?seed=Robot4",
            "https://api.dicebear.com/7.x/bottts/svg?seed=Robot5",

            // Bottts Neutral 风格
            "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=Bot1",
            "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=Bot2",
            "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=Bot3",
            "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=Bot4",
            "https://api.dicebear.com/7.x/bottts-neutral/svg?seed=Bot5",

            // Doodles 风格
            "https://api.dicebear.com/7.x/doodles/svg?seed=Doodle1",
            "https://api.dicebear.com/7.x/doodles/svg?seed=Doodle2",
            "https://api.dicebear.com/7.x/doodles/svg?seed=Doodle3",
            "https://api.dicebear.com/7.x/doodles/svg?seed=Doodle4",
            "https://api.dicebear.com/7.x/doodles/svg?seed=Doodle5",

            // Fun Emoji 风格
            "https://api.dicebear.com/7.x/fun-emoji/svg?seed=Emoji1",
            "https://api.dicebear.com/7.x/fun-emoji/svg?seed=Emoji2",
            "https://api.dicebear.com/7.x/fun-emoji/svg?seed=Emoji3",
            "https://api.dicebear.com/7.x/fun-emoji/svg?seed=Emoji4",
            "https://api.dicebear.com/7.x/fun-emoji/svg?seed=Emoji5",

            // Icons 风格
            "https://api.dicebear.com/7.x/icons/svg?seed=Icon1",
            "https://api.dicebear.com/7.x/icons/svg?seed=Icon2",
            "https://api.dicebear.com/7.x/icons/svg?seed=Icon3",
            "https://api.dicebear.com/7.x/icons/svg?seed=Icon4",
            "https://api.dicebear.com/7.x/icons/svg?seed=Icon5",

            // Identicon 风格
            "https://api.dicebear.com/7.x/identicon/svg?seed=Ident1",
            "https://api.dicebear.com/7.x/identicon/svg?seed=Ident2",
            "https://api.dicebear.com/7.x/identicon/svg?seed=Ident3",
            "https://api.dicebear.com/7.x/identicon/svg?seed=Ident4",
            "https://api.dicebear.com/7.x/identicon/svg?seed=Ident5",

            // Initials 风格（姓名首字母）
            "https://api.dicebear.com/7.x/initials/svg?seed=AB",
            "https://api.dicebear.com/7.x/initials/svg?seed=CD",
            "https://api.dicebear.com/7.x/initials/svg?seed=EF",
            "https://api.dicebear.com/7.x/initials/svg?seed=GH",
            "https://api.dicebear.com/7.x/initials/svg?seed=IJ",

            // Lorelei 风格
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Lore1",
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Lore2",
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Lore3",
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Lore4",
            "https://api.dicebear.com/7.x/lorelei/svg?seed=Lore5",

            // Micah 风格
            "https://api.dicebear.com/7.x/micah/svg?seed=Micah1",
            "https://api.dicebear.com/7.x/micah/svg?seed=Micah2",
            "https://api.dicebear.com/7.x/micah/svg?seed=Micah3",
            "https://api.dicebear.com/7.x/micah/svg?seed=Micah4",
            "https://api.dicebear.com/7.x/micah/svg?seed=Micah5",

            // Miniavs 风格
            "https://api.dicebear.com/7.x/miniavs/svg?seed=Mini1",
            "https://api.dicebear.com/7.x/miniavs/svg?seed=Mini2",
            "https://api.dicebear.com/7.x/miniavs/svg?seed=Mini3",
            "https://api.dicebear.com/7.x/miniavs/svg?seed=Mini4",
            "https://api.dicebear.com/7.x/miniavs/svg?seed=Mini5",

            // Notionists 风格
            "https://api.dicebear.com/7.x/notionists/svg?seed=Notion1",
            "https://api.dicebear.com/7.x/notionists/svg?seed=Notion2",
            "https://api.dicebear.com/7.x/notionists/svg?seed=Notion3",
            "https://api.dicebear.com/7.x/notionists/svg?seed=Notion4",
            "https://api.dicebear.com/7.x/notionists/svg?seed=Notion5",

            // Notionists Neutral 风格
            "https://api.dicebear.com/7.x/notionists-neutral/svg?seed=Note1",
            "https://api.dicebear.com/7.x/notionists-neutral/svg?seed=Note2",
            "https://api.dicebear.com/7.x/notionists-neutral/svg?seed=Note3",
            "https://api.dicebear.com/7.x/notionists-neutral/svg?seed=Note4",
            "https://api.dicebear.com/7.x/notionists-neutral/svg?seed=Note5",

            // Open Peeps 风格
            "https://api.dicebear.com/7.x/open-peeps/svg?seed=Peep1",
            "https://api.dicebear.com/7.x/open-peeps/svg?seed=Peep2",
            "https://api.dicebear.com/7.x/open-peeps/svg?seed=Peep3",
            "https://api.dicebear.com/7.x/open-peeps/svg?seed=Peep4",
            "https://api.dicebear.com/7.x/open-peeps/svg?seed=Peep5",

            // Personas 风格
            "https://api.dicebear.com/7.x/personas/svg?seed=Persona1",
            "https://api.dicebear.com/7.x/personas/svg?seed=Persona2",
            "https://api.dicebear.com/7.x/personas/svg?seed=Persona3",
            "https://api.dicebear.com/7.x/personas/svg?seed=Persona4",
            "https://api.dicebear.com/7.x/personas/svg?seed=Persona5",

            // Pixel Art 风格
            "https://api.dicebear.com/7.x/pixel-art/svg?seed=Pixel1",
            "https://api.dicebear.com/7.x/pixel-art/svg?seed=Pixel2",
            "https://api.dicebear.com/7.x/pixel-art/svg?seed=Pixel3",
            "https://api.dicebear.com/7.x/pixel-art/svg?seed=Pixel4",
            "https://api.dicebear.com/7.x/pixel-art/svg?seed=Pixel5",

            // Pixel Art Neutral 风格
            "https://api.dicebear.com/7.x/pixel-art-neutral/svg?seed=Pixe1",
            "https://api.dicebear.com/7.x/pixel-art-neutral/svg?seed=Pixe2",
            "https://api.dicebear.com/7.x/pixel-art-neutral/svg?seed=Pixe3",
            "https://api.dicebear.com/7.x/pixel-art-neutral/svg?seed=Pixe4",
            "https://api.dicebear.com/7.x/pixel-art-neutral/svg?seed=Pixe5",

            // Rings 风格
            "https://api.dicebear.com/7.x/rings/svg?seed=Ring1",
            "https://api.dicebear.com/7.x/rings/svg?seed=Ring2",
            "https://api.dicebear.com/7.x/rings/svg?seed=Ring3",
            "https://api.dicebear.com/7.x/rings/svg?seed=Ring4",
            "https://api.dicebear.com/7.x/rings/svg?seed=Ring5",

            // Shapes 风格
            "https://api.dicebear.com/7.x/shapes/svg?seed=Shape1",
            "https://api.dicebear.com/7.x/shapes/svg?seed=Shape2",
            "https://api.dicebear.com/7.x/shapes/svg?seed=Shape3",
            "https://api.dicebear.com/7.x/shapes/svg?seed=Shape4",
            "https://api.dicebear.com/7.x/shapes/svg?seed=Shape5",

            // Thumbs 风格
            "https://api.dicebear.com/7.x/thumbs/svg?seed=Thumb1",
            "https://api.dicebear.com/7.x/thumbs/svg?seed=Thumb2",
            "https://api.dicebear.com/7.x/thumbs/svg?seed=Thumb3",
            "https://api.dicebear.com/7.x/thumbs/svg?seed=Thumb4",
            "https://api.dicebear.com/7.x/thumbs/svg?seed=Thumb5"
    };

    private static final Random random = new Random();

    /**
     * 获取随机头像
     * @return 随机头像URL
     */
    public static String getRandomAvatar() {
        return AVATAR_LIST[random.nextInt(AVATAR_LIST.length)];
    }

    /**
     * 根据用户ID生成固定头像（同一用户每次都获得相同头像）
     * @param userId 用户ID
     * @return 固定头像URL
     */
    public static String getAvatarByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return getRandomAvatar();
        }
        int index = (int) (userId % AVATAR_LIST.length);
        return AVATAR_LIST[index];
    }
}
