package io.github.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.github.rpg.model.GameState;
import io.github.rpg.screens.PlayScreen;
import io.github.rpg.utils.Assets;
import io.github.rpg.utils.DungeonGenerator;

public class Main extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        this.batch = new SpriteBatch();

        io.github.rpg.utils.DataManager.load();

        Assets.load();
        Assets.manager.finishLoading();

        loadLevel("maps/hub.tmx");
    }

    public void loadLevel(String mapPath) {
        Gdx.app.postRunnable(() -> {
            if (getScreen() != null) getScreen().dispose();

            if (mapPath.equals("GENERATE")) {
                GameState.getInstance().incrementLevel();

                DungeonGenerator gen = new DungeonGenerator();
                int rooms = 4; //+ io.github.rpg.model.GameState.level;
                setScreen(new PlayScreen(this, gen.generate(rooms)));
            } else {
                if (mapPath.contains("hub")) {
                    GameState.getInstance().reset();
                }
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
