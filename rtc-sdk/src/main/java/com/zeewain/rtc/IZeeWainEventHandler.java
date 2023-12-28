package com.zeewain.rtc;

import com.alibaba.fastjson.JSONArray;

public interface IZeeWainEventHandler {

    void onError(int err);

    void onJoinChannelSuccess(String channel, String uid);

    void onLeaveChannel();

    void onUserJoined(String uid);

    void onUserOnline(JSONArray usersInfo);

    void onUserOffline(String uid);

    void onRemoteVideoStateChanged(String uid, String trackId, boolean state);

    void onUserMessage(String uid, String message);

}
