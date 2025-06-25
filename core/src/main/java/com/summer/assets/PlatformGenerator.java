package com.summer.assets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlatformGenerator {

    public static List<platform> generateStackedPlatforms(
        int screenWidth,
        int startY,
        int platformCount,
        int verticalSpacing,
        CopyOnWriteArrayList<platform> platforms
    ) {
        platforms.add(new platform(-750, -500, 1500, 20));
        platforms.add(new platform(-770, -480, 20, 980));
        platforms.add(new platform(750, -480, 20, 980));
        
        Random rand = new Random();
    
        float minSeparation = 100;
        float maxSeparation = 500;
    
        float screenLeft = -screenWidth / 2f;
        float screenRight = screenWidth / 2f;
    
        float previousX = 0;
        boolean side_facing = true;
    
        for (int i = 0; i < platformCount; i++) {
            float width = rand.nextInt(120) + 150; // width 80–200
            float height = 20;
    
            // Pick a horizontal offset from previousX within min–max separation
            float deltaX = (minSeparation + width) + rand.nextFloat() * (maxSeparation - minSeparation);
    
            // Randomly decide to go left or right
            if (side_facing) deltaX = -deltaX;
    
            float x = previousX + deltaX;
    
            // Clamp to screen bounds
            x = Math.max(screenLeft, Math.min(screenRight - width + 40f, x));
    
            float y = startY + i * verticalSpacing;
    
            platforms.add(new platform(x, y, width, height));
            previousX = x; // update for next iteration
            side_facing = !side_facing;
        }
    
        return platforms;
    }
    
}
