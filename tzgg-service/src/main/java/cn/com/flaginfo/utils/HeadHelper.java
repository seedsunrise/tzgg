package cn.com.flaginfo.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * 返回json数据格式带head body格式的辅助类
 * @author chenzhipan
 * @since 2016-6-15
 * @version 1.0
 */
public class HeadHelper {

    public static int resultCode = 200;

    public static String message = "";

    public static Map response(Object object){
        Map map = new HashMap();
        Map map1 = new HashMap();
        map1.put("resultCode",resultCode);
        map1.put("message",message);
        map.put("head",map1);
        map.put("body",object);
        resultCode = 200;
        message = "";
        return map;
    }

    public static Map errResponse(Object object){
        resultCode=-1;
        message = (String)object;
        return response("");
    }
}
