package com.zeewain.api.example;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListener();

    }

    private void initListener() {
        ImageView ivNewMeeting = findViewById(R.id.iv_bar_new_meeting);
        ImageView ivJoinMeeting = findViewById(R.id.iv_bar_join);
        ImageView ivFusionVideo = findViewById(R.id.iv_bar_fusion);

        ivNewMeeting.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewMeetingGuideActivity.class)));

        ivJoinMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, JoinGuideActivity.class));
            }
        });

        ivFusionVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JoinGuideActivity.class);
                intent.putExtra("isFromFusion",true);
                startActivity(intent);
            }
        });
    }
}