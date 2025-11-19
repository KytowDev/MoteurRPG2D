package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {

    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 180;

    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;

    private final Array<Entity> entities;
    private final Array<Rectangle> collisionRects;
    private final Array<Portal> portals;
    private Player player;

    private final Texture playerIdleSheet;
    private final Texture playerRunSheet;
    private final Texture bigMonsterIdleSheet;
    private final Texture bigMonsterRunSheet;
    private final Texture dwarfIdleSheet;
    private final Texture dwarfRunSheet;

    public PlayScreen(Main game, String mapPath) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        this.map = new TmxMapLoader().load(mapPath, params);

        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        this.playerIdleSheet = new Texture("anims/knight/knight_idle.png");
        this.playerRunSheet = new Texture("anims/knight/knight_run.png");
        this.bigMonsterIdleSheet = new Texture("anims/bigmonster/bigmonster_idle.png");
        this.bigMonsterRunSheet = new Texture("anims/bigmonster/bigmonster_run.png");
        this.dwarfIdleSheet = new Texture("anims/dwarf_m/dwarf_m_idle.png");
        this.dwarfRunSheet = new Texture("anims/dwarf_m/dwarf_m_run.png");

        this.entities = new Array<Entity>();
        this.collisionRects = new Array<Rectangle>();
        this.portals = new Array<Portal>();

        loadMapObjects();
    }

    private void loadMapObjects() {
        for (MapObject object : map.getLayers().get("entities").getObjects()) {
            processMapObject(object);
        }
        for (MapObject object : map.getLayers().get("collisions").getObjects()) {
            collisionRects.add(((RectangleMapObject) object).getRectangle());
        }
    }

    private void processMapObject(MapObject object) {
        float x = object.getProperties().get("x", Float.class);
        float y = object.getProperties().get("y", Float.class);
        String type = object.getProperties().get("type", String.class);

        switch (type) {
            case "player":
                setupPlayer(new Vector2(x, y));
                break;
            case "portal":
                createPortal(object);
                break;
            case "big_monster":
                entities.add(new Monster(new Vector2(x, y), bigMonsterIdleSheet, bigMonsterRunSheet, 40f, new ChaseBehavior(), 32, 36, 4));
                break;
            case "dwarf_npc":
                entities.add(new Monster(new Vector2(x, y), dwarfIdleSheet, dwarfRunSheet, 75f, new StandStillBehavior(), 16, 28, 4));
                break;
        }
    }

    private void createPortal(MapObject object) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();
        String dest = object.getProperties().get("destination", String.class);
        portals.add(new Portal(rect, dest));
    }

    private void setupPlayer(Vector2 pos) {
        this.player = new Player(pos, playerIdleSheet, playerRunSheet);
        this.entities.add(this.player);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        drawEntities();
        game.batch.end();
    }

    private void update(float delta) {
        updateEntities(delta);
        checkPortals();

        float x = Math.round(player.getPosition().x * 100) / 100f;
        float y = Math.round(player.getPosition().y * 100) / 100f;
        camera.position.set(x, y, 0);

        camera.update();
    }

    private void checkPortals() {
        for (Portal portal : portals) {
            if (player.getBounds().overlaps(portal.bounds)) {
                game.loadLevel(portal.destination);
            }
        }
    }

    private void updateEntities(float delta) {
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update(delta, collisionRects, player, entities);
            if (entity.isDead()) {
                entities.removeIndex(i);
            }
        }
    }

    private void drawEntities() {
        for (Entity entity : entities) {
            entity.render(game.batch);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        playerIdleSheet.dispose();
        playerRunSheet.dispose();
        bigMonsterIdleSheet.dispose();
        bigMonsterRunSheet.dispose();
        dwarfIdleSheet.dispose();
        dwarfRunSheet.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
