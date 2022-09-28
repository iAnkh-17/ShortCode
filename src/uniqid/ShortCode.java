package uniqid;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ankh
 */
public final class ShortCode {

    private static IdGenerator UNIQUE = new IdGenerator();


    /**
     * 基于时间生成短码
     *
     * @return 生成的标识符
     */
    public static String gen() {
        StringBuilder builder = new StringBuilder(String.valueOf(UNIQUE.nextId()));
        //反转,目的是让字符串更无规律
        String reverse = builder.reverse().toString();
        return CodeConverter.convert(Long.parseLong(reverse));
    }


    private ShortCode() {
        throw new UnsupportedOperationException("Private constructor, cannot be accessed.");
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        while (true){
            String s = gen();
            boolean add = set.add(s);
            if (!add){
                break;
            }
        }
    }
}
