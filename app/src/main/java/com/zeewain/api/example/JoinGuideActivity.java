package com.zeewain.api.example;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.zeewain.utils.CommonUtils;

public class JoinGuideActivity extends AppCompatActivity {

    private TextView tvStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_join);

        boolean isFromFusion = getIntent().getBooleanExtra("isFromFusion", false);

        View view = findViewById(R.id.header_create_meeting);
        TextView tvHeaderTitle = view.findViewById(R.id.tv_header_title);
        TextView tvHeaderLeft = view.findViewById(R.id.tv_header_left);
        tvHeaderLeft.setText(getString(R.string.exit));
        tvHeaderLeft.setOnClickListener(v -> finish());
        tvHeaderTitle.setText(getString(R.string.join_meeting));

        EditText editRoomId = findViewById(R.id.edit_meeting_id);
        editRoomId.requestFocus();

        EditText editDisplayName = findViewById(R.id.edit_display_name);
        editDisplayName.setText(CommonUtils.getRandomString(8));

        EditText editBackground = findViewById(R.id.edit_display_background);
        editBackground.setText("https://oss.zeewain.com/PRODUCT_IMAGE/65b45c5d6bb74ce0882b2365208bd6cc/11111.jpg");
        editBackground.setVisibility(isFromFusion ? View.VISIBLE : View.GONE);

        View view1 = findViewById(R.id.not_auto_connect_audio);
        TextView optionAudio = view1.findViewById(R.id.tv_option_title);
        optionAudio.setText(R.string.open_microphone);
        SwitchCompat switchAudio = view1.findViewById(R.id.switch_option);
        switchAudio.setChecked(true);

        View view2 = findViewById(R.id.not_auto_open_camera);
        TextView optionCamera = view2.findViewById(R.id.tv_option_title);
        optionCamera.setText(R.string.open_camera);
        SwitchCompat switchCamera = view2.findViewById(R.id.switch_option);
        switchCamera.setChecked(true);

        tvStart = findViewById(R.id.btn_create_meeting);
        tvStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, isFromFusion ? FusionActivity.class : RoomActivity.class);
            intent.putExtra("roomId", editRoomId.getText().toString());
            intent.putExtra("displayName", editDisplayName.getText().toString());
            intent.putExtra("audioEnable", switchAudio.isChecked());
            intent.putExtra("cameraEnable", switchCamera.isChecked());
            intent.putExtra("backgroundUrl", editBackground.getText().toString());
            startActivity(intent);
            finish();
        });

        editRoomId.addTextChangedListener(textWatcher);
        editDisplayName.addTextChangedListener(textWatcher);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 7) {
                tvStart.setEnabled(true);
                tvStart.setBackgroundResource(R.drawable.shape_top_bar_frame_0e71e9);
                tvStart.setTextColor(0xFFFFFFFF);
            } else {
                tvStart.setEnabled(false);
                tvStart.setBackgroundResource(R.drawable.shape_top_bar_frame_e9e9ee);
                tvStart.setTextColor(0xFF909095);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
