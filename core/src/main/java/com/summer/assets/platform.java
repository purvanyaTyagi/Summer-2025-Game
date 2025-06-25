package com.summer.assets;

public class platform { //x,y represent the bottom-left point of the rectangle/
    public float x, y, width, height;

    public platform(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(float px, float py, float radius) {
        return (px + radius > x && px - radius < x + width &&
                py + radius > y && py - radius < y + height);
    }   
}
