package com.summer;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.summer.assets.*;

public class PhysicsHandler{
    public float x;
    public float y;
    public float grav;
    public float initial_velocity_x = 0f;
    public float initial_velocity_y = 0f;
    public float jump_pow;

    private volatile float accel_x = 0f;
    private volatile float velocity_x = 0f;
    private volatile float accel_y = 0f;
    private volatile float velocity_y = 0f;
    final private float mass = 2f;

    private float impulse_factor = 10f;
    final float lower_bound_y = -480f;
    final float upper_bound_y = 4850f;
    final float lower_bound_x = -730f;
    final float upper_bound_x = 730f;
    final float x_control_speed = 10f;
    public List<platform> platforms = new ArrayList<>();

    public PhysicsHandler(float x, float y, float grav, float jump_pow){
        this.x = x;
        this.y = y;
        this.grav = grav;
        this.jump_pow = jump_pow;
        velocity_x = initial_velocity_x;
        velocity_y = initial_velocity_y;
        platforms.add(new platform(100, -470, 200, 20));
        platforms.add(new platform(-150, -100, 100, 20));

    }

    public ClientState update_position(float delta_time){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            velocity_y += jump_pow;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && isOnPlatform()) velocity_x -= x_control_speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D) && isOnPlatform()) velocity_x += x_control_speed;   

        velocity_y += mass*(grav + accel_y)*delta_time;
        velocity_x += mass*(accel_x)*delta_time;
        if(isOnPlatform()){
            velocity_x = velocity_x * 0.98f;
        }
        y += velocity_y*delta_time;
        x += velocity_x*delta_time;

        if (y <= lower_bound_y || y >= upper_bound_y) {
            y = Math.max(lower_bound_y, Math.min(y, upper_bound_y)); // clamp y
            velocity_y = -velocity_y / impulse_factor;
    
            if (Math.abs(velocity_y) < 1f) {
                velocity_y = 0;
            }
        }if(x <= lower_bound_x || x >= upper_bound_x){
            x = Math.max(lower_bound_x, Math.min(x, upper_bound_x)); // clamp x
            velocity_x = -velocity_x / impulse_factor;
            if (Math.abs(velocity_x) < 1f) {
                velocity_x = 0;
            }
        }
        for (platform p : platforms) {
            if (p.contains(x, y, 20)) { // ball overlaps platform
                // Find platform bounds
                float px = p.x;
                float py = p.y;
                float pw = p.width;
                float ph = p.height;
        
                // Compute penetration distances on both axes
                float ballLeft   = x - 20;
                float ballRight  = x + 20;
                float ballTop    = y + 20;
                float ballBottom = y - 20;
        
                float platLeft   = px;
                float platRight  = px + pw;
                float platTop    = py + ph;
                float platBottom = py;
        
                float overlapLeft   = ballRight - platLeft;
                float overlapRight  = platRight - ballLeft;
                float overlapTop    = platTop - ballBottom;
                float overlapBottom = ballTop - platBottom;
        
                // Find the smallest overlap axis
                float minOverlapX = Math.min(overlapLeft, overlapRight);
                float minOverlapY = Math.min(overlapTop, overlapBottom);
        
                if (minOverlapX < minOverlapY) {
                    // Resolve horizontally
                    if (overlapLeft < overlapRight) {
                        x -= overlapLeft;
                    } else {
                        x += overlapRight;
                    }
                    velocity_x = -velocity_x / impulse_factor;
                } else {
                    // Resolve vertically
                    if (overlapBottom < overlapTop) {
                        y -= overlapBottom;
                    } else {
                        y += overlapTop;
                    }
                    velocity_y = -velocity_y / impulse_factor;
        
                    // Optional: stop tiny bouncing
                    if (Math.abs(velocity_y) < 1f) {
                        velocity_y = 0;
                    }
                }
            }
        }
        
        return new ClientState(x, y);
    }

    public boolean isOnPlatform(){
        if(this.y <= lower_bound_y + 5f){
            return true;
        }else{
            for(platform p : platforms){
                if(this.y <= p.y + (p.height) + 25f && this.y >= p.y + (p.height)
                && this.x <= p.x + (p.width) && this.x >= p.x){
                    return true;
                }
            }
            return false;
        }
        
    }
}
