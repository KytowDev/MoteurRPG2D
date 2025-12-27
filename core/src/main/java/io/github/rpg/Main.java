package io.github.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.rpg.screens.PlayScreen;
import io.github.rpg.utils.Assets;
import io.github.rpg.utils.DungeonGenerator;

public class Main extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        this.batch = new SpriteBatch();

        // 1. D'abord on lit le JSON
        io.github.rpg.utils.DataManager.load();

        // 2. Ensuite on charge les images listées dans le JSON
        Assets.load();
        Assets.manager.finishLoading();

        loadLevel("maps/hub.tmx");
    }

    public void loadLevel(String mapPath) {
        Gdx.app.postRunnable(() -> {
            if (getScreen() != null) getScreen().dispose();

            if (mapPath.equals("GENERATE")) {
                DungeonGenerator gen = new DungeonGenerator();
                // Génère 3 salles normales entre le début et la fin
                setScreen(new PlayScreen(this, gen.generate(3)));
            } else {
                setScreen(new PlayScreen(this, mapPath));
            }
        });
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        super.dispose();
        this.batch.dispose();
        Assets.dispose();
    }
}
