package com.summer.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DrawPlatformTexture {
    public float platform_tile_ratio = 16f/21f;
    public int single_platform_width = 16 * 4;
    public int single_platform_height = 21 * 4;

    Texture fullTexture = new Texture("Tileset/atlas_walls_high-16x32.png");
    TextureRegion[][] tmp = TextureRegion.split(fullTexture, 16, 32);
    public TextureRegion region = new TextureRegion(fullTexture, 0, 128 - 21,   16, 21);    
    public DrawPlatformTexture(){   
        //region = tmp[3][1];
    }
    public void drawPlatformTiled(SpriteBatch batch, platform p) {
    //batch.draw(region, p.x + 1536/2f, p.y + 1024/2f, p.width, p.height);
    int number_of_tiles = (int)(p.width / single_platform_width);
    for(int i = 0; i < number_of_tiles; i++){
        if(!p.isBorder_Base){
        batch.draw(region, p.x + 1536/2f + single_platform_width*i, p.y + 1024/2f, single_platform_width, single_platform_height);
        }
    }
    // float tileWidth = region.getRegionWidth();
    // float tileHeight = region.getRegionHeight();

    // int tilesX = (int)(p.width / tileWidth);
    // int tilesY = (int)(p.height / tileHeight);

    // for (int i = 0; i <= tilesX; i++) {
    //     for (int j = 0; j <= tilesY; j++) {
    //         float drawX = p.x + i * tileWidth;
    //         float drawY = p.y + j * tileHeight;

    //         float remainingWidth = Math.min(tileWidth, p.x + p.width - drawX);
    //         float remainingHeight = Math.min(tileHeight, p.y + p.height - drawY);

    //         // Create a temporary TextureRegion for partial tiles
    //         TextureRegion partialRegion = new TextureRegion(region);
    //         partialRegion.setRegionWidth((int)remainingWidth);
    //         partialRegion.setRegionHeight((int)remainingHeight);

    //         batch.draw(partialRegion, drawX + 1536/2f, drawY + 1024/2f, 50, 60);
    //     }
    // }
}
}
