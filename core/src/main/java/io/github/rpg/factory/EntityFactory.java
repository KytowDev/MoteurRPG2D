package io.github.rpg.factory;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import io.github.rpg.model.*;
import io.github.rpg.utils.DataManager;

public class EntityFactory {

    public static Entity create(MapObject object, AssetManager assetManager) {
        String type = object.getProperties().get("type", String.class);
        float x = object.getProperties().get("x", Float.class);
        float y = object.getProperties().get("y", Float.class);
        Vector2 pos = new Vector2(x, y);

        EntityConfig config = DataManager.get(type);
        if (config == null) return null;

        if ("player".equals(type)) {
            return new Player(pos, config.health, config.speed);
        }

        EnemyBehavior behavior = getBehavior(config.behavior);

        return new Monster(
            pos,
            type,
            (int) (config.health + (GameState.getInstance().getLevel() * 5)),
            config.speed,
            config.damage,
            behavior,
            config.width,
            config.height
        );
    }

    private static EnemyBehavior getBehavior(String behaviorName) {
        try {
            String fullPath = "io.github.rpg.model." + behaviorName;
            return (EnemyBehavior) Class.forName(fullPath).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger le comportement " + behaviorName, e);
        }
    }
}
