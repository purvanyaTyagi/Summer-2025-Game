package com.summer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;


public class Animator {
    Texture knightSheet;
    private Texture sheet;
    private Animation<TextureRegion> idleAnimation;
    private float stateTime = 0f;

    public Animator(){
        sheet = new Texture(Gdx.files.internal("knight.png")); // your path
        TextureRegion[][] frames = TextureRegion.split(sheet, 32, 32); // 32x32 tiles

        // First row = idle animation, assuming 6 frames
        TextureRegion[] idleFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = frames[0][i];
        }

        idleAnimation = new Animation<TextureRegion>(0.1f, idleFrames); // 0.1s per frame

    }

    public TextureRegion getCurrentFrame(float deltaTime) {
        stateTime += deltaTime;
        return idleAnimation.getKeyFrame(stateTime, true); // true = looping
    }

    public void dispose() {
        sheet.dispose();
    }

}
