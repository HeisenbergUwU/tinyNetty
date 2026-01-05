package io.donkey.util.internal;

public class ArrayUtil {

    public static void printArray(char[] table) {
        if (table == null) {
            System.out.println("null");
            return;
        }
        for (char c : table) {
            System.out.print(c);
        }
        System.out.println(); // 换行，使输出更清晰
    }

    public static void printAsMatrix(Object[] table) {
        if (table == null) {
            System.out.println("null");
            return;
        }
        int len = table.length;
        int n = (int) Math.sqrt(len);
        if (n * n != len) {
            for (int i = 0; i < len; i++) {
                System.out.print(table[i]);
                if ((i + 1) % 16 == 0) {
                    System.out.println(); // 换行：每打印完一整行
                }
            }
        }

        for (int i = 0; i < len; i++) {
            System.out.print(table[i]);
            if ((i + 1) % n == 0) {
                System.out.println(); // 换行：每打印完一整行
            }
        }
    }
}
