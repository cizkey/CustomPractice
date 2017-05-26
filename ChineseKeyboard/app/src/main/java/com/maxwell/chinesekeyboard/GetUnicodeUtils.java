package com.maxwell.chinesekeyboard;

/**
 * Created by Administrator on 2017/5/25.
 */

public class GetUnicodeUtils {

    /**
     * 生成xml文件中的codes和keyLabel
     */
    public static void main(String[] args) {
        String[] names = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈"};
        char c;
        int i;
        for (String str : names) {
            c = str.toCharArray()[0];
            i = c;
            System.out.println("<Key android:codes=\"" + i + "\" android:keyLabel=\"" + c + "\"/>");
        }
    }
}
