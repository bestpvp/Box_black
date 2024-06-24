package com.github.tvbox.osc.util;

import android.content.Context;

import androidx.media3.exoplayer.DefaultRenderersFactory;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.bean.IJKCode;
import com.orhanobut.hawk.Hawk;

import java.util.List;

import io.github.anilbeesetti.nextlib.media3ext.ffdecoder.NextRenderersFactory;

public class HawkUtils {
    public static String getIJKCodec() {
        return Hawk.get(HawkConfig.IJK_CODEC, "");
    }

    public static void nextIJKCodec() {
        List<IJKCode> ijkCodes = ApiConfig.get().getIjkCodes();
        String ijkCodec = getIJKCodec();
        int index = 0;
        for (int i = 0; i < ijkCodes.size(); i++) {
            IJKCode ijkCode = ijkCodes.get(i);
            if (ijkCode.getName().equals(ijkCodec)) {
                index = i;
                break;
            }
        }
        ijkCodes.get(index).selected(false);
        index++;
        index %= ijkCodes.size();
        ijkCodes.get(index).selected(true);
    }

    public static boolean getIJKCache() {
        return Hawk.get(HawkConfig.IJK_CACHE_PLAY, false);
    }

    public static void nextIJKCache() {
        boolean ijkCache = getIJKCache();
        Hawk.put(HawkConfig.IJK_CACHE_PLAY, !ijkCache);
    }

    public static String getIJKCacheDesc() {
        return getIJKCache() ? "开启" : "关闭";
    }

    /**
     * 获取exo渲染器 自己存储的数据
     *
     * @return int
     */
    public static int getExoRenderer() {
        return Hawk.get(HawkConfig.EXO_RENDERER, 0);
    }

    public static void nextExoRenderer() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer);
        int renderer = getExoRenderer();
        renderer++;
        renderer %= array.length;
        Hawk.put(HawkConfig.EXO_RENDERER, renderer);
    }

    /**
     * 创建exo渲染器
     *
     * @param context 上下文
     * @return {@link DefaultRenderersFactory }
     */
    public static DefaultRenderersFactory createExoRendererActualValue(Context context) {
        int renderer = getExoRenderer();
        switch (renderer) {
            case 1:
                return new NextRenderersFactory(context);
            case 0:
            default:
                return new DefaultRenderersFactory(context);
        }
    }

    /**
     * 获取exo渲染器描述
     *
     * @return {@link String }
     */
    public static String getExoRendererDesc() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer);
        return array[getExoRenderer()];
    }

    /**
     * 获取exo渲染器模式 自己存储的 值
     *
     * @return int
     */
    public static int getExoRendererMode() {
        return Hawk.get(HawkConfig.EXO_RENDERER_MODE, 1);
    }

    public static void nextExoRendererMode() {
        int rendererMode = getExoRendererMode();
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer_mode);
        rendererMode++;
        rendererMode %= array.length;
        Hawk.put(HawkConfig.EXO_RENDERER_MODE, rendererMode);
    }


    /**
     * 返回程序 需要的值 exo渲染器模式
     */
    public static int getExoRendererModeActualValue() {
        int i = getExoRendererMode();
        switch (i) {
            case 0:
                return DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON;
            case 2:
                return DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            case 1:
            default:
                return DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;
        }
    }

    /**
     * 获取exo渲染器模式描述
     *
     * @return {@link String }
     */
    public static String getExoRendererModeDesc() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_ExoPlayer_renderer_mode);
        return array[getExoRendererMode()];
    }

    // Vod 播放器首选
    public static int getVodPlayerPreferred() {
        return Hawk.get(HawkConfig.VOD_PLAYER_PREFERRED, 0);
    }

    public static void nextVodPlayerPreferred() {
        int index = getVodPlayerPreferred();
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_General_VodPlayerPreferred);
        index++;
        index %= array.length;
        Hawk.put(HawkConfig.VOD_PLAYER_PREFERRED, index);
    }

    public static boolean getVodPlayerPreferredConfigurationFile() {
        int i = getVodPlayerPreferred();
        return i == 0;
    }

    public static String getVodPlayerPreferredDesc() {
        App app = App.getInstance();
        String[] array = app.getResources().getStringArray(R.array.media_content_General_VodPlayerPreferred);
        return array[getVodPlayerPreferred()];
    }

    public static String getLastLiveChannelGroup() {
        return Hawk.get(HawkConfig.LIVE_CHANNEL_GROUP, "");
    }

    public static void setLastLiveChannelGroup(String group) {
        Hawk.put(HawkConfig.LIVE_CHANNEL_GROUP, group);
    }

    public static String getLastLiveChannel() {
        return Hawk.get(HawkConfig.LIVE_CHANNEL, "");
    }

    public static void setLastLiveChannel(String channel) {
        Hawk.put(HawkConfig.LIVE_CHANNEL, channel);
    }
}
