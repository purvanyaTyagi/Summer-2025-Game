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

        for (int i = 0; i < platformCount; i++) {
            float width = rand.nextInt(120) + 80; // Platform width between 80–200
            float height = 20;

            // Clamp x so platform doesn't go off-screen
            float x = -(screenWidth/2) + rand.nextFloat() * ((screenWidth/2 - width) + (screenWidth/2));

            //float x = rand.nextFloat() * (screenWidth - width);

            // Platforms are stacked from bottom up with fixed spacing
            float y = startY + i * verticalSpacing;

            platforms.add(new platform(x, y, width, height));
        }

        return platforms;
    }
}
