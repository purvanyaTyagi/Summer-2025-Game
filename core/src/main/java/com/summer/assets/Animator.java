package com.summer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;;


public class Animator {
    public Texture spriteSheet;
    public Animation<TextureRegion> idleAnimationRight;
    public Animation<TextureRegion> idleAnimationLeft;
    public float stateTime = 0;

    public Animator(String color) {
        if(color.equals("b")){
        spriteSheet = new Texture(Gdx.files.internal("knight_blue.png"));
        }else if(color.equals("r")) {
        spriteSheet = new Texture(Gdx.files.internal("knight_Red.png"));
        }else if(color.equals("w")){
            spriteSheet = new Texture(Gdx.files.internal("knight_white.png"));
        }else{
            spriteSheet = new Texture(Gdx.files.internal("knight.png"));
        }
        // Split into 32x32 frames
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);

        // First row, idle: 6 frames (adjust if more/less)
        TextureRegion[] idleFramesRight = new TextureRegion[4];
        TextureRegion[] idleFramesLeft = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            idleFramesRight[i] = tmp[0][i];
            idleFramesLeft[i] = new TextureRegion(tmp[0][i]);
            idleFramesLeft[i].flip(true, false);
        }

        idleAnimationRight = new Animation<>(0.1f, idleFramesRight);
        idleAnimationLeft = new Animation<>(0.1f, idleFramesLeft);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void render_left(SpriteBatch batch, float x, float y) {
        TextureRegion currentFrame = idleAnimationLeft.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - 50, y - 50, 100, 100); // offset to center
    }
    public void render_right(SpriteBatch batch, float x, float y) {
        TextureRegion currentFrame = idleAnimationRight.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - 50, y - 50, 100, 100); // offset to center
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}

