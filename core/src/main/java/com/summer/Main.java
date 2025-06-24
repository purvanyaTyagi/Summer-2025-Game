package com.summer;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.summer.assets.*;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

public class Main extends ApplicationAdapter {

    public float stateTimeForRoll = 0f;
    public String chosenColor;
    public List<String> allColors = Arrays.asList("g", "b");
    public List<String> usedColors = new ArrayList<>();
    public List<String> availableColors = new ArrayList<>(allColors);
    ShapeRenderer shapeRenderer;
    DesktopNetworkHandler network_handler;
    ClientState state;
    OrthographicCamera camera;
    PhysicsHandler phy_handler;
    SpriteBatch batch;
    HashMap<String, Animator> idle_Animator = new HashMap<>();    ;
    HashMap<String, AnimatorWalk> walk_Animator = new HashMap<>();    ;
    HashMap<String, AnimatorRoll> roll_Animator = new HashMap<>();

    public Main(String host, int port){
        network_handler = new DesktopNetworkHandler(host, port);
    }

    @Override
    public void create() {
        state = new ClientState(0f, 0f, true, false, false, false, "g");
        network_handler.sendPosition(state);

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(network_handler.other_clients.size() >= 2){
            throw new RuntimeException("Maximum Client Size reached");
        }
        System.out.println(network_handler.other_clients.size());


        for(Map.Entry<InetSocketAddress, ClientState> entry : network_handler.other_clients.entrySet()){
            System.out.println(entry.getValue().color);
            usedColors.add(entry.getValue().color);
        }
        if (usedColors != null) {
            availableColors.removeAll(usedColors);  
            System.out.println(usedColors.size());
        }
        Random rand = new Random();
        chosenColor = availableColors.get(rand.nextInt(availableColors.size()));


        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        phy_handler = new PhysicsHandler(state.x, state.y, -500f, 700f, chosenColor);
        batch = new SpriteBatch();

        for(String color : allColors){
            idle_Animator.put(color, new Animator(color));
            walk_Animator.put(color, new AnimatorWalk(color));
            roll_Animator.put(color, new AnimatorRoll(color));
        }
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        // Handle input
        // if (Gdx.input.isKeyPressed(Input.Keys.W)) y += speed * dt;
        // if (Gdx.input.isKeyPressed(Input.Keys.S)) y -= speed * dt;
        // if (Gdx.input.isKeyPressed(Input.Keys.A)) x -= speed * dt;
        // if (Gdx.input.isKeyPressed(Input.Keys.D)) x += speed * dt;
        state = phy_handler.update_position(dt);

        for(String color : availableColors){
            idle_Animator.get(color).update(dt);
            walk_Animator.get(color).update(dt);
            roll_Animator.get(color).update(dt);
        }
        update_screen();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        for(String color : availableColors){
            idle_Animator.get(color).dispose();
            walk_Animator.get(color).dispose();
            roll_Animator.get(color).dispose();
        }
    }

    public void update_screen(){
        network_handler.sendPosition(state);
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        batch.begin();

        // shapeRenderer.setColor(1, 0, 0, 1);
        // shapeRenderer.rect(state.x - phy_handler.width / 2f, state.y - phy_handler.height / 2f, phy_handler.width, phy_handler.height);
        // if(phy_handler.isWalking){
        //     batch.begin();
        //     walk_Animator.render(batch, state.x, state.y);  // Your cube position
        //     batch.end();
        // }else{
        //     batch.begin();
        //     idle_Animator.render(batch, state.x, state.y);  // Your cube position
        //     batch.end();
        // }
        if(phy_handler.roll && phy_handler.FacingLeft){
            stateTimeForRoll += Gdx.graphics.getDeltaTime();
            if(stateTimeForRoll >= 0.35f){
                stateTimeForRoll = 0f;
                phy_handler.roll = false;
            }else{
                roll_Animator.get(chosenColor).render_left(batch, state.x, state.y);
                phy_handler.velocity_x -= phy_handler.x_control_speed;
            }
        }else if(phy_handler.roll && phy_handler.FacingRight){
            stateTimeForRoll += Gdx.graphics.getDeltaTime();
            if(stateTimeForRoll >= 0.35f){
                stateTimeForRoll = 0f;
                phy_handler.roll = false;
            }else{
                roll_Animator.get(chosenColor).render_left(batch, state.x, state.y);
                phy_handler.velocity_x += phy_handler.x_control_speed;
            }
        }
        else if(phy_handler.isWalking && phy_handler.FacingLeft){

            walk_Animator.get(chosenColor).render_left(batch, state.x, state.y);

        }else if(phy_handler.isWalking && phy_handler.FacingRight){

            walk_Animator.get(chosenColor).render_right(batch, state.x, state.y);

        }
        else if(phy_handler.FacingLeft){

            idle_Animator.get(chosenColor).render_left(batch, state.x, state.y);

        }else{

            idle_Animator.get(chosenColor).render_right(batch, state.x, state.y);

        }

        // if(walk_Animator.WalkAnimation.getKeyFrame(walk_Animator.stateTime, true).isFlipX() != !phy_handler.FacingRight){
        //     walk_Animator.WalkAnimation.getKeyFrame(walk_Animator.stateTime, true).flip(true, false);
        // }



        for (ClientState state : network_handler.other_clients.values()) {
            // shapeRenderer.setColor(0, 1, 0, 1);
            // shapeRenderer.rect(state.x - phy_handler.width / 2f, state.y - phy_handler.height / 2f, phy_handler.width, phy_handler.height);
            if(state.rolling && state.FacingLeft){
                roll_Animator.get(state.color).render_left(batch, state.x, state.y);
            }else if(state.rolling && state.FacingRight){
                roll_Animator.get(state.color).render_right(batch, state.x, state.y);
            }
            else if(state.isWalking && state.FacingLeft){
                walk_Animator.get(state.color).render_left(batch, state.x, state.y);
            }else if(state.isWalking && state.FacingRight){
                walk_Animator.get(state.color).render_right(batch, state.x, state.y);
            }else if(state.FacingLeft){
                idle_Animator.get(state.color).render_left(batch, state.x, state.y);   
            }else{
                idle_Animator.get(state.color).render_right(batch, state.x, state.y);
            }
        }

        for (platform p : phy_handler.platforms) {
            shapeRenderer.setColor(1, 1, 1, 1);
            shapeRenderer.rect(p.x, p.y, p.width, p.height);
            // batch.draw(p.tileRegion, p.x - p.width/2, p.y - p.height/2, p.width, p.height);
        }
        batch.end();
        shapeRenderer.end();
    }
}

