package com.github.tvbox.osc.util;

import androidx.media3.common.util.UriUtil;

import com.github.tvbox.osc.base.App;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author asdfgh、FongMi
 * 参考 FongMi/TV 的代码
 * https://github.com/FongMi/TV
 */
public class M3U8 {
    private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
    private static final String TAG_MEDIA_DURATION = "#EXTINF";
    private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
    private static final String TAG_KEY = "#EXT-X-KEY";

    private static final Pattern REGEX_X_DISCONTINUITY = Pattern.compile("#EXT-X-DISCONTINUITY[\\s\\S]*?(?=#EXT-X-DISCONTINUITY|$)");
    private static final Pattern REGEX_MEDIA_DURATION = Pattern.compile(TAG_MEDIA_DURATION + ":([\\d\\.]+)\\b");
    private static final Pattern REGEX_URI = Pattern.compile("URI=\"(.+?)\"");

    public static boolean isAd(String regex) {
        return regex.contains(TAG_DISCONTINUITY) || regex.contains(TAG_MEDIA_DURATION) || regex.contains(TAG_ENDLIST) || regex.contains(TAG_KEY) || M3U8.isDouble(regex);
    }

    public static String purify(String tsUrlPre, String m3u8content) {
        if (null == m3u8content || m3u8content.length() == 0) return null;
        if (!m3u8content.startsWith("#EXTM3U")) return null;
        String result = removeMinorityUrl(tsUrlPre, m3u8content);
        if (result != null) return result;
        return get(tsUrlPre, m3u8content);
    }

    /**
     * @author asdfgh
     * <a href="https://github.com/asdfgh"> asdfgh </a>
     */
    private static String removeMinorityUrl(String tsUrlPre, String m3u8content) {
        String linesplit = "\n";
        if (m3u8content.contains("\r\n"))
            linesplit = "\r\n";
        String[] lines = m3u8content.split(linesplit);

        HashMap<String, Integer> preUrlMap = new HashMap<>();
        for (String line : lines) {
            if (line.length() == 0 || line.charAt(0) == '#') {
                continue;
            }
            int ilast = line.lastIndexOf('.');
            if (ilast <= 4) {
                continue;
            }
            String preUrl = line.substring(0, ilast - 4);
            Integer cnt = preUrlMap.get(preUrl);
            if (cnt != null) {
                preUrlMap.put(preUrl, cnt + 1);
            } else {
                preUrlMap.put(preUrl, 1);
            }
        }
        if (preUrlMap.size() <= 1) return null;
        if (preUrlMap.size() > 5) {
            //尝试判断域名，取同域名最多的链接，其它域名当作广告去除
            preUrlMap.clear();
            for (String line : lines) {
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                if (!line.startsWith("http://") && !line.startsWith("https://")) {
                    return null;
                }
                int ifirst = line.indexOf('/', 9);//skip http:// or https://
                if (ifirst <= 0) {
                    continue;
                }
                String preUrl = line.substring(0, ifirst);
                Integer cnt = preUrlMap.get(preUrl);
                if (cnt != null) {
                    preUrlMap.put(preUrl, cnt + 1);
                } else {
                    preUrlMap.put(preUrl, 1);
                }
            }
            if (preUrlMap.size() <= 1) return null;
            int maxTimes = 0;
            int totalTimes = 0;
            for (Map.Entry<String, Integer> entry : preUrlMap.entrySet()) {
                if (entry.getValue() > maxTimes) {
                    maxTimes = entry.getValue();
                }
                totalTimes += entry.getValue();
            }
            if (maxTimes*1.0 / (totalTimes*1.0) < 0.8) {
                return null; //视频非广告片断占比不够大
            }
        }
        int maxTimes = 0;
        String maxTimesPreUrl = "";
        for (Map.Entry<String, Integer> entry : preUrlMap.entrySet()) {
            if (entry.getValue() > maxTimes) {
                maxTimesPreUrl = entry.getKey();
                maxTimes = entry.getValue();
            }
        }
        if (maxTimes == 0) return null;

        boolean dealedExtXKey = false;
        for (int i = 0; i < lines.length; ++i) {
            if (!dealedExtXKey && lines[i].startsWith("#EXT-X-KEY")) {
                String keyUrl = "";
                int start = lines[i].indexOf("URI=\"");
                if (start != -1) {
                    start += "URI=\"".length();
                    int end = lines[i].indexOf("\"", start);
                    if (end != -1) {
                        keyUrl = lines[i].substring(start, end);
                    }
                    if (!keyUrl.startsWith("http://") && !keyUrl.startsWith("https://")) {
                        String newKeyUrl;
                        if (keyUrl.charAt(0) == '/') {
                            int ifirst = tsUrlPre.indexOf('/', 9);//skip https://, http://
                            newKeyUrl = tsUrlPre.substring(0, ifirst) + keyUrl;
                        } else
                            newKeyUrl = tsUrlPre + keyUrl;
                        lines[i] = lines[i].replace("URI=\"" + keyUrl + "\"", "URI=\"" + newKeyUrl + "\"");
                    }
                    dealedExtXKey = true;
                }
            }
            if (lines[i].length() == 0 || lines[i].charAt(0) == '#') {
                continue;
            }
            if (lines[i].startsWith(maxTimesPreUrl)) {
                if (!lines[i].startsWith("http://") && !lines[i].startsWith("https://")) {
                    if (lines[i].charAt(0) == '/') {
                        int ifirst = tsUrlPre.indexOf('/', 9);//skip https://, http://
                        lines[i] = tsUrlPre.substring(0, ifirst) + lines[i];
                    } else
                        lines[i] = tsUrlPre + lines[i];
                }
            } else {
                if (i > 0 && lines[i - 1].length() > 0 && lines[i - 1].charAt(0) == '#') {
                    lines[i - 1] = "";
                }
                lines[i] = "";
            }
        }
        ToastHelper.showToast(App.getInstance(), CustomUtil.getPrefix()+" 原生广告过滤成功");
        return String.join(linesplit, lines);
    }

