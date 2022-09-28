package uniqid;

/**
 * @author ankh
 * @ClassName CodeConverter.java
 * @Description 本质是转换为64进制
 * @createTime 2022/9/27 11:25
 */
public class CodeConverter {

    /**
     * 0~9 + 字母大小写 + #*，不重复
      */
    private static final char[] CODE_CHAR = "5W6X7Y8Z9efghijklmnopqrstuvwxyz0a1b2c3d4#ABCDEFGHI*JKLMNOPQRSTUV".toCharArray();

    private static final int BASE = CODE_CHAR.length;
    /**
     * 移动位数
     */
    private static final int SHIFTBITS = 6;

    public static String convert(long number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Number must be positive: " + number);
        }
        StringBuilder builder = new StringBuilder();
        while (number != 0) {
            builder.append(CODE_CHAR[(int) (number % BASE)]);
            number = number >> SHIFTBITS;
        }
        return builder.toString();
    }

}
