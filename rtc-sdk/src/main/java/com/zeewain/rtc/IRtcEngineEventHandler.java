package com.zeewain.rtc;

import com.alibaba.fastjson.JSONArray;

public abstract class IRtcEngineEventHandler implements IZeeWainEventHandler  {

    public IRtcEngineEventHandler() {

    }

    @Override
    public void onError(int err) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, String uid) {

    }

    @Override
    public void onLeaveChannel() {

    }

    @Override
    public void onUserJoined(String uid) {

    }

    @Override
    public void onUserOffline(String uid) {

    }

    @Override
    public void onRemoteVideoStateChanged(String uid, String trackId, boolean state) {

    }

    @Override
    public void onUserMessage(String uid, String message) {

    }

    @Override
    public void onUserOnline(JSONArray usersInfo) {

    }
}
