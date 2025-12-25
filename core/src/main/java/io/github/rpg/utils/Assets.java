package io.github.rpg.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    public static final AssetManager manager = new AssetManager();

    public static void load() {
        manager.load("anims/knight/knight_idle.png", Texture.class);
        manager.load("anims/knight/knight_run.png", Texture.class);
        manager.load("anims/bigmonster/bigmonster_idle.png", Texture.class);
        manager.load("anims/bigmonster/bigmonster_run.png", Texture.class);
        manager.load("anims/dwarf_m/dwarf_m_idle.png", Texture.class);
        manager.load("anims/dwarf_m/dwarf_m_run.png", Texture.class);
        manager.load("swords/weapon_rusty_sword.png", Texture.class);
    }

    public static void dispose() {
        manager.dispose();
    }
}
