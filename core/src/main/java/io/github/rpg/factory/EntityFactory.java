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

        // 1. On charge la config depuis le JSON
        EntityConfig config = DataManager.get(type);
        if (config == null) return null;

        // Cas particulier du Joueur (classe spécifique)
        if ("player".equals(type)) {
            return new Player(pos, config.health, config.speed);
        }

        // 2. On détermine le comportement grâce au SWITCH ici
        EnemyBehavior behavior = getBehavior(config.behavior);

        // 3. On crée le monstre générique avec toutes les infos du JSON
        return new Monster(
            pos,
            type,           // Pour que le Renderer sache quelle texture charger
            config.health,
            config.speed,
            behavior,
            config.width,
            config.height
        );
    }

    // Le switch que tu voulais garder !
    private static EnemyBehavior getBehavior(String behaviorName) {
        if (behaviorName == null) return new StandStillBehavior();

        switch (behaviorName) {
            case "chase":
                return new ChaseBehavior();
            case "stand_still":
                return new StandStillBehavior();
            // Ajoutez vos futurs comportements ici (ex: "patrol", "shoot")
            default:
                return new StandStillBehavior();
        }
    }
}
