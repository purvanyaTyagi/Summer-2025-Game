package com.summer.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatorWalk {
    public Texture spriteSheet;
    public Animation<TextureRegion> WalkAnimationLeft;
    public Animation<TextureRegion> WalkAnimationRight;
    public float stateTime = 0;

    public AnimatorWalk(){
        spriteSheet = new Texture(Gdx.files.internal("knight.png"));
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 32, 32);

        TextureRegion[] walkFramesRight = new TextureRegion[16];
        TextureRegion[] walkFramesLeft = new TextureRegion[16];


        for (int i = 0; i < 8; i++) {
            walkFramesRight[i] = tmp[2][i];
            walkFramesRight[i + 8] = tmp[3][i];
        
            // Clone and flip
            walkFramesLeft[i] = new TextureRegion(tmp[2][i]);
            walkFramesLeft[i].flip(true, false);
        
            walkFramesLeft[i + 8] = new TextureRegion(tmp[3][i]);
            walkFramesLeft[i + 8].flip(true, false);
        }
        WalkAnimationRight = new Animation<>(0.05f, walkFramesRight);
        WalkAnimationLeft = new Animation<>(0.05f, walkFramesLeft);
    }
    public void update(float delta) {
        stateTime += delta;
    }

    public void render_left(SpriteBatch batch, float x, float y) {
        TextureRegion currentFrame = WalkAnimationLeft.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - 32, y - 32, 64, 64); // offset to center
    }
    public void render_right(SpriteBatch batch, float x, float y){
        TextureRegion currentFrame = WalkAnimationRight.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x - 32, y - 32, 64, 64); // offset to center
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
