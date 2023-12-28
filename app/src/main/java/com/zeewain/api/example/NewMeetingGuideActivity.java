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

public class NewMeetingGuideActivity extends AppCompatActivity {

    private TextView tvStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_new_meeting);

        TextView tvHeaderLeft = findViewById(R.id.tv_header_left);
        tvHeaderLeft.setOnClickListener(v -> finish());

        TextView tvHeaderTitle = findViewById(R.id.tv_header_title);
        tvHeaderTitle.setText(getString(R.string.start_meeting));

        View view = findViewById(R.id.layout_open_camera);
        TextView tvOptionTitle = view.findViewById(R.id.tv_option_title);
        tvOptionTitle.setText(getString(R.string.open_camera));
        SwitchCompat switchCompat = view.findViewById(R.id.switch_option);
        switchCompat.setChecked(true);

        EditText editRoomId = findViewById(R.id.edit_start_meeting_id);
        editRoomId.requestFocus();

        EditText editDisplayName = findViewById(R.id.edit_start_display_name);
        editDisplayName.setText(CommonUtils.getRandomString(8));
        tvStart = findViewById(R.id.btn_start);

        editDisplayName.addTextChangedListener(textWatcher);
        editRoomId.addTextChangedListener(textWatcher);

        tvStart.setOnClickListener(v -> {
            Intent intent = new Intent(NewMeetingGuideActivity.this, RoomActivity.class);
            intent.putExtra("roomId", editRoomId.getText().toString());
            intent.putExtra("displayName", editDisplayName.getText().toString());
            intent.putExtra("cameraEnable", switchCompat.isChecked());
            startActivity(intent);
            finish();
        });
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
    protected void onStart() {
        super.onStart();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
