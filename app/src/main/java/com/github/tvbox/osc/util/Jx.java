package com.github.tvbox.osc.util;

import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.github.catvod.net.OkHttp;
import com.orhanobut.hawk.Hawk;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class Jx {

    public static String getUrl(Context context, String jxToken, String realPlayUrl, Map<String, String> header) {
        try {
            String jxUrl = Hawk.get("jxUrl", "");
            if (jxUrl.isEmpty() || jxToken.isEmpty()) return realPlayUrl;
            // 对 URL 进行编码
            String enCodeUrl = URLEncoder.encode(realPlayUrl, "UTF-8");
            System.out.println("jxUrl: "+String.format(jxUrl, jxToken, enCodeUrl));
            String response = OkHttp.string(String.format(jxUrl, jxToken, enCodeUrl), header);
            if (response.isEmpty()) {
                System.out.println("解析服务返回空, 不处理!");
                return realPlayUrl;
            }
            com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(response);
            System.out.println(object);
            // Handle potential missing "code" field
            if (object.containsKey("code") && object.getInteger("code") == 200) {
                realPlayUrl = object.getJSONObject("data").getString("jx_url");
                System.out.println(object.getString("msg"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, object.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // Extract message if available, otherwise use generic error message
                String message = object.containsKey("msg")? object.getString("msg"): object.getString("detail");
                System.out.println(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
            System.out.println("outPutUrl: "+realPlayUrl);
            return realPlayUrl;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return realPlayUrl;
        }
    }

    public interface Callback {
        void onResult(String result);
    }

    public static void fetchUrl(Context context, String initialUrl, Map<String, String> header, Callback callback) {
        System.out.println("解析开关: "+Hawk.get("remove_ad"));
        if (Hawk.get("remove_ad")) {
            if (initialUrl.contains(".m3u8") && !initialUrl.contains("www.lintech.work") && !initialUrl.contains("127.0.0.1") && !initialUrl.contains("0.0.0.0")) {
                System.out.println("时光机开始解析: "+initialUrl);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String jxToken = Hawk.get("jx_token");
                        if (!Hawk.get("related_jxtoken","").isEmpty()){
                            Integer jxToken_date = Integer.parseInt(Hawk.get("related_jxtoken", "").substring(Math.max(0, Hawk.get("related_jxtoken", "").length() - 6)));
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.getDefault());
                            String formattedDate = dateFormat.format(calendar.getTime());
                            int dateInt = Integer.parseInt(formattedDate);
                            System.out.println(jxToken_date+" - "+dateInt);
                            if (jxToken_date >= dateInt) {
                                System.out.println("关联 jxToken 有效: " + jxToken_date);
                                jxToken = Hawk.get("related_jxtoken");
                            } else {
                                System.out.println("关联 jxToken 已失效: " + jxToken_date);
                            }
                        }
                        System.out.println("最终取的 jxToken: "+jxToken);
                        final String resultUrl = getUrl(context, jxToken, initialUrl, header);

                        // 使用 Handler 将结果传回主线程
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    System.out.println("时光机解析完成: "+ resultUrl);
                                    callback.onResult(resultUrl);
                                }
                            }
                        });
                    }
                });
                thread.start();
            } else {
                // 如果前面的条件不满足，返回 initialUrl
                if (callback != null) {
                    System.out.println("时光机解析条件不满足: "+initialUrl);
                    callback.onResult(initialUrl);
                }
            }
        } else {
            // 如果前面的条件不满足，返回 initialUrl
            if (callback != null) {
                System.out.println("时光机解析条件不满足: "+initialUrl);
                callback.onResult(initialUrl);
            }
        }
    }
}
