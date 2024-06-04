package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.github.tvbox.osc.util.CustomUtil;
import java.text.SimpleDateFormat;
import java.util.Date;


import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.NotNull;

public class AboutDialog extends BaseDialog {

    public AboutDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_about);

        TextView contentTextView = findViewById(R.id.tv_version);
        contentTextView.setText(CustomUtil.getTitle());

        Button btnShowDate = findViewById(R.id.btn_show_date);
        btnShowDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming AboutDialog is only used in MainActivity
                Toast.makeText(getContext(), "当前版本: 240514", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
