package com.zeewain.api.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zeewain.api.example.model.FusionConfig;

public class FusionConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        View headerView = findViewById(R.id.header_view);
        TextView tvTitle = headerView.findViewById(R.id.tv_header_title);
        tvTitle.setText("融合设置");
        TextView tvCancel = headerView.findViewById(R.id.tv_header_left);
        tvCancel.setOnClickListener(view -> {
            setResult(1);
            finish();
        });

        View view1 = findViewById(R.id.view_control_1);
        TextView tvConfigTitle1 = view1.findViewById(R.id.tv_control_title);
        tvConfigTitle1.setText("参与合照人数");
        EditText editConfig1 = view1.findViewById(R.id.ed_control);
        editConfig1.setText("2");

        View view2 = findViewById(R.id.view_control_2);
        TextView tvConfigTitle2 = view2.findViewById(R.id.tv_control_title);
        tvConfigTitle2.setText("画面融合缩放系数（范围：0.000 - 20.000）");
        EditText editConfig2 = view2.findViewById(R.id.ed_control);
        editConfig2.setText("1.200");

        View view3 = findViewById(R.id.view_control_3);
        TextView tvConfigTitle3 = view3.findViewById(R.id.tv_control_title);
        tvConfigTitle3.setText("人像到底部距离的系数");
        EditText editConfig3 = view3.findViewById(R.id.ed_control);
        editConfig3.setText("0.2");

        View view4 = findViewById(R.id.view_control_4);
        TextView tvConfigTitle4 = view4.findViewById(R.id.tv_control_title);
        tvConfigTitle4.setText("人像到相机画面中左边距离的系数（范围：0.000 - 1.000）");
        EditText editConfig4 = view4.findViewById(R.id.ed_control);
        editConfig4.setText("0.400");

        View view5 = findViewById(R.id.view_control_5);
        TextView tvConfigTitle5 = view5.findViewById(R.id.tv_control_title);
        tvConfigTitle5.setText("人像区域宽度在相机画面中占比系数（范围：0.000 - 1.000）");
        EditText editConfig5 = view5.findViewById(R.id.ed_control);
        editConfig5.setText("1.000");

        View view6 = findViewById(R.id.view_control_6);
        TextView tvConfigTitle6 = view6.findViewById(R.id.tv_control_title);
        tvConfigTitle6.setText("人像在背景合照中的旋转角度（范围：-90 - 90）");
        EditText editConfig6 = view6.findViewById(R.id.ed_control);
        editConfig6.setText("0");

        TextView tvStart = findViewById(R.id.tv_start);
        tvStart.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("data_return", new FusionConfig(
                    Integer.parseInt(editConfig1.getText().toString().trim()),
                    Float.parseFloat(editConfig2.getText().toString().trim()),
                    Float.parseFloat(editConfig3.getText().toString()),
                    Float.parseFloat(editConfig4.getText().toString()),
                    Float.parseFloat(editConfig5.getText().toString()),
                    Integer.parseInt(editConfig6.getText().toString())));
            setResult(FusionActivity.RESULT_FROM_UPDATE_FUSION, intent);
            finish();
        });
    }
}
