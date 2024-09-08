package com.github.tvbox.osc.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.tvbox.osc.ui.activity.SettingActivity;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.CustomUtil;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JxtokenDialog extends BaseDialog {

    @SuppressLint("SetTextI18n")
    public JxtokenDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_jxtoken);

        TextView jxTokenTitle = findViewById(R.id.jxTokenTitle);
        jxTokenTitle.setText("JxToken");

        // 获取按钮
        TextView cancelButton = findViewById(R.id.jxTokenButton_cancel);
        TextView saveButton = findViewById(R.id.jxTokenButton_save);

        TextInputEditText inputText = findViewById(R.id.jxTokenInputText);

        // 设置按钮监听器
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // 关闭对话框
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = Objects.requireNonNull(inputText.getText()).toString();
                if (!input.isEmpty()) {
                    // 处理保存操作
                    if (context instanceof SettingActivity) {
                        TextView jxtokenText = ((SettingActivity) context).findViewById(R.id.jxtokenText);
                        jxtokenText.setText(input);
                    }
                    Toast.makeText(getContext(), "保存成功: " + input, Toast.LENGTH_SHORT).show();
                    Hawk.put("jx_token", input);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "请输入内容", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
