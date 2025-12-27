package io.github.rpg.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.rpg.model.EntityConfig;
import io.github.rpg.utils.DataManager;

public class Assets {

    public static final AssetManager manager = new AssetManager();

    public static void load() {
        // Textures UI fixes
        manager.load("ui/heart_full.png", Texture.class);
        manager.load("ui/heart_half.png", Texture.class);
        manager.load("ui/heart_empty.png", Texture.class);
        manager.load("swords/weapon_rusty_sword.png", Texture.class);

        // Chargement DYNAMIQUE (Nouvelle convention)
        for (EntityConfig config : DataManager.getAllConfigs()) {
            if (config.texturePath != null && !config.texturePath.isEmpty()) {
                String path = config.texturePath;

                // Sécurité : on s'assure que le chemin finit par "/"
                if (!path.endsWith("/")) path += "/";

                // On charge simplement "run.png" et "idle.png" dans ce dossier
                manager.load(path + "idle.png", Texture.class);
                manager.load(path + "run.png", Texture.class);
            }
        }
    }

    public static void dispose() {
        manager.dispose();
    }
}
