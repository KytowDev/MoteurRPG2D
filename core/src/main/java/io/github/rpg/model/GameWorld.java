package io.github.rpg.model;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import io.github.rpg.controller.PlayerController;
import io.github.rpg.factory.EntityFactory;
import io.github.rpg.utils.Assets;

import java.util.function.Consumer;

public class GameWorld implements Disposable {

    private final TiledMap map;
    private final Array<Entity> entities;
    private final Array<Rectangle> collisionRects;
    private final Array<Portal> portals;
    private Player player;
    private PlayerController controller;
    private Entity interactableEntity;

    private final Consumer<String> onLevelChangeRequest;

    public GameWorld(String mapPath, Consumer<String> onLevelChangeRequest) {
        this.onLevelChangeRequest = onLevelChangeRequest;
        this.entities = new Array<>();
        this.collisionRects = new Array<>();
        this.portals = new Array<>();

        this.map = loadMap(mapPath);

        loadWallTiles();
        loadDemiWallTiles();
        loadMapObjects();
    }

    // constructeur alternatif pour une map déjà générée
    public GameWorld(TiledMap map, Consumer<String> onLevelChangeRequest) {
        this.onLevelChangeRequest = onLevelChangeRequest;
        this.entities = new Array<>();
        this.collisionRects = new Array<>();
        this.portals = new Array<>();
        this.map = map;

        loadWallTiles();
        loadDemiWallTiles();
        loadMapObjects();
    }

    private TiledMap loadMap(String path) {
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;
        params.textureMagFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Nearest;

        return new TmxMapLoader().load(path, params);
    }

    public void update(float delta) {
        if (player == null) return;

        if (player.isDead()) {
            onLevelChangeRequest.accept("maps/hub.tmx");
            return;
        }

        if (controller != null) {
            controller.update(delta, collisionRects, entities);
        }

        updateEntities(delta);

        GameState.getInstance().update(delta);
        updateInteractableEntity();

        checkPortals();

        if(player != null) {
            GameState.getInstance().setCurrentHealth(player.getHealth());
        }
    }

    private void updateInteractableEntity() {
        interactableEntity = null;
        if (player == null) return;

        float interactionRange = 30f;
        float minDst = Float.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof Monster && ((Monster)entity).hasInteractionStrategy()) {
                float dst = player.getPosition().dst(entity.getPosition());
                if (dst < interactionRange && dst < minDst) {
                    minDst = dst;
                    interactableEntity = entity;
                }
            }
        }
    }

    public Entity getInteractableEntity() {
        return interactableEntity;
    }

    private void updateEntities(float delta) {
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update(delta, collisionRects, player, entities);

            if (entity.isDead()) {
                if (entity instanceof Monster) {
                    GameState.getInstance().addCoin();
                }
                entities.removeIndex(i);
            }
        }
    }

    private void checkPortals() {
        for (Portal portal : portals) {
            if (player.getBounds().overlaps(portal.getBounds())) {
                onLevelChangeRequest.accept(portal.getDestination());
                break;
            }
        }
    }

    private void loadWallTiles() {
        parseLayerCollisions("walls", 0, 0, 0, 0);
    }

    private void loadDemiWallTiles() {
        float tw = 16;
        float th = 16;
        float rectW = 5;
        float rectH = 16;
        float offsetX = (tw - rectW) / 2f;
        float offsetY = (th - rectH) / 2f;
        parseLayerCollisions("demi_walls", offsetX, offsetY, rectW, rectH);
    }

    private void parseLayerCollisions(String layerName, float offX, float offY, float overrideW, float overrideH) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        if (layer == null) return;
        float tw = layer.getTileWidth();
        float th = layer.getTileHeight();

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) {
                    float w = (overrideW > 0) ? overrideW : tw;
                    float h = (overrideH > 0) ? overrideH : th;
                    collisionRects.add(new Rectangle(x * tw + offX, y * th + offY, w, h));
                }
            }
        }
    }

    private void loadMapObjects() {
        if (map.getLayers().get("entities") != null) {
            for (MapObject object : map.getLayers().get("entities").getObjects()) {
                processMapObject(object);
            }
        }
        if (map.getLayers().get("collisions") != null) {
            for (MapObject object : map.getLayers().get("collisions").getObjects()) {
                collisionRects.add(((RectangleMapObject) object).getRectangle());
            }
        }
    }

    private void processMapObject(MapObject object) {
        String type = object.getProperties().get("type", String.class);

        if ("portal".equals(type)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            String dest = object.getProperties().get("destination", String.class);
            portals.add(new Portal(rect, dest));
            return;
        }

        Entity entity = EntityFactory.create(object, Assets.manager);
        if (entity != null) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                if (GameState.getInstance().getCurrentHealth() > 0) {
                    player.setHealth(GameState.getInstance().getCurrentHealth());
                }
                this.controller = new PlayerController(this.player);
            }
            entities.add(entity);
        }
    }

    public TiledMap getMap() { return map; }
    public Array<Entity> getEntities() { return entities; }
    public Player getPlayer() { return player; }

    @Override
    public void dispose() {
        map.dispose();
    }
}
