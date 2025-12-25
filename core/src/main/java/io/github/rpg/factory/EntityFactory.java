package io.github.rpg.factory;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import io.github.rpg.model.Entity;
import io.github.rpg.model.Monster;
import io.github.rpg.model.Player;
import io.github.rpg.model.ChaseBehavior;
import io.github.rpg.model.StandStillBehavior;

public class EntityFactory {

    public static Entity create(MapObject object, AssetManager manager) {
        String type = object.getProperties().get("type", String.class);
        float x = object.getProperties().get("x", Float.class);
        float y = object.getProperties().get("y", Float.class);
        Vector2 pos = new Vector2(x, y);

        if (type == null) return null;

        switch (type) {
            case "player":
                return new Player(pos,6);

            case "big_monster":
                return new Monster(pos, "big_monster",40f, new ChaseBehavior(), 32, 36, 4);

            case "dwarf_npc":
                return new Monster(pos, "dwarf_npc", 0f, new StandStillBehavior(), 16, 28, 4);

            default:
                return null;
        }
    }
}