    private static String get(String tsUrlPre, String m3u8Content) {
        m3u8Content = m3u8Content.replaceAll("\r\n", "\n");
        StringBuilder sb = new StringBuilder();
        for (String line : m3u8Content.split("\n")) sb.append(shouldResolve(line) ? resolve(tsUrlPre, line) : line).append("\n");
        List<String> ads = getRegex(tsUrlPre);
        if (ads == null || ads.isEmpty()) return null;
        return clean(sb.toString(), ads);
    }

    private static List<String> getRegex(String tsUrlPre) {
        HashMap<String, ArrayList<String>> hostsRegex = VideoParseRuler.getHostsRegex();
        List<String> list = new ArrayList<>();
        for (String host : hostsRegex.keySet()) {
            if (!tsUrlPre.contains(host)) continue;
            if (hostsRegex.get(host) == null) continue;
            list = hostsRegex.get(host);
            break;
        }
        return list;
    }

    private static String clean(String line, List<String> ads) {
        boolean scan = false;
        for (String ad : ads) {
            if (ad.contains(TAG_DISCONTINUITY) || ad.contains(TAG_MEDIA_DURATION)) line = line.replaceAll(ad, "");
            else if (isDouble(ad)) scan = true;
        }
        return scan ? scan(line, ads) : line;
    }

    private static String scan(String line, List<String> ads) {
        Matcher m1 = REGEX_X_DISCONTINUITY.matcher(line);
        while (m1.find()) {
            String group = m1.group();
            BigDecimal t = BigDecimal.ZERO;
            Matcher m2 = REGEX_MEDIA_DURATION.matcher(group);
            while (m2.find()) t = t.add(new BigDecimal(m2.group(1)));
            for (String ad : ads) if (t.toString().startsWith(ad)) line = line.replace(group.replace(TAG_ENDLIST, ""), "");
        }
        return line;
    }

    private static boolean isDouble(String ad) {
        try {
            return Double.parseDouble(ad) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean shouldResolve(String line) {
        return (!line.startsWith("#") && !line.startsWith("http")) || line.startsWith(TAG_KEY);
    }

    private static String resolve(String base, String line) {
        if (line.startsWith(TAG_KEY)) {
            Matcher matcher = REGEX_URI.matcher(line);
            String value = matcher.find() ? matcher.group(1) : null;
            return value == null ? line : line.replace(value, UriUtil.resolve(base, value));
        } else {
            return UriUtil.resolve(base, line);
        }
    }
}
