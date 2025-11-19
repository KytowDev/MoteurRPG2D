package io.github.rpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {

    public SpriteBatch batch;

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        Assets.load();
        Assets.manager.finishLoading();
        loadLevel("maps/hub.tmx");
    }

    public void loadLevel(String mapPath) {
        setScreen(new PlayScreen(this, mapPath));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.batch.dispose();
        Assets.dispose();
    }
}
