package io.github.rpg.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import io.github.rpg.model.EntityConfig;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

    private static final Map<String, EntityConfig> configs = new HashMap<>();

    public static void load() {
        JsonReader reader = new JsonReader();
        JsonValue root = reader.parse(Gdx.files.internal("data/entities.json"));

        for (JsonValue entry : root) {
            EntityConfig config = new EntityConfig();
            config.health = entry.getInt("health");
            config.speed = entry.getFloat("speed");
            config.damage = entry.has("damage") ? entry.getInt("damage") : 0;
            config.texturePath = entry.getString("texture");

            config.width = entry.has("width") ? entry.getInt("width") : 16;
            config.height = entry.has("height") ? entry.getInt("height") : 16;

            config.behavior = entry.has("behavior") ? entry.getString("behavior") : "stand_still";

            configs.put(entry.name, config);
        }
    }

    public static EntityConfig get(String entityName) {
        return configs.get(entityName);
    }

    public static java.util.Collection<EntityConfig> getAllConfigs() {
        return configs.values();
    }
}
