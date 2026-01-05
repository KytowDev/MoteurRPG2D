package io.github.rpg.utils;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.List;

public class DungeonGenerator {

    private final TmxMapLoader loader = new TmxMapLoader();
    private int currentX = 20; //marge pour ne pas voir de bord noir à gauche

    public TiledMap generate(int roomCount) {
        TiledMap masterMap = loader.load("maps/prefabs/empty_base.tmx");

        // --- 1. DÉTECTION AUTOMATIQUE DES SALLES ---
        List<String> availableRooms = new ArrayList<>();
        int index = 1;

        // On cherche "room_1.tmx", puis "room_2.tmx", etc.
        while (Gdx.files.internal("maps/prefabs/room_" + index + ".tmx").exists()) {
            availableRooms.add("maps/prefabs/room_" + index + ".tmx");
            System.out.println("Salle détectée : room_" + index);
            index++;
        }

        pasteChunk(masterMap, "maps/prefabs/room_start.tmx", 20);
        pasteChunk(masterMap, "maps/prefabs/corridor_h.tmx", 27);

        // génération des salles aléatoires
        for (int i = 0; i < roomCount; i++) {
            String randomRoomPath = availableRooms.get(MathUtils.random(availableRooms.size() - 1));
            pasteChunk(masterMap, randomRoomPath, 20);
            pasteChunk(masterMap, "maps/prefabs/corridor_h.tmx", 27);
        }

        pasteChunk(masterMap, "maps/prefabs/room_end.tmx", 20);

        return masterMap;
    }

    private void pasteChunk(TiledMap master, String path, int yOffset) {
        TiledMap chunk = loader.load(path);
        int width = ((TiledMapTileLayer) chunk.getLayers().get(0)).getWidth();

        copyLayer(chunk, master, "floor", width, yOffset);
        copyLayer(chunk, master, "walls", width, yOffset);
        copyLayer(chunk, master, "demi_walls", width, yOffset);
        copyObjects(chunk, master, "entities", width, yOffset);
        copyObjects(chunk, master, "collisions", width, yOffset);

        currentX += width;
    }

    private void copyLayer(TiledMap src, TiledMap dest, String layerName, int width, int yOffset) {
        TiledMapTileLayer srcLayer = (TiledMapTileLayer) src.getLayers().get(layerName);
        TiledMapTileLayer destLayer = (TiledMapTileLayer) dest.getLayers().get(layerName);
        if (srcLayer == null || destLayer == null) return;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < srcLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = srcLayer.getCell(x, y);
                if (cell != null) destLayer.setCell(currentX + x, y + yOffset, cell);
            }
        }
    }

    private void copyObjects(TiledMap src, TiledMap dest, String layerName, int width, int yOffset) {
        MapLayer srcLayer = src.getLayers().get(layerName);
        MapLayer destLayer = dest.getLayers().get(layerName);
        if (srcLayer == null || destLayer == null) return;

        float tileWidth = 16f;
        float pixelOffsetX = currentX * tileWidth;
        float pixelOffsetY = yOffset * tileWidth;

        for (MapObject obj : srcLayer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();

                // 1. Calcul de la vraie position dans le monde
                Rectangle newRect = new Rectangle(rect.x + pixelOffsetX, rect.y + pixelOffsetY, rect.width, rect.height);
                RectangleMapObject newObj = new RectangleMapObject(newRect.x, newRect.y, newRect.width, newRect.height);

                // 2. Copie des propriétés (qui contient malheureusement les vieilles valeurs x/y)
                Iterator<String> keys = obj.getProperties().getKeys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    newObj.getProperties().put(key, obj.getProperties().get(key));
                }

                // 3. CORRECTION CRUCIALE : On force la mise à jour des propriétés X et Y
                // pour que PlayScreen lise la bonne valeur décalée et pas la valeur locale du prefab
                newObj.getProperties().put("x", newRect.x);
                newObj.getProperties().put("y", newRect.y);

                destLayer.getObjects().add(newObj);
            }
        }
    }
}
