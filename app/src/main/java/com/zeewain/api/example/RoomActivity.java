package com.zeewain.api.example;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zeewain.rtc.IRtcEngineEventHandler;
import com.zeewain.rtc.RtcEngine;
import com.zeewain.rtc.RtcEngineConfig;
import com.zeewain.rtc.model.CameraConfig;
import com.zeewain.utils.CommonUtils;

import org.webrtc.SurfaceViewRenderer;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class RoomActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {

    private String roomId;
    private Boolean cameraEnable;

    private Boolean audioEnable;

    private RtcEngineConfig config;
    private RtcEngine mRtcEngine;
    protected Handler handler;

    private TextView tvAudio, tvVideo, tvChat, tvParticipant;
    private VideoReportLayout videoReportLayout;
    private static final int lineCount = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        videoReportLayout = findViewById(R.id.videoReportLayout);

        handler = new Handler(Looper.getMainLooper());
        roomId = getIntent().getStringExtra("roomId");
        String displayName = getIntent().getStringExtra("displayName");
        cameraEnable = getIntent().getBooleanExtra("cameraEnable", false);
        audioEnable = getIntent().getBooleanExtra("audioEnable", false);

        config = new RtcEngineConfig();
        config.context = RoomActivity.this;
        config.roomId = roomId;
        config.appId = getString(R.string.zwrtc_app_id);
        config.token = getString(R.string.zwrtc_token);
        config.displayName = displayName;
        config.userId = CommonUtils.getRandomString(8);
        config.eventHandler = iRtcEngineEventHandler;

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
        mRtcEngine.setupCameraConfig(new CameraConfig(CameraConfig.CAMERA_DIRECTION.CAMERA_FRONT));
        if (mRtcEngine.joinChannel() != 0) {
            finish();
        }
        if (cameraEnable) {
            mRtcEngine.enableVideo();
            setTextImageTopDrawable(R.drawable.ic_webcam_on, tvVideo);
            tvVideo.setText(getString(R.string.stop_camera));
        } else {
            setTextImageTopDrawable(R.drawable.ic_webcam_off, tvVideo);
            tvVideo.setText(getString(R.string.open_camera));
        }

        if (audioEnable) {
            mRtcEngine.enableAudio();
            setTextImageTopDrawable(R.drawable.ic_mic_on, tvAudio);
            tvAudio.setText(getString(R.string.mute));
        } else {
            setTextImageTopDrawable(R.drawable.ic_mic_off, tvAudio);
            tvAudio.setText(getString(R.string.talk));
        }

        View view = View.inflate(this, R.layout.item_player_layout, null);
        SurfaceViewRenderer surfaceView = view.findViewById(R.id.player);
        surfaceView.getLayoutParams().width = getWindowWidth() / lineCount;
        surfaceView.getLayoutParams().height = getWindowWidth() / lineCount;
        surfaceView.setVisibility(View.VISIBLE);
        TextView textView = view.findViewById(R.id.tv_consume_name);
        textView.setText(config.userId);
        view.setTag(config.userId);
        mRtcEngine.setupLocalVideo(surfaceView);
        videoReportLayout.addView(view);
    }

    private void initListener() {
        TextView tvMeetingTitle = findViewById(R.id.tv_meeting_title);
        Button btnExit = findViewById(R.id.btn_meeting_exit);
        Button btnEnd = findViewById(R.id.btn_meeting_end);
        ImageView ivSwitchCamera = findViewById(R.id.iv_meeting_switch_camera);
        tvAudio = findViewById(R.id.tv_meeting_audio);
        tvVideo = findViewById(R.id.tv_meeting_video);
        tvChat = findViewById(R.id.tv_chat);
        tvParticipant = findViewById(R.id.tv_meeting_participants);

        tvMeetingTitle.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnEnd.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);
        tvAudio.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
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
        if (v.getId() == R.id.tv_meeting_video) {
            if (mRtcEngine.hasVideoAvailable() == 0) {
                mRtcEngine.disableVideo();
                setTextImageTopDrawable(R.drawable.ic_webcam_off, tvVideo);
                tvVideo.setText(getString(R.string.open_camera));
            } else {
                mRtcEngine.enableVideo();
                setTextImageTopDrawable(R.drawable.ic_webcam_on, tvVideo);
                tvVideo.setText(getString(R.string.stop_camera));
            }
        }
        if (v.getId() == R.id.tv_meeting_audio) {
            if (mRtcEngine.hasAudioAvailable() == 0) {
                mRtcEngine.disableAudio();
                setTextImageTopDrawable(R.drawable.ic_mic_off, tvAudio);
                tvAudio.setText(getString(R.string.talk));
            } else {
                mRtcEngine.enableAudio();
                setTextImageTopDrawable(R.drawable.ic_mic_on, tvAudio);
                tvAudio.setText(getString(R.string.mute));
            }
        }
        if (v.getId() == R.id.iv_meeting_switch_camera) {
            mRtcEngine.switchCamera();
        }
        if (v.getId() == R.id.tv_chat) {
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
        public void onUserOnline(JSONArray usersInfo) {
            handler.post(() -> usersInfo.forEach(object -> {
                JSONObject jsonObject = (JSONObject) object;
                String uid = jsonObject.getString("id");
                addUserView(uid);
            }));
        }

        @Override
        public void onJoinChannelSuccess( String uid) {
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
        public void onUserJoined(String uid) {
            handler.post(() -> {
                addUserView(uid);
            });
        }

        @Override
        public void onUserOffline(String uid) {
            handler.post(() -> {
                for (int i = 0; i < videoReportLayout.getChildCount(); i++) {
                    View childView = videoReportLayout.getChildAt(i);
                    if (childView.getTag() != null) {
                        if (childView.getTag().toString().equals(uid)) {
                            videoReportLayout.removeView(childView);
                            break;
                        }
                    }
                }
            });

        }

        @Override
        public void onRemoteVideoStateChanged(String uid, String trackId, boolean state) {
            handler.post(() -> {
                if (state) {
                    for (int i = 0; i < videoReportLayout.getChildCount(); i++) {
                        View childView = videoReportLayout.getChildAt(i);
                        if (childView.getTag() != null) {
                            if (childView.getTag().toString().equals(uid)) {
                                SurfaceViewRenderer surfaceView = childView.findViewById(R.id.player);
                                surfaceView.getLayoutParams().width = getWindowWidth() / lineCount;
                                surfaceView.getLayoutParams().height = getWindowWidth() / lineCount;
                                surfaceView.setVisibility(View.VISIBLE);
                                mRtcEngine.setupRemoteVideo(surfaceView, uid);
                                return;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < videoReportLayout.getChildCount(); i++) {
                        View childView = videoReportLayout.getChildAt(i);
                        if (childView.getTag() != null) {
                            if (childView.getTag().toString().equals(uid)) {
                                SurfaceViewRenderer surfaceView = childView.findViewById(R.id.player);
                                surfaceView.release();
                                surfaceView.setVisibility(View.GONE);
                                break;
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onUserMessage(String s, String s1) {
            handler.post(() -> Toast.makeText(RoomActivity.this, s + ": " + s1, Toast.LENGTH_SHORT).show());
        }
    };

    private void addUserView(String uid) {
        View view = View.inflate(RoomActivity.this, R.layout.item_player_layout, null);
        ImageView ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        ivUserAvatar.getLayoutParams().width = getWindowWidth() / lineCount;
        ivUserAvatar.getLayoutParams().height = getWindowWidth() / lineCount;
        view.setTag(uid);
        TextView textView = view.findViewById(R.id.tv_consume_name);
        textView.setText(uid);
        videoReportLayout.addView(view);
    }

    private void setTextImageTopDrawable(int resourceId, TextView textView) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawTop = getDrawable(resourceId);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, drawTop, null, null);
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

}
