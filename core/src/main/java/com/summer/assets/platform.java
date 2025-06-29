package com.summer.assets;

public class platform { //x,y represent the bottom-left point of the rectangle/
    public float x, y, width, height;
    public boolean isBorder_Base = false;

    public platform(float x, float y, float width, float height, boolean isBorder_Base) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isBorder_Base = isBorder_Base;
    }

    public boolean contains(float px, float py, float radius) {
        return (px + radius > x && px - radius < x + width &&
                py + radius > y && py - radius < y + height);
    }   
}
