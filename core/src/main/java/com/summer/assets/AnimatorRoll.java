package com.summer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;;


public class AnimatorRoll {
    public Texture spriteSheet;
    public Animation<TextureRegion> RollAnimationRight;
    public Animation<TextureRegion> RollAnimationLeft;
    public float stateTime = 0;

    public AnimatorRoll(String color) {
        if(color == "b"){
            spriteSheet = new Texture(Gdx.files.internal("knight_blue.png"));
            }else {
            spriteSheet = new Texture(Gdx.files.internal("knight.png"));
        }
        // Split into 32x32 frames
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);

        // First row, idle: 6 frames (adjust if more/less)
        TextureRegion[] RollFramesRight = new TextureRegion[7];
        TextureRegion[] RollFramesLeft = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            RollFramesRight[i] = tmp[5][i];
            RollFramesLeft[i] = new TextureRegion(tmp[5][i]);
            RollFramesLeft[i].flip(true, false);
        }

        RollAnimationRight = new Animation<>(0.05f, RollFramesRight);
        RollAnimationLeft = new Animation<>(0.05f, RollFramesLeft);
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void render_left(SpriteBatch batch, float x, float y) {
        TextureRegion currentFrame = RollAnimationLeft.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - 50, y - 50, 100, 100); // offset to center
    }
    public void render_right(SpriteBatch batch, float x, float y) {
        TextureRegion currentFrame = RollAnimationRight.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - 50, y - 50, 100, 100); // offset to center
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}

