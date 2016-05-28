package org.androidchile.gamerpg.model;

/**
 * Created by pablo on 5/25/16.
 */

public class Move {
    private int fromXNode;
    private int fromYNode;
    private int toXNode;
    private int toYNode;

    public int getFromXNode() {
        return fromXNode;
    }

    public void setFromXNode(int fromXNode) {
        this.fromXNode = fromXNode;
    }

    public int getFromYNode() {
        return fromYNode;
    }

    public void setFromYNode(int fromYNode) {
        this.fromYNode = fromYNode;
    }

    public int getToXNode() {
        return toXNode;
    }

    public void setToXNode(int toXNode) {
        this.toXNode = toXNode;
    }

    public int getToYNode() {
        return toYNode;
    }

    public void setToYNode(int toYNode) {
        this.toYNode = toYNode;
    }
}
