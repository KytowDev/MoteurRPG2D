package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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

    private Array<Entity> entities;
    private Player player;

    private Texture playerIdleSheet;
    private Texture playerRunSheet;

    private Texture bigMonsterIdleSheet;
    private Texture bigMonsterRunSheet;
    private Texture dwarfIdleSheet;
    private Texture dwarfRunSheet;

    private Array<Rectangle> collisionRects;

    public PlayScreen(Main game) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        this.mapLoader = new TmxMapLoader();
        this.map = mapLoader.load("maps/test.tmx");
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);
        this.batch = new SpriteBatch();

        this.playerIdleSheet = new Texture("anims/knight/knight_idle.png");
        this.playerRunSheet = new Texture("anims/knight/knight_run.png");

        this.bigMonsterIdleSheet = new Texture("anims/bigmonster/bigmonster_idle.png");
        this.bigMonsterRunSheet = new Texture("anims/bigmonster/bigmonster_run.png");

        this.dwarfIdleSheet = new Texture("anims/dwarf_m/dwarf_m_idle.png");
        this.dwarfRunSheet = new Texture("anims/dwarf_m/dwarf_m_run.png");

        this.entities = new Array<Entity>();

        loadCollisionRectangles();
        loadEntitiesFromMap();
    }

    private void loadEntitiesFromMap() {
        MapLayer objectLayer = map.getLayers().get("entities");
        if (objectLayer == null) {
            Gdx.app.log("ERREUR", "Calque 'entities' introuvable");
            return;
        }

        for (MapObject object : objectLayer.getObjects()) {
            float x = object.getProperties().get("x", Float.class);
            float y = object.getProperties().get("y", Float.class);
            Vector2 spawnPos = new Vector2(x, y);
            String type = object.getProperties().get("type", String.class);

            if (type == null) continue;

            switch (type) {
                case "player":
                    if (this.player == null) {
                        this.player = new Player(spawnPos, playerIdleSheet, playerRunSheet);
                        this.entities.add(this.player);
                    }
                    break;

                case "big_monster":
                    Monster monster = new Monster(
                        spawnPos,
                        bigMonsterIdleSheet,
                        bigMonsterRunSheet,
                        100f,
                        new ChaseBehavior(),
                        32, 36, 4
                    );
                    this.entities.add(monster);
                    break;

                case "dwarf_npc":
                    Monster npc = new Monster(
                        spawnPos,
                        dwarfIdleSheet,
                        dwarfRunSheet,
                        75f,
                        new StandStillBehavior(),
                        16, 28, 4
                    );
                    this.entities.add(npc);
                    break;
            }
        }
    }

    private void loadCollisionRectangles() {
        this.collisionRects = new Array<Rectangle>();

        MapLayer layer = map.getLayers().get("collisions");
        if (layer == null) return;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                this.collisionRects.add(rect);
            }
        }
    }

    @Override
    public void show() { }

    private void update(float delta) {
        for (Entity entity : entities) {
            entity.update(delta, collisionRects, player);
        }

        if (player != null) {
            camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        }
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
        for (Entity entity : entities) {
            entity.render(batch);
        }
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

        playerIdleSheet.dispose();
        playerRunSheet.dispose();

        bigMonsterIdleSheet.dispose();
        bigMonsterRunSheet.dispose();
        dwarfIdleSheet.dispose();
        dwarfRunSheet.dispose();
    }
}
