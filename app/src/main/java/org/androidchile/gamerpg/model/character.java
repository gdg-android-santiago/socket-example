package org.androidchile.gamerpg.model;

import android.animation.AnimatorSet;
import android.widget.ImageView;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

/**
 * Created by pablo on 5/13/16.
 */
public class Character {
    private int viewId;
    private String userId;
    private String userName;
    private Coordinates currentPosition;
    private boolean isWalking;
    private transient ImageView imageView;
    private transient AnimatorSet viewMover;
    private transient SimpleTooltip.Builder talk;

    public Character(){}

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Coordinates getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coordinates currentPosition) {
        this.currentPosition = currentPosition;
    }

    public AnimatorSet getAnimator() {
        return viewMover;
    }

    public void setAnimator(AnimatorSet viewMover) {
        this.viewMover = viewMover;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public void setWalking(boolean walking) {
        isWalking = walking;
    }

    public SimpleTooltip.Builder getTalk() {
        return talk;
    }

    public void setTalk(SimpleTooltip.Builder talk) {
        this.talk = talk;
    }
}