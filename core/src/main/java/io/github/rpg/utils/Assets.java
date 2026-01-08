package io.github.rpg.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import io.github.rpg.model.EntityConfig;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Assets {

    public static final AssetManager manager = new AssetManager();

    public static void load() {
        manager.load("ui/heart_full.png", Texture.class);
        manager.load("ui/heart_half.png", Texture.class);
        manager.load("ui/heart_empty.png", Texture.class);
        manager.load("swords/weapon_rusty_sword.png", Texture.class);
        manager.load("music/theme.mp3", Music.class);


        for (EntityConfig config : DataManager.getAllConfigs()) {
            if (config.texturePath != null && !config.texturePath.isEmpty()) {
                String path = config.texturePath;
                if (!path.endsWith("/")) path += "/";
                manager.load(path + "idle.png", Texture.class);
                manager.load(path + "run.png", Texture.class);
            }
        }
    }
    public static void playSound(String name) {
        if (manager.isLoaded(name, Sound.class)) {
            manager.get(name, Sound.class).play(0.5f);
        }
    }

    public static void dispose() {
        manager.dispose();
    }
}
