package uniqid;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ankh
 * @ClassName GenerateCodeUtil.java
 * @Description 生成随机激活码
 * @createTime 2022/8/29 13:48
 */
public class GenerateCodeUtil {

    public static String generateShortCode() {
       return ShortCode.gen();
    }


    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        int index = 0;
        while (true){
            index++;
            set.add(generateShortCode());
            if (set.size()==10000){
                System.out.println(index);
                break;
            }
        }
    }

}
