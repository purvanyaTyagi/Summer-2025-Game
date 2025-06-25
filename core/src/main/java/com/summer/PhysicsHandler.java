package com.summer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.summer.assets.*;

public class PhysicsHandler{ //x,y represent the middle point of the generated player rectangle.
    public float x;
    public float y;
    public float grav;
    public float initial_velocity_x = 0f;
    public float initial_velocity_y = 0f;
    public float jump_pow;

    public volatile float accel_x = 0f;
    public volatile float velocity_x = 0f;
    public volatile float accel_y = 0f;
    public volatile float velocity_y = 0f;
    final private float mass = 2f;
    final public float sprite_half = 20f;

    private float impulse_factor = 10f;
    final float lower_bound_y = -480f;
    final float upper_bound_y = 4850f;
    final float lower_bound_x = -730f;
    final float upper_bound_x = 730f;
    final float x_control_speed = 60f;
    public boolean isOnPlatform = false;

    public boolean isWalking = false;
    public boolean FacingRight = true;
    public boolean FacingLeft = false;
    public boolean roll = false;

    public String color;


    float width = 20f;  // match sprite size
    float height = 72f;

    public List<platform> platforms = new ArrayList<>();
    public PlatformGenerator platformGenerator = new PlatformGenerator();

    public PhysicsHandler(float x, float y, float grav, float jump_pow, String color){
        this.x = x;
        this.y = y;
        this.grav = grav;
        this.jump_pow = jump_pow;
        this.color = color;
        velocity_x = initial_velocity_x;
        velocity_y = initial_velocity_y;
        // platforms.add(new platform(-750, -500, 1500, 20));
        // platforms.add(new platform(-770, -480, 20, 980));
        // platforms.add(new platform(750, -480, 20, 980));
        // PlatformGenerator.generateStackedPlatforms(1500, -440, 5, 200, platforms);
        // platforms.sort(Comparator.comparingDouble(p -> p.y));
    }

    public ClientState update_position(float deltaTime, ConcurrentHashMap<Integer, platform> platforms){

        // if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        //     velocity_y += jump_pow;
        // }
        // if (Gdx.input.isKeyPressed(Input.Keys.A) && isOnPlatform()) velocity_x -= x_control_speed;
        // if (Gdx.input.isKeyPressed(Input.Keys.D) && isOnPlatform()) velocity_x += x_control_speed;   

        // velocity_y += mass*(grav + accel_y)*delta_time;
        // velocity_x += mass*(accel_x)*delta_time;
        // if(isOnPlatform()){
        //     velocity_x = velocity_x * 0.98f;
        // }
        // y += velocity_y*delta_time;
        // x += velocity_x*delta_time;

        // if (y <= lower_bound_y || y >= upper_bound_y) {
        //     y = Math.max(lower_bound_y, Math.min(y, upper_bound_y)); // clamp y
        //     velocity_y = -velocity_y / impulse_factor;
    
        //     if (Math.abs(velocity_y) < 1f) {
        //         velocity_y = 0;
        //     }
        // }if(x <= lower_bound_x || x >= upper_bound_x){
        //     x = Math.max(lower_bound_x, Math.min(x, upper_bound_x)); // clamp x
        //     velocity_x = -velocity_x / impulse_factor;
        //     if (Math.abs(velocity_x) < 1f) {
        //         velocity_x = 0;
        //     }
        // }
        // for (platform p : platforms) {
        //     if (p.contains(x, y, sprite_half)) { // ball overlaps platform
        //         // Find platform bounds
        //         float px = p.x;
        //         float py = p.y;
        //         float pw = p.width;
        //         float ph = p.height;
        
        //         // Compute penetration distances on both axes
        //         float ballLeft   = x - sprite_half;
        //         float ballRight  = x + sprite_half;
        //         float ballTop    = y + sprite_half;
        //         float ballBottom = y - sprite_half;
        
        //         float platLeft   = px;
        //         float platRight  = px + pw;
        //         float platTop    = py + ph;
        //         float platBottom = py;
        
        //         float overlapLeft   = ballRight - platLeft;
        //         float overlapRight  = platRight - ballLeft;
        //         float overlapTop    = platTop - ballBottom;
        //         float overlapBottom = ballTop - platBottom;
        
        //         // Find the smallest overlap axis
        //         float minOverlapX = Math.min(overlapLeft, overlapRight);
        //         float minOverlapY = Math.min(overlapTop, overlapBottom);
        
        //         if (minOverlapX < minOverlapY) {
        //             // Resolve horizontally
        //             if (overlapLeft < overlapRight) {
        //                 x -= overlapLeft;
        //             } else {
        //                 x += overlapRight;
        //             }
        //             velocity_x = -velocity_x / impulse_factor;
        //         } else {
        //             // Resolve vertically
        //             if (overlapBottom < overlapTop) {
        //                 y -= overlapBottom;
        //             } else {
        //                 y += overlapTop;
        //             }
        //             velocity_y = -velocity_y / impulse_factor;
        
        //             // Optional: stop tiny bouncing
        //             if (Math.abs(velocity_y) < 1f) {
        //                 velocity_y = 0;
        //             }
        //         }
        //     }
        // }

        // === Player state ===
    float halfWidth = width / 2f;
    float halfHeight = height / 2f;
    isWalking = false;

    // === Handle input ===
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)  && isOnPlatform) {
        velocity_y += jump_pow;
        isWalking = true;

    }
    if (Gdx.input.isKeyPressed(Input.Keys.A) && isOnPlatform) {
        velocity_x -= x_control_speed;
        isWalking = true;
        FacingLeft = true;
        FacingRight = false;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D) && isOnPlatform) {
        velocity_x += x_control_speed;
        isWalking = true;
        FacingLeft = false;
        FacingRight = true;
    }if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && isOnPlatform){
        roll = true;
    }
    

    // === Apply physics ===
    velocity_y += mass * grav * deltaTime;
    x += velocity_x * deltaTime;
    y += velocity_y * deltaTime;

    // === Reset ground state before checking collisions ===
    isOnPlatform = false;

    // === Platform collision handling ===
    for (platform p : platforms.values()) {
        float px = p.x;
        float py = p.y;
        float pw = p.width;
        float ph = p.height;

        float playerLeft = x - halfWidth;
        float playerRight = x + halfWidth;
        float playerTop = y + halfHeight;
        float playerBottom = y - halfHeight;

        float platLeft = px;
        float platRight = px + pw;
        float platTop = py + ph;
        float platBottom = py;

        boolean overlaps = playerRight > platLeft &&
                        playerLeft < platRight &&
                        playerTop > platBottom &&
                        playerBottom < platTop;

        if (overlaps) {
            float overlapX = Math.min(playerRight - platLeft, platRight - playerLeft);
            float overlapY = Math.min(playerTop - platBottom, platTop - playerBottom);

            if (overlapX < overlapY) {
                // Horizontal
                if (x < platLeft) x -= overlapX;
                else x += overlapX;

                velocity_x = -velocity_x / impulse_factor;
            } else {
                // Vertical
                if (y < platBottom) {
                    y -= overlapY;
                    velocity_y = -velocity_y / impulse_factor;
                } else {
                    y += overlapY;
                    velocity_y = -velocity_y / impulse_factor;
                    isOnPlatform = true;

                    if (Math.abs(velocity_y) < 1f)
                        velocity_y = 0;
                }
            }
        }
    }
    // === Friction on platform ===
    if (isOnPlatform) {
        velocity_x *= 0.9f; // friction
    }

    return new ClientState(x, y, FacingRight, isOnPlatform, isWalking, roll, color);
    }
}
