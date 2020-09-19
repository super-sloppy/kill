package com.wk.kill.server.utils;

import io.netty.util.internal.ThreadLocalRandom;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

public class RandomUtil {

    private static final SimpleDateFormat dateFormatOne = new SimpleDateFormat("yyyyMMddHHmmssSS");

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    /**
     * 生成订单编号-方式一
     * @return
     */
    public static String generateOrderCode(){
        //TODO: 时间戳 + N位随机数流水号
        return dateFormatOne.format(DateTime.now().toDate()) + generateNumber(4);
    }

    public static String generateNumber(final int num){
        // 在高并发下用buffer保证安全
        StringBuffer sb = new StringBuffer();
        for(int i = 1; i <= num; i++){
            sb.append(random.nextInt(9));
        }
        return sb.toString();

    }

}
