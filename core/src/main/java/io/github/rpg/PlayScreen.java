package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PlayScreen implements Screen {

    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 180;

    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthographicCamera uiCamera;
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

    private final Texture heartFull;
    private final Texture heartHalf;
    private final Texture heartEmpty;

    public PlayScreen(Main game, String mapPath) {
        this(game, loadMap(mapPath));
    }

    public PlayScreen(Main game, TiledMap map) {
        this.game = game;
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        this.map = map;
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        this.playerIdleSheet = new Texture("anims/knight/knight_idle.png");
        this.playerRunSheet = new Texture("anims/knight/knight_run.png");
        this.bigMonsterIdleSheet = new Texture("anims/bigmonster/bigmonster_idle.png");
        this.bigMonsterRunSheet = new Texture("anims/bigmonster/bigmonster_run.png");
        this.dwarfIdleSheet = new Texture("anims/dwarf_m/dwarf_m_idle.png");
        this.dwarfRunSheet = new Texture("anims/dwarf_m/dwarf_m_run.png");

        this.heartFull = new Texture("ui/heart_full.png");
        this.heartHalf = new Texture("ui/heart_half.png");
        this.heartEmpty = new Texture("ui/heart_empty.png");

        this.entities = new Array<Entity>();
        this.collisionRects = new Array<Rectangle>();
        this.portals = new Array<Portal>();

        loadWallTiles();
        loadDemiWallTiles();
        loadMapObjects();
    }

    private static TiledMap loadMap(String path) {
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        return new TmxMapLoader().load(path, params);
    }

    private void loadWallTiles() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
        if (layer == null) return;
        float tw = layer.getTileWidth();
        float th = layer.getTileHeight();
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) collisionRects.add(new Rectangle(x * tw, y * th, tw, th));
            }
        }
    }

    private void loadDemiWallTiles() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("demi_walls");
        if (layer == null) return;
        float tw = layer.getTileWidth();
        float th = layer.getTileHeight();
        float rectW = 5;
        float rectH = 16;
        float offsetX = (tw - rectW) / 2f;
        float offsetY = (th - rectH) / 2f;
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) collisionRects.add(new Rectangle(x * tw + offsetX, y * th + offsetY, rectW, rectH));
            }
        }
    }

    private void loadMapObjects() {
        if (map.getLayers().get("entities") != null) {
            for (MapObject object : map.getLayers().get("entities").getObjects()) processMapObject(object);
        }
        if (map.getLayers().get("collisions") != null) {
            for (MapObject object : map.getLayers().get("collisions").getObjects()) collisionRects.add(((RectangleMapObject) object).getRectangle());
        }
    }

    // Dans processMapObject de PlayScreen.java
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
                // Comportement ChaseBehavior (attaque)
                entities.add(new Monster(new Vector2(x, y), bigMonsterIdleSheet, bigMonsterRunSheet, 40f, new ChaseBehavior(), 32, 36, 4));
                break;
            case "dwarf_npc":
                // Comportement StandStillBehavior (pacifique)
                // Note : On utilise 'Monster' comme conteneur générique pour l'instant
                entities.add(new Monster(new Vector2(x, y), dwarfIdleSheet, dwarfRunSheet, 0f, new StandStillBehavior(), 16, 28, 4));
                break;
        }
    }

    private void createPortal(MapObject object) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();
        String dest = object.getProperties().get("destination", String.class);
        portals.add(new Portal(rect, dest));
    }

    private void setupPlayer(Vector2 pos) {
        this.player = new Player(pos, playerIdleSheet, playerRunSheet, 100f, 6);
        this.entities.add(this.player);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player == null) return;

        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        drawEntities();
        if (!player.isDead()) player.render(game.batch, camera);
        game.batch.end();

        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();
        renderHUD(game.batch);
        game.batch.end();
    }

    private void renderHUD(com.badlogic.gdx.graphics.g2d.Batch batch) {
        int health = player.getHealth();
        int maxHearts = 3;
        int startX = 10;
        int startY = (int) WORLD_HEIGHT - 20;
        int padding = 2;

        for (int i = 0; i < maxHearts; i++) {
            Texture textureToDraw;
            if (health >= (i + 1) * 2) textureToDraw = heartFull;
            else if (health == (i * 2) + 1) textureToDraw = heartHalf;
            else textureToDraw = heartEmpty;
            batch.draw(textureToDraw, startX + (i * (heartFull.getWidth() + padding)), startY);
        }
    }

    private void update(float delta) {
        if (player.isDead()) {
            game.loadLevel("maps/hub.tmx");
            player = null;
            return;
        }
        updateEntities(delta);
        checkPortals();
        Vector3 target = new Vector3(player.getPosition().x, player.getPosition().y, 0);
        camera.position.lerp(target, 0.1f);
        camera.update();
    }

    private void checkPortals() {
        for (Portal portal : portals) if (player.getBounds().overlaps(portal.bounds)) game.loadLevel(portal.destination);
    }

    private void updateEntities(float delta) {
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update(delta, collisionRects, player, entities);
            if (entity.isDead()) entities.removeIndex(i);
        }
    }

    private void drawEntities() {
        for (Entity entity : entities) entity.render(game.batch);
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void dispose() {
        map.dispose(); mapRenderer.dispose();
        playerIdleSheet.dispose(); playerRunSheet.dispose();
        bigMonsterIdleSheet.dispose(); bigMonsterRunSheet.dispose();
        dwarfIdleSheet.dispose(); dwarfRunSheet.dispose();
        heartFull.dispose(); heartHalf.dispose(); heartEmpty.dispose();
    }
    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
