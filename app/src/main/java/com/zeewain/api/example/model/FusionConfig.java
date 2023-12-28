package com.zeewain.api.example.model;

import java.io.Serializable;

public class FusionConfig implements Serializable {
    private int userNumber;
    private float scale;
    private float fromBottomRatio;
    private float scaleFromLeft;
    private float scaleFromWidth;
    private int rotationAngle;

    public FusionConfig(int userNumber, float scale, float fromBottomRatio, float scaleFromLeft, float scaleFromWidth, int rotationAngle) {
        this.userNumber = userNumber;
        this.scale = scale;
        this.fromBottomRatio = fromBottomRatio;
        this.scaleFromLeft = scaleFromLeft;
        this.scaleFromWidth = scaleFromWidth;
        this.rotationAngle = rotationAngle;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(int userNumber) {
        this.userNumber = userNumber;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getFromBottomRatio() {
        return fromBottomRatio;
    }

    public void setFromBottomRatio(float fromBottomRatio) {
        this.fromBottomRatio = fromBottomRatio;
    }

    public float getScaleFromLeft() {
        return scaleFromLeft;
    }

    public void setScaleFromLeft(float scaleFromLeft) {
        this.scaleFromLeft = scaleFromLeft;
    }

    public float getScaleFromWidth() {
        return scaleFromWidth;
    }

    public void setScaleFromWidth(float scaleFromWidth) {
        this.scaleFromWidth = scaleFromWidth;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}
