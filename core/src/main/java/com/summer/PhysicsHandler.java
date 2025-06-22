package com.summer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

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
    final float lower_bound_y = -485f;
    final float upper_bound_y = 485f;
    final float lower_bound_x = -735f;
    final float upper_bound_x = 735f;
    final float x_control_speed = 10f;


    public PhysicsHandler(float x, float y, float grav, float jump_pow){
        this.x = x;
        this.y = y;
        this.grav = grav;
        this.jump_pow = jump_pow;
        velocity_x = initial_velocity_x;
        velocity_y = initial_velocity_y;
    }

    public ClientState update_position(float delta_time){
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            velocity_y += jump_pow;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && this.y <= lower_bound_y + 5f) velocity_x -= x_control_speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D) && this.y <= lower_bound_y + 5f) velocity_x += x_control_speed;   

        velocity_y += mass*(grav + accel_y)*delta_time;
        velocity_x += mass*(accel_x)*delta_time;
        if(this.y <= lower_bound_y + 2f){
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
        return new ClientState(x, y);
    }
}
