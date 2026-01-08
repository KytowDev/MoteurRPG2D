package io.github.rpg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.rpg.Main;
import io.github.rpg.model.Entity;
import io.github.rpg.model.GameState;
import io.github.rpg.model.GameWorld;
import io.github.rpg.utils.Assets;
import io.github.rpg.view.EntityRenderer;

public class PlayScreen implements Screen {

    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 180;

    private final Main game;
    private final GameWorld world;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthographicCamera uiCamera;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final EntityRenderer entityRenderer;
    private final BitmapFont font;
    private final Texture heartFull;
    private final Texture heartHalf;
    private final Texture heartEmpty;

    public PlayScreen(Main game, String mapPath) {
        this.game = game;

        this.world = new GameWorld(mapPath, (destination) -> game.loadLevel(destination));

        this.font = new BitmapFont();
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        this.mapRenderer = new OrthogonalTiledMapRenderer(world.getMap(), 1f);


        this.entityRenderer = new EntityRenderer();

        this.heartFull = new Texture("ui/heart_full.png");
        this.heartHalf = new Texture("ui/heart_half.png");
        this.heartEmpty = new Texture("ui/heart_empty.png");

        playMusic();
    }

    // Constructeur secondaire (pour map générée procéduralement)
    public PlayScreen(Main game, TiledMap generatedMap) {
        this.game = game;
        this.world = new GameWorld(generatedMap, (destination) -> game.loadLevel(destination));

        this.font = new BitmapFont();
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        this.mapRenderer = new OrthogonalTiledMapRenderer(world.getMap(), 1f);
        this.entityRenderer = new EntityRenderer();

        this.heartFull = new Texture("ui/heart_full.png");
        this.heartHalf = new Texture("ui/heart_half.png");
        this.heartEmpty = new Texture("ui/heart_empty.png");

        playMusic();
    }

    private void playMusic() {
        if (Assets.manager.isLoaded("music/theme.mp3")) {
            com.badlogic.gdx.audio.Music music = Assets.manager.get("music/theme.mp3", com.badlogic.gdx.audio.Music.class);
            music.setLooping(true);
            music.setVolume(0.3f);
            music.play();
        }
    }

    @Override
    public void render(float delta) {
        world.update(delta);
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (world.getPlayer() == null) return;

        Vector3 target = new Vector3(world.getPlayer().getPosition().x, world.getPlayer().getPosition().y, 0);
        camera.position.lerp(target, 6f * delta);
        camera.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        for (Entity entity : world.getEntities()) {
            entityRenderer.render(entity, game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();
        renderHUD(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        game.batch.begin();
        renderTextHUD(game.batch);
        game.batch.end();
    }

    private void handleInput() {
        if (world.getPlayer() == null) return;

        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        float playerCenterX = world.getPlayer().getPosition().x + world.getPlayer().getBounds().width / 2f;
        float playerCenterY = world.getPlayer().getPosition().y + world.getPlayer().getBounds().height / 2f;
        float angle = MathUtils.atan2(mousePos.y - playerCenterY, mousePos.x - playerCenterX) * MathUtils.radiansToDegrees - 90f;

        world.getPlayer().setAimAngle(angle);
    }

    private void renderHUD(Batch batch) {
        if(world.getPlayer() == null) return;

        int health = world.getPlayer().getHealth();
        int maxHearts = 3;
        int startX = 10;
        int startY = (int) WORLD_HEIGHT - 20;
        int padding = 2;

        for (int i = 0; i < maxHearts; i++) {
            Texture textureToDraw;
            if (health >= (i + 1) * 2) textureToDraw = heartFull;
            else if (health == (i * 2) + 1) textureToDraw = heartHalf;
            else textureToDraw = heartEmpty;

            batch.draw(textureToDraw, startX + (i * (heartFull.getWidth() + padding)), startY);
        }
    }

    private void renderTextHUD(Batch batch) {
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        String text = "NIVEAU : " + GameState.getInstance().getLevel() + "\n" +
            "MEILLEUR : " + GameState.getInstance().getBestLevel() + "\n" +
            "PIÈCES : " + GameState.getInstance().getCoins() + " $";

        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        GlyphLayout layout = new GlyphLayout(font, text);
        font.draw(batch, text, screenW - layout.width - 20, screenH - 20);

        // Notification
        String notif = GameState.getInstance().getNotificationMessage();
        if (notif != null) {
            font.setColor(Color.YELLOW);
            GlyphLayout notifLayout = new GlyphLayout(font, notif);
            font.draw(batch, notif, (screenW - notifLayout.width) / 2f, screenH - 80);
        }

        // Prompt d'interaction

        if (world.getInteractableEntity() != null) {
            String prompt = "Appuyez sur [E] pour interagir";

            font.setColor(Color.WHITE);

            GlyphLayout promptLayout = new GlyphLayout(font, prompt);

            float promptX = (screenW - promptLayout.width) / 2f;
            float promptY = 100f;

            font.draw(batch, prompt, promptX, promptY);
        }
    }

    @Override
    public void resize(int width, int height) { viewport.update(width, height); }

    @Override
    public void dispose() {
        world.dispose();

        mapRenderer.dispose();
        heartFull.dispose();
        heartHalf.dispose();
        heartEmpty.dispose();
        font.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
