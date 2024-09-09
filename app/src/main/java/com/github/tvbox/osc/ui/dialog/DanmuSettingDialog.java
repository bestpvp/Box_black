package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.ui.adapter.ButtonAdapter;
import com.github.tvbox.osc.util.HawkUtils;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import master.flame.danmaku.ui.widget.DanmakuView;

public class DanmuSettingDialog extends BaseDialog {

    private DanmakuView danmakuView;

    public DanmuSettingDialog(@NonNull @NotNull Context context, DanmakuView danmakuView) {
        super(context);
        setContentView(R.layout.dialog_danmu_setting);

        this.danmakuView = danmakuView;

        initOnOff();
        initColor();
        initSpeed();
        initSize();
        initLine();
        initAlpha();
    }

    private void initOnOff(){
        Boolean defaultVal = danmakuView.isShown();
        ArrayList<Boolean> players = new ArrayList<>();
        players.add(true);
        players.add(false);
        int defaultPos = defaultVal?0:1;
        setAdapter(R.id.trv_onoff,new ButtonAdapter.SelectDialogInterface<Boolean>() {
            @Override
            public void click(Boolean value, int pos) {
                if(value){
                    danmakuView.show();
                }else{
                    danmakuView.hide();
                }
            }
            @Override
            public String getDisplay(Boolean val) {
                return val ? "开启" : "关闭";
            }
        }, new DiffUtil.ItemCallback<Boolean>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Boolean oldItem, @NonNull @NotNull Boolean newItem) {
                return oldItem.booleanValue() == newItem.booleanValue();
            }
            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Boolean oldItem, @NonNull @NotNull Boolean newItem) {
                return oldItem.booleanValue() == newItem.booleanValue();
            }
        }, players, defaultPos);
    }

    private void initColor(){
        Boolean defaultVal = HawkUtils.getDanmuColor();
        ArrayList<Boolean> colors = new ArrayList<>();
        colors.add(false);
        colors.add(true);
        int defaultPos = defaultVal?1:0;
        setAdapter(R.id.trv_color,new ButtonAdapter.SelectDialogInterface<Boolean>() {
            @Override
            public void click(Boolean value, int pos) {
                HawkUtils.setDanmuColor(value);
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,true));
            }
            @Override
            public String getDisplay(Boolean val) {
                return val ? "随机" : "默认";
            }
        }, new DiffUtil.ItemCallback<Boolean>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Boolean oldItem, @NonNull @NotNull Boolean newItem) {
                return oldItem.booleanValue() == newItem.booleanValue();
            }
            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Boolean oldItem, @NonNull @NotNull Boolean newItem) {
                return oldItem.booleanValue() == newItem.booleanValue();
            }
        }, colors, defaultPos);
    }

    private void initSpeed(){
        List<Float> speeds = Arrays.asList(2.4f, 1.8f, 1.5f, 1.0f);
        int defaultPos = speeds.indexOf(HawkUtils.getDanmuSpeed());
        setAdapter(R.id.speed,new ButtonAdapter.SelectDialogInterface<Float>() {
            @Override
            public void click(Float value, int pos) {
                HawkUtils.setDanmuSpeed(value);
                EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,false));
            }
            @Override
            public String getDisplay(Float val) {
                return getSpeedText(speeds.indexOf(val));
            }
        }, new DiffUtil.ItemCallback<Float>() {
            @Override
            public boolean areItemsTheSame(@NonNull @NotNull Float oldItem, @NonNull @NotNull Float newItem) {
                return oldItem.floatValue() == newItem.floatValue();
            }
            @Override
            public boolean areContentsTheSame(@NonNull @NotNull Float oldItem, @NonNull @NotNull Float newItem) {
                return oldItem.floatValue() == newItem.floatValue();
            }
        }, speeds, defaultPos);
    }

    private void initSize(){
        TextView sizeText = findViewById(R.id.size);
        ImageView sizeAdd = findViewById(R.id.sizeAdd);
        ImageView sizeSub = findViewById(R.id.sizeSub);
        List<Float> sizes = Arrays.asList(0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f);
        int index = sizes.indexOf(HawkUtils.getDanmuSizeScale())+1;
        AtomicReference<Integer> size = new AtomicReference<>(index);
        sizeText.setText(size+"倍");
        sizeAdd.setOnClickListener((v)->{
            if(size.get() >= 15) return;
            size.set(size.get() + 1);
            HawkUtils.setDanmuSizeScale(sizes.get(size.get()-1));
            sizeText.setText(size+"倍");
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,false));
        });
        sizeSub.setOnClickListener((v)->{
            if(size.get() <= 1) return;
            size.set(size.get() - 1);
            HawkUtils.setDanmuSizeScale(sizes.get(size.get()-1));
            sizeText.setText(size+"倍");
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,false));
        });
    }

    private void initLine(){
        TextView lineText = findViewById(R.id.line);
        ImageView lineAdd = findViewById(R.id.lineAdd);
        ImageView lineSub = findViewById(R.id.lineSub);
        AtomicReference<Integer> line = new AtomicReference<>(HawkUtils.getDanmuMaxLine());
        lineText.setText(line+"行");
        lineAdd.setOnClickListener((v)->{
            if(line.get() >= 15) return;
            line.set(line.get() + 1);
            HawkUtils.setDanmuMaxLine(line.get());
            lineText.setText(line+"行");
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,false));
        });
        lineSub.setOnClickListener((v)->{
            if(line.get() <= 1) return;
            line.set(line.get() - 1);
            HawkUtils.setDanmuMaxLine(line.get());
            lineText.setText(line+"行");
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,false));
        });
    }

    private void initAlpha(){
        TextView alphaText = findViewById(R.id.alpha);
        ImageView alphaAdd = findViewById(R.id.alphaAdd);
        ImageView alphaSub = findViewById(R.id.alphaSub);
        DecimalFormat df = new DecimalFormat("#.0");
        AtomicReference<Integer> alpha = new AtomicReference<>((int)(Float.parseFloat(df.format(HawkUtils.getDanmuAlpha()))*100));
        alphaText.setText(alpha+"%");
        alphaAdd.setOnClickListener((v)->{
            if(alpha.get() >= 100) return;
            alpha.set(alpha.get() + 10);
            HawkUtils.setDanmuAlpha((float) alpha.get() /100);
            alphaText.setText(alpha+"%");
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,false));
        });
        alphaSub.setOnClickListener((v)->{
            if(alpha.get() <= 10) return;
            alpha.set(alpha.get() - 10);
            HawkUtils.setDanmuAlpha((float) alpha.get() /100);
            alphaText.setText(alpha+"%");
            EventBus.getDefault().post(new RefreshEvent(RefreshEvent.TYPE_SET_DANMU_SETTINGS,false));
        });
    }

    private String getSizeText(int index) {
        return index+1+"倍";
    }
    private String getSpeedText(int index) {
        String[] speeds = {"超慢", "慢", "适中", "快"};
        return speeds[index];
    }

    private <T> void setAdapter(int rid, ButtonAdapter.SelectDialogInterface<T>selectDialogInterface, DiffUtil.ItemCallback<T> itemCallback, List<T> data, int select) {

        ButtonAdapter<T> adapter = new ButtonAdapter<>(selectDialogInterface, itemCallback);
        adapter.setData(data, select);
        TvRecyclerView tvRecyclerView = findViewById(rid);
        tvRecyclerView.setAdapter(adapter);
        tvRecyclerView.setSelectedPosition(select);
        tvRecyclerView.post(() -> {
            tvRecyclerView.smoothScrollToPosition(select);
            tvRecyclerView.setSelectionWithSmooth(select);
        });
    }
}
