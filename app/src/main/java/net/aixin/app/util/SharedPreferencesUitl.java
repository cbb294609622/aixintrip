package net.aixin.app.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by BoBo on 2015/9/2.
 */
public class SharedPreferencesUitl {
    private static SharedPreferences sharedPreferences;
    /**
     * 存boolean
     * @param context 上下文
     * @param key 键
     * @param value 值
     */
    public static void saveBooleanData(Context context,String key,Boolean value){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }
    /**
     * 取boolean
     * @param context 上下文
     * @param key 键
     * @param defValue 默认值
     * @return 返回的结果
     */
    public static Boolean getBooleanData(Context context,String key,Boolean defValue){
        if (context == null) {
            return false;
        }
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    //以上为标记值

    /**
     * 存String值
     * @param context 上下文
     * @param key   键
     * @param value 值
     */
    public static void saveStringData(Context context,String key,String value){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }

        sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 取String值
     * @param context 上下文
     * @param key   键
     * @param defValue  值
     * @return  返回的结果
     */
    public static String getStringData(Context context,String key,String defValue){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }

}
