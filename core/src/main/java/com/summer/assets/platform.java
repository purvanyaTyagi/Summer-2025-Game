package com.summer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class platform { //x,y represent the bottom-left point of the rectangle/
    public float x, y, width, height;
    public TextureRegion tileRegion;
    Texture platformSheet = new Texture(Gdx.files.internal("platforms.png")); // Your uploaded image
    TextureRegion grassPlatform;
    TextureRegion dirtPlatform;
    TextureRegion lavaPlatform;
    TextureRegion icePlatform;
    public TextureRegion[][] tiles;

    public platform(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        tiles = TextureRegion.split(platformSheet, 16, 8);
        System.out.println(platformSheet.getWidth() + " x " + platformSheet.getHeight());
        grassPlatform = tiles[0][0]; // 1st row, 1st column
        //dirtPlatform = tiles[1][0];  // 2nd row, 1st column
        //lavaPlatform = tiles[2][0];  // etc.
        //icePlatform  = tiles[3][0];
        tileRegion = grassPlatform;
    }

    public boolean contains(float px, float py, float radius) {
        return (px + radius > x && px - radius < x + width &&
                py + radius > y && py - radius < y + height);
    }   
}
