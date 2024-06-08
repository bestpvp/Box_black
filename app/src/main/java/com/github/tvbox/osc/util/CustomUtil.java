package com.github.tvbox.osc.util;

import com.orhanobut.hawk.Hawk;
import com.github.catvod.net.OkHttp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class CustomUtil {

    public static void initCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "https://gitee.com/bestpvp/config/raw/master/config/unify.json";
                    if (!Hawk.get("source", "").isEmpty()) {
                        System.out.println("initCache: 读取缓存成功");
                    } else {
                        System.out.println("initCache: 请求接口: " + url);
                        String data = OkHttp.string(url);
                        if (!data.isEmpty()) {
                            JsonObject object = JsonParser.parseString(data).getAsJsonObject();
                            Hawk.put("force_refresh", object.get("force_refresh").getAsInt());
                            Hawk.put("source", object.get("source").getAsString());
                            Hawk.put("app_show_dialog", object.get("app_show_dialog").getAsBoolean());
                            Hawk.put("jar_show_dialog", object.get("jar_show_dialog").getAsBoolean());
                            Hawk.put("app_require_password", object.get("app_require_password").getAsBoolean());
                            Hawk.put("jar_require_password", object.get("jar_require_password").getAsBoolean());
                            Hawk.put("app_password", object.get("app_password").getAsString());
                            Hawk.put("jar_password", object.get("jar_password").getAsString());
                            Hawk.put("universal_password", object.get("universal_password").getAsString());
                            Hawk.put("app_message", object.get("app_message").getAsString());
                            Hawk.put("jar_message", object.get("jar_message").getAsString());
                            Hawk.put("filter", object.getAsJsonArray("filter").toString());
                            Hawk.put("prefix_wolong", object.get("prefix_wolong").getAsString());
                            Hawk.put("title", object.get("title").getAsString());
                            Hawk.put("picture", object.get("picture").getAsString());
                            Hawk.put("link", object.get("link").getAsString());
                            System.out.println("initCache: 保存缓存成功");
                        } else {
                            System.out.println("initCache: 保存缓存失败: " + data);
                        }
                    }
//                    printAllCache();
                    System.out.println("缓存数量: "+Hawk.count());
                } catch (Exception e) {
                    System.out.println("initCache: 保存缓存异常");
                }
            }
        }).start();
    }

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
        return Hawk.get("prefix_wolong", "★卧龙TV★");
    }

    public static String getTitle() {
        return Hawk.get("title", "公众号「插兜的干货仓库」");
    }

    public static String getAppMsg() {
        return Hawk.get("app_message", "本APP以及「时光机」均为免费开源项目，仅供测试，请勿付费购买！ \n播放时若出现广告均为三方插入, 与本公众号无关，请勿上当!");
    }

    public static String getSource() {
        return Hawk.get("source", "https://gitee.com/bestpvp/source/raw/master/source/stable/main.json");
    }

    public static int getForceRefresh() {
        return Hawk.get("force_refresh", -1);
    }
}