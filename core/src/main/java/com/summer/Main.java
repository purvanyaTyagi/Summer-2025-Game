package com.summer;

import java.net.InetSocketAddress;
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

    ShapeRenderer shapeRenderer;
    DesktopNetworkHandler network_handler;
    ClientState state;
    OrthographicCamera camera;
    PhysicsHandler phy_handler;

    public Main(String host, int port){
        network_handler = new DesktopNetworkHandler(host, port);
    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        state = new ClientState(0f, 300f);
        phy_handler = new PhysicsHandler(state.x, state.y, -500f, 1000f);
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

        update_screen();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    public void update_screen(){
        network_handler.sendPosition(state.x, state.y);
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (ClientState state : network_handler.other_clients.values()) {
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.circle(state.x, state.y, 20);
        }

        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.circle(state.x, state.y, 20);

        for (platform p : phy_handler.platforms) {
            shapeRenderer.setColor(1, 1, 1, 1);
            shapeRenderer.rect(p.x, p.y, p.width, p.height);
        }
        shapeRenderer.end();
    }
}

