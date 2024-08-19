package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 240101; // Handle error appropriately
        }
    }

    public AboutDialog(@NonNull @NotNull Context context) {
        super(context);
        setContentView(R.layout.dialog_about);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(CustomUtil.getPrefix());

        TextView tv_version = findViewById(R.id.tv_version);
        tv_version.setText(CustomUtil.getTitle());

        TextView tv_version_message = findViewById(R.id.tv_version_message);
        tv_version_message.setText(CustomUtil.getAppMsg());

        String versionCode = String.valueOf(getVersionCode(context));

        Button btnShowDate = findViewById(R.id.btn_show_version);
        btnShowDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Assuming AboutDialog is only used in MainActivity
                Toast.makeText(getContext(), "当前版本: "+versionCode, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
