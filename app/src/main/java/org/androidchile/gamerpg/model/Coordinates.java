package org.androidchile.gamerpg.model;

/**
 * Created by pablo on 5/24/16.
 */

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(){}

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}