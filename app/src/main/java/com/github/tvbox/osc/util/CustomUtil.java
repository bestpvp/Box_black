package com.github.tvbox.osc.util;

import android.os.Handler;
import android.os.Looper;

import com.orhanobut.hawk.Hawk;
import com.github.catvod.net.OkHttp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class CustomUtil {

    public static void clearCache(){
        Hawk.deleteAll();
        System.out.println("clearCache: 清除缓存成功");
    }

//    public static void printAllCache(){
//        Hawk.;
//    }

    public static String filterString(String input) {
        try {
//            System.out.println("过滤数据: input - "+input);
            String jsonString = Hawk.get("filter", "");
            if (!jsonString.isEmpty()){
//                System.out.println("过滤数据: 开始过滤 - "+jsonString);
                JsonArray filterListTest = JsonParser.parseString(jsonString).getAsJsonArray();
                for (int i = 0; i < filterListTest.size(); i++) {
                    String filter = filterListTest.get(i).getAsString();
//                    System.out.println("过滤数据: 循环 - "+filter);
                    input = input.replace(filter, "").replaceAll("^\\s+|\\s+$", "");
                }
            }
//            System.out.println("过滤数据: output - "+input);
            return input;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("过滤数据: error - "+input);
            return input;
        }
    }

    public static String getPrefix() {
        return Hawk.get("prefix_wolong", "");
    }

    public static String getTitle() {
        return Hawk.get("title", "");
    }

    public static String getAppMsg() {
        return Hawk.get("app_message", "");
    }

    public static String getSource() {
        return Hawk.get("source", "");
    }

    public static int getForceRefresh() {
        return Hawk.get("force_refresh", -1);
    }

    public interface Callback {
        void onResult(String result);
    }

    public static void initCache(CustomUtil.Callback callback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://gitee.com/bestpvp/config/raw/master/config/unify.json";
                System.out.println("initCache: 请求接口: " + url);
                String data = OkHttp.string(url);

                // 使用 Handler 将结果传回主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onResult(data);
                        }
                    }
                });
            }
        });
        thread.start();
    }

}