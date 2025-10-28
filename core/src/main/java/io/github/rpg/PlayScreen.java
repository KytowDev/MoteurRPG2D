package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {

    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 320;

    private final Main game;

    private OrthographicCamera camera;
    private Viewport viewport;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    public PlayScreen(Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();

        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        this.mapLoader = new TmxMapLoader();
        this.map = mapLoader.load("maps/main.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }
}
