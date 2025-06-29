package com.summer.assets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlatformGenerator {
    public static float platform_tile_ratio = 16f/21f;
    public static int single_platform_width = 16 * 4;
    public static int single_platform_height = 21 * 4;
    public static List<platform> generateStackedPlatforms(
        int screenWidth,
        int startY,
        int platformCount,
        int verticalSpacing,
        CopyOnWriteArrayList<platform> platforms
    ) {
        platforms.add(new platform(-750, -520, 1500, 20, true));
        platforms.add(new platform(-770, -480, 20, 980, true));
        platforms.add(new platform(750, -480, 20, 980, true));
        
        Random rand = new Random();
    
        float minSeparation = 500;
        float maxSeparation = 600;
    
        float screenLeft = -screenWidth / 2f + 200f;
        float screenRight = screenWidth / 2f - 90f;
    
        float previousX = 0;
        boolean side_facing = true;
    
        for (int i = 0; i < platformCount; i++) {
            //float width = rand.nextInt(120) + 150; // width 80–200
            float width = getRandomMultipleInRange(80, 300, single_platform_width, rand);
            float height = single_platform_height;
    
            // Pick a horizontal offset from previousX within min–max separation
            float deltaX = (minSeparation + width) + rand.nextFloat() * (maxSeparation - minSeparation);
    
            // Randomly decide to go left or right
            if (side_facing) deltaX = -deltaX;
    
            float x = previousX + deltaX;
    
            // Clamp to screen bounds
            x = Math.max(screenLeft, Math.min(screenRight - width + 40f, x));
    
            float y = startY + i * verticalSpacing;
    
            platforms.add(new platform(x, y, width, height, false));
            previousX = x; // update for next iteration
            side_facing = !side_facing;
        }

        //platforms.add(new platform(0f, 0f, 16 * 4f, 21* 4f));

        return platforms;
    }

    public static int getRandomMultipleInRange(int min, int max, int divisor, Random rand) {
        int first = ((min + divisor - 1) / divisor) * divisor; // first multiple >= min
        int last = (max / divisor) * divisor;                 // last multiple <= max
    
        if (first > last) {
            throw new IllegalArgumentException("No multiples of " + divisor + " within range " + min + "-" + max);
        }
    
        int count = ((last - first) / divisor) + 1;
        int index = rand.nextInt(count);
        return first + index * divisor;
    }
    
}
