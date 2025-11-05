package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer; // <-- IMPORT manquant (ou à corriger)
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {

    private static final float WORLD_WIDTH = 256;
    private static final float WORLD_HEIGHT = 144;

    private final Main game;

    private OrthographicCamera camera;
    private Viewport viewport;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private SpriteBatch batch;
    private Player player;
    private Texture playerTexture;

    public PlayScreen(Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        this.mapLoader = new TmxMapLoader();
        this.map = mapLoader.load("maps/test.tmx");

        MapLayer objectLayer = map.getLayers().get("entities");
        MapObject spawnObject = objectLayer.getObjects().get("player_spawn");

        float spawnX = spawnObject.getProperties().get("x", Float.class);
        float spawnY = spawnObject.getProperties().get("y", Float.class);

        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        this.playerTexture = new Texture("plrtexture.png");

        this.batch = new SpriteBatch();

        Vector2 spawnPos = new Vector2(spawnX, spawnY);
        this.player = new Player(spawnPos, playerTexture);
    }

    @Override
    public void show() {

    }

    private void handleInput() {
        player.stopMoving();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.moveUp();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            player.moveDown();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.moveLeft();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            player.moveRight();
        }
    }

    private void update(float delta) {
        handleInput();
        player.update(delta);
        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        batch.dispose();
        playerTexture.dispose();
    }
}
