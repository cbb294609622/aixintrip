package net.aixin.app.util;

/**
 * Created by BoBo on 2015/11/11.
 */
public class HttpUrl {
    public static String BASE_URL = "http://m.aixintrip.net/api.php?";
    /**
     * 获取兴趣标签
     */
    public static String INTEREST_URL = BASE_URL+"t=73f5c138&u=123&c=tag&a=get";
    /**
     * 提交兴趣标签
     */
    public static String INTEREST_POST_URL = BASE_URL+"t=73f5c138&u=123&c=tag&a=set";
    /**
     * 获取Detial的title数据
     */
    public static String DETAIL_TITLE_DATA = BASE_URL+"t=73f5c138&u=123&c=news&a=get";

}
