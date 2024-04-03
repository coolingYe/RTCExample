package com.zeewain.api.example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeewain.api.example.model.FusionConfig;
import com.zeewain.api.example.utils.DisplayUtil;
import com.zeewain.rtc.IRtcEngineEventHandler;
import com.zeewain.rtc.RtcEngine;
import com.zeewain.rtc.RtcEngineConfig;
import com.zeewain.rtc.model.CameraCapturerConfiguration;
import com.zeewain.utils.CommonUtils;

import org.webrtc.SurfaceViewRenderer;

import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class FusionActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener, GestureDetector.OnGestureListener {

    private Boolean cameraEnable;
    private Boolean audioEnable;
    private RtcEngineConfig config;
    private RtcEngine mRtcEngine;
    protected Handler handler;

    private TextView tvParticipants;
    private ImageView ivAudio;
    private ImageView ivVideo;
    private VideoReportLayout videoReportLayout;
    private SurfaceViewRenderer fusionView;
    private static final int lineCount = 4;

    private GestureDetector gestureDetector;

    private ActivityResultLauncher<Intent> launcher;

    public static final int RESULT_FROM_UPDATE_FUSION = 0x00001;
    public static final int RESULT_FROM_UPDATE_BACKGROUND = 0x00002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fusion);

        handler = new Handler(Looper.getMainLooper());
        String roomId = getIntent().getStringExtra("roomId");
        String displayName = getIntent().getStringExtra("displayName");
        cameraEnable = getIntent().getBooleanExtra("cameraEnable", false);
        audioEnable = getIntent().getBooleanExtra("audioEnable", false);
        audioEnable = getIntent().getBooleanExtra("audioEnable", false);
        String backgroundUrl = getIntent().getStringExtra("backgroundUrl");

        gestureDetector = new GestureDetector(this, this);

        videoReportLayout = findViewById(R.id.videoReportLayout);
        NestedScrollView nestedScrollView = findViewById(R.id.scrollView);
        tvParticipants = findViewById(R.id.participants);
        fusionView = findViewById(R.id.fusionView);

        ConstraintLayout rightView = findViewById(R.id.rightView);
        rightView.getLayoutParams().width = (getWindowWidth() / lineCount) + DisplayUtil.dip2px(this, 18);
        nestedScrollView.getLayoutParams().width = getWindowWidth() / lineCount;

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_FROM_UPDATE_FUSION) {
                if (result.getData() == null) return;
                FusionConfig fusionConfig = (FusionConfig) result.getData().getSerializableExtra("data_return");
                if (fusionConfig == null) return;
                mRtcEngine.updateFusionSetting(fusionConfig.getUserNumber(),
                        fusionConfig.getScale(),
                        fusionConfig.getFromBottomRatio(),
                        fusionConfig.getScaleFromLeft(),
                        fusionConfig.getScaleFromWidth(),
                        fusionConfig.getRotationAngle());
                Toast.makeText(FusionActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
            }
        });

        config = new RtcEngineConfig();
        config.mContext = FusionActivity.this;
        config.mChannelProfile = 1;
        config.mRoomId = roomId;
        config.mAppId = getString(R.string.zwrtc_app_id);
        config.mUserToken = getString(R.string.zwrtc_fusion_token);
        config.mDisplayName = displayName;
        config.mUserId = CommonUtils.getRandomString(8);
        config.mBackgroundUrl = backgroundUrl;
        config.mEventHandler = iRtcEngineEventHandler;

        mRtcEngine = RtcEngine.create(config);

        initListener();
        checkPermissions();
    }

    private void checkPermissions() {
        String[] permissions = {android.Manifest.permission.INTERNET, android.Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "Please provide permissions", 1, permissions);
        } else joinChannel();
    }

    private void joinChannel() {
        videoReportLayout.removeAllViews();
        mRtcEngine.setupCameraCapturerConfiguration(new CameraCapturerConfiguration(CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_FRONT));
        mRtcEngine.joinChannel();
        if (cameraEnable) {
            ivVideo.setImageResource(R.drawable.ic_webcam_on);
            mRtcEngine.enableVideo();
        } else {
            ivVideo.setImageResource(R.drawable.ic_webcam_off);
        }

        if (audioEnable) {
            ivAudio.setImageResource(R.drawable.ic_mic_on);
            mRtcEngine.enableAudio();
        } else {
            ivAudio.setImageResource(R.drawable.ic_mic_off);
        }

        View view = View.inflate(this, R.layout.item_player_layout, null);
        SurfaceViewRenderer surfaceView = view.findViewById(R.id.player);
        surfaceView.getLayoutParams().width = (getWindowWidth() / lineCount) - DisplayUtil.dip2px(this, 20);
        surfaceView.getLayoutParams().height = (getWindowWidth() / lineCount) - DisplayUtil.dip2px(this, 20);
        surfaceView.setVisibility(View.VISIBLE);
        TextView textView = view.findViewById(R.id.tv_consume_name);
        textView.setText(config.mUserId);
        view.setTag(config.mUserId);
        mRtcEngine.setupLocalVideo(surfaceView);
        videoReportLayout.addView(view);
        tvParticipants.setText(videoReportLayout.getChildCount() + " 人参加");
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        TextView tvMeetingTitle = findViewById(R.id.tv_meeting_title);
        TextView btnExit = findViewById(R.id.btn_meeting_exit);
        TextView btnEnd = findViewById(R.id.btn_meeting_end);
        ImageView ivSwitchCamera = findViewById(R.id.iv_meeting_switch_camera);
        ivAudio = findViewById(R.id.iv_meeting_audio);
        ivVideo = findViewById(R.id.iv_meeting_video);
        ImageView tvChat = findViewById(R.id.iv_meeting_chat);
        TextView tvParticipant = findViewById(R.id.tv_meeting_participants);
        ImageView tvMore = findViewById(R.id.tv_meeting_more);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        drawerLayout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });

        tvParticipants.setOnClickListener(this);
        tvMeetingTitle.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        ivAudio.setOnClickListener(this);
        ivVideo.setOnClickListener(this);
        tvChat.setOnClickListener(this);
        tvParticipant.setOnClickListener(this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        joinChannel();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_meeting_title) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", mRtcEngine.getRoomLink());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.btn_meeting_exit) {
            mRtcEngine.leaveChannel();
        }
        if (v.getId() == R.id.btn_meeting_end) {
            mRtcEngine.closeChannel();
        }
        if (v.getId() == R.id.tv_meeting_more) {
            showMoreDialog();
        }
        if (v.getId() == R.id.participants) {
            DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
            drawerLayout.openDrawer(Gravity.RIGHT);
        }
        if (v.getId() == R.id.iv_meeting_video) {
            if (mRtcEngine.hasVideoAvailable()) {
                ivVideo.setImageResource(R.drawable.ic_webcam_off);
                mRtcEngine.disableVideo();
            } else {
                ivVideo.setImageResource(R.drawable.ic_webcam_on);
                mRtcEngine.enableVideo();
            }
        }
        if (v.getId() == R.id.iv_meeting_audio) {
            if (mRtcEngine.hasAudioAvailable()) {
                ivAudio.setImageResource(R.drawable.ic_mic_off);
                mRtcEngine.disableAudio();
            } else {
                ivAudio.setImageResource(R.drawable.ic_mic_on);
                mRtcEngine.enableAudio();
            }
        }
        if (v.getId() == R.id.iv_meeting_switch_camera) {
            mRtcEngine.switchCamera();
        }
        if (v.getId() == R.id.iv_meeting_chat) {
            mRtcEngine.sendChatMessage("test");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**leaveChannel and Destroy the RtcEngine instance*/
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
        destroyVideoTrack();
        handler.post(RtcEngine::destroy);
        mRtcEngine = null;
    }

    private final IRtcEngineEventHandler iRtcEngineEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onError(int err) {

        }

        @Override
        public void onJoinChannelSuccess(String uid) {
            handler.post(() -> Toast.makeText(FusionActivity.this, "onJoinChannelSuccess() :" + uid, Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onLeaveChannel() {
            finish();
        }

        @Override
        public void onCloseChannel() {
            finish();
        }

        @Override
        public void onUserOnline(JSONArray usersInfo) {
           handler.post(() -> usersInfo.forEach(object -> {
               JSONObject jsonObject = (JSONObject) object;
               String uid = jsonObject.getString("id");
               addUserView(uid);
           }));
        }

        @Override
        public void onUserJoined(String uid) {
            handler.post(() -> {
                addUserView(uid);
            });
        }

        @Override
        public void onUserOffline(String uid) {
            handler.post(() -> {
                Toast.makeText(FusionActivity.this, "onUserOffline() :" + uid, Toast.LENGTH_SHORT).show();
                for (int i = 0; i < videoReportLayout.getChildCount(); i++) {
                    View childView = videoReportLayout.getChildAt(i);
                    if (childView.getTag() != null) {
                        if (childView.getTag().toString().equals(uid)) {
                            videoReportLayout.removeView(childView);
                            tvParticipants.setText(videoReportLayout.getChildCount() + " 人参加");
                            break;
                        }
                    }
                }
            });

        }

        @Override
        public void onRemoteVideoStateChanged(String uid, String trackId, boolean state) {
            handler.post(() -> {
                if (Objects.equals(uid, "_fusion_merge")) {
                    mRtcEngine.setupRemoteVideo(fusionView, uid);
                }
            });
        }

        @Override
        public void onUserMessage(String s, String s1) {

        }
    };

    @SuppressLint("SetTextI18n")
    private void addUserView(String uid) {
        if (Objects.equals(uid, "_fusion_merge")) {
            return;
        }
        View view = View.inflate(FusionActivity.this, R.layout.item_player_layout, null);
        ImageView ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        ivUserAvatar.getLayoutParams().width = (getWindowWidth() / lineCount) - DisplayUtil.dip2px(view.getContext(), 20);
        ivUserAvatar.getLayoutParams().height = (getWindowWidth() / lineCount) - DisplayUtil.dip2px(view.getContext(), 20);
        view.setTag(uid);
        TextView textView = view.findViewById(R.id.tv_consume_name);
        textView.setText(uid);
        videoReportLayout.addView(view);
        tvParticipants.setText(videoReportLayout.getChildCount() + " 人参加");
    }


    private int getWindowWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void destroyVideoTrack() {
        ViewGroup parentView = (ViewGroup) videoReportLayout.getParent();
        if (parentView != null) {
            parentView.removeView(videoReportLayout);
        }
        videoReportLayout.removeAllViews();
        videoReportLayout = null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        ConstraintLayout maskView = findViewById(R.id.mask);
        if (maskView.getVisibility() == View.VISIBLE) {
            maskView.setVisibility(View.GONE);
        } else {
            maskView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private void showMoreDialog() {
        Dialog moreDialog = new Dialog(this, R.style.DoraView_AlertDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_more_layout, null);
        contentView.findViewById(R.id.cl_more_setting).setOnClickListener(v -> {
            Intent intent = new Intent(FusionActivity.this, FusionConfigActivity.class);
            launcher.launch(intent);
            moreDialog.cancel();
        });

        contentView.findViewById(R.id.cl_more_background).setOnClickListener(v -> {
            showUpdateFusionBg();
            moreDialog.cancel();
        });
        moreDialog.setContentView(contentView);
        moreDialog.setCanceledOnTouchOutside(true);
        moreDialog.setCancelable(true);
        Objects.requireNonNull(moreDialog.getWindow()).setWindowAnimations(R.style.DoraView_BottomDialog_Animation);
        Objects.requireNonNull(moreDialog.getWindow()).setGravity(Gravity.BOTTOM);
        contentView.findViewById(R.id.tv_more_cancel).setOnClickListener(v -> moreDialog.cancel());
        moreDialog.show();
    }

    private void showUpdateFusionBg() {
        Dialog moreDialog = new Dialog(this, R.style.DoraView_AlertDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_update_bg_layout, null);
        EditText editText = contentView.findViewById(R.id.et_update_bg_link);
        TextView submit = contentView.findViewById(R.id.tv_update_bg_submit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    submit.setTextColor(0xFFFFFFFF);
                    submit.setBackgroundResource(R.drawable.shape_right_0e71e9_frame);
                    submit.setEnabled(true);
                } else {
                    submit.setTextColor(0xFF000000);
                    submit.setBackgroundResource(0x00000000);
                    submit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        submit.setOnClickListener(v -> {
            String bgLink = editText.getText().toString().trim();
            mRtcEngine.updateFusionBackground(bgLink);
            moreDialog.cancel();
            Toast.makeText(FusionActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
        });
        moreDialog.setContentView(contentView);
        moreDialog.setCanceledOnTouchOutside(true);
        moreDialog.setCancelable(true);
        Objects.requireNonNull(moreDialog.getWindow()).setWindowAnimations(R.style.DoraView_BottomDialog_Animation);
        Objects.requireNonNull(moreDialog.getWindow()).setGravity(Gravity.BOTTOM);
        contentView.findViewById(R.id.tv_more_cancel).setOnClickListener(v -> moreDialog.cancel());
        moreDialog.show();
    }
}
