package io.github.rpg.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.rpg.controller.PlayerController;
import io.github.rpg.model.Monster;
import io.github.rpg.utils.Assets;
import io.github.rpg.Main;
import io.github.rpg.model.Entity;
import io.github.rpg.model.Player;
import io.github.rpg.model.Portal;
import io.github.rpg.view.EntityRenderer; // Assurez-vous d'avoir créé ce package
import io.github.rpg.factory.EntityFactory; // Assurez-vous d'avoir créé ce package

public class PlayScreen implements Screen {

    private static final float WORLD_WIDTH = 320;
    private static final float WORLD_HEIGHT = 180;

    private final Main game;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final OrthographicCamera uiCamera;

    // Map & Rendu
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final EntityRenderer entityRenderer; // La nouvelle Vue
    private PlayerController controller;
    private final com.badlogic.gdx.graphics.g2d.BitmapFont font;


    // Modèle (Données du jeu)
    private final Array<Entity> entities;
    private final Array<Rectangle> collisionRects;
    private final Array<Portal> portals;
    private Player player;

    private com.badlogic.gdx.audio.Music music;

    // UI (Pour l'instant on garde les textures UI ici, ou on pourrait les mettre dans Assets)
    private final Texture heartFull;
    private final Texture heartHalf;
    private final Texture heartEmpty;

    public PlayScreen(Main game, String mapPath) {
        this(game, loadMap(mapPath));
        if (Assets.manager.isLoaded("music/theme.mp3")) {
            music = Assets.manager.get("music/theme.mp3", com.badlogic.gdx.audio.Music.class);
            music.setLooping(true);
            music.setVolume(0.3f);
            music.play();
        }
    }

    public PlayScreen(Main game, TiledMap map) {
        this.game = game;
        this.font = new com.badlogic.gdx.graphics.g2d.BitmapFont();

        // Caméra et Viewport
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);

        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);

        // Map
        this.map = map;
        this.mapRenderer = new OrthogonalTiledMapRenderer(map, 1f);

        // Initialisation du Renderer (La Vue)
        this.entityRenderer = new EntityRenderer();

        // UI Assets (Chargez-les ici ou via Assets.java si vous préférez)
        this.heartFull = new Texture("ui/heart_full.png");
        this.heartHalf = new Texture("ui/heart_half.png");
        this.heartEmpty = new Texture("ui/heart_empty.png");

        // Listes du Modèle
        this.entities = new Array<>();
        this.collisionRects = new Array<>();
        this.portals = new Array<>();

        // Chargement du niveau
        loadWallTiles();
        loadDemiWallTiles();
        loadMapObjects();
    }

    private static TiledMap loadMap(String path) {
        TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
        params.textureMinFilter = Texture.TextureFilter.Nearest;
        params.textureMagFilter = Texture.TextureFilter.Nearest;
        return new TmxMapLoader().load(path, params);
    }

    // --- Chargement des collisions (Murs) ---
    private void loadWallTiles() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
        if (layer == null) return;
        float tw = layer.getTileWidth();
        float th = layer.getTileHeight();
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) collisionRects.add(new Rectangle(x * tw, y * th, tw, th));
            }
        }
    }

    private void loadDemiWallTiles() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("demi_walls");
        if (layer == null) return;
        float tw = layer.getTileWidth();
        float th = layer.getTileHeight();
        float rectW = 5;
        float rectH = 16;
        float offsetX = (tw - rectW) / 2f;
        float offsetY = (th - rectH) / 2f;
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) collisionRects.add(new Rectangle(x * tw + offsetX, y * th + offsetY, rectW, rectH));
            }
        }
    }

    // --- Chargement des Objets via la Factory ---
    private void loadMapObjects() {
        if (map.getLayers().get("entities") != null) {
            for (MapObject object : map.getLayers().get("entities").getObjects()) {
                processMapObject(object);
            }
        }
        if (map.getLayers().get("collisions") != null) {
            for (MapObject object : map.getLayers().get("collisions").getObjects()) {
                collisionRects.add(((RectangleMapObject) object).getRectangle());
            }
        }
    }

    private void processMapObject(MapObject object) {
        String type = object.getProperties().get("type", String.class);

        if ("portal".equals(type)) {
            createPortal(object);
            return;
        }

        // UTILISATION DE LA FACTORY ICI !
        // Plus de switch géant, on délègue la création.
        // On passe Assets.manager pour que la factory puisse vérifier les textures si besoin,
        // ou simplement pour respecter la signature.
        Entity entity = EntityFactory.create(object, Assets.manager);
        if (entity != null) {
            if (entity instanceof Player) {
                this.player = (Player) entity;
                // 2. Création du controller dès qu'on a le joueur
                this.controller = new PlayerController(this.player);
            }
            entities.add(entity);
        }
    }

    private void createPortal(MapObject object) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();
        String dest = object.getProperties().get("destination", String.class);
        portals.add(new Portal(rect, dest));
    }

    // --- Boucle de jeu (Update & Render) ---
    @Override
    public void render(float delta) {
        update(delta);

        // Effacer l'écran
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player == null) return;

        // 1. Rendu de la carte
        mapRenderer.setView(camera);
        mapRenderer.render();

        // 2. Rendu des entités (MVC : On utilise EntityRenderer)
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // On trie les entités par Y pour un effet de profondeur simple (Z-sorting)
        // entities.sort((e1, e2) -> Float.compare(e2.getPosition().y, e1.getPosition().y));

        for (Entity entity : entities) {
            entityRenderer.render(entity, game.batch);
        }

        game.batch.end();

        // 3. Rendu de l'interface (HUD)
        game.batch.setProjectionMatrix(uiCamera.combined);
        game.batch.begin();
        renderHUD(game.batch);
        game.batch.end();

        game.batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        game.batch.begin();
        renderTextHUD(game.batch);
        game.batch.end();
    }

    private void update(float delta) {
        if (player.isDead()) {
            game.loadLevel("maps/hub.tmx");
            player = null;
            return;
        }

        if (controller != null) {
            controller.update(delta, collisionRects, entities);
        }

        // --- AJOUT : Gestion de la visée (Mouse Follow) ---
        // 1. On projette la souris dans le monde du jeu
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

        // 2. On calcule le centre du joueur
        float playerCenterX = player.getPosition().x + player.getBounds().width / 2f;
        float playerCenterY = player.getPosition().y + player.getBounds().height / 2f;

        // 3. Calcul de l'angle (Atan2)
        // Le -90f dépend de l'orientation de votre sprite d'épée original
        float angle = MathUtils.atan2(mousePos.y - playerCenterY, mousePos.x - playerCenterX) * MathUtils.radiansToDegrees - 90f;

        // 4. On met à jour le Modèle
        player.setAimAngle(angle);
        // --------------------------------------------------

        updateEntities(delta);
        checkPortals();

        Vector3 target = new Vector3(player.getPosition().x, player.getPosition().y, 0);
        camera.position.lerp(target, 0.1f);
        camera.update();
    }

    private void updateEntities(float delta) {
        // Boucle inversée pour suppression sûre
        for (int i = entities.size - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            entity.update(delta, collisionRects, player, entities);

            if (entity.isDead()) {
                if (entity instanceof Monster) {
                    // Gain de pièces (ex: 1 pièce par niveau du monstre, ou fixe)
                    io.github.rpg.model.GameState.coins += 1;
                }
                entities.removeIndex(i);
            }
        }
    }

    private void renderTextHUD(Batch batch) {
        // 1. Préparation du texte
        String text = "NIVEAU : " + io.github.rpg.model.GameState.level + "\n" +
            "BEST : " + io.github.rpg.model.GameState.bestLevel + "\n" +
            "PIÈCES : " + io.github.rpg.model.GameState.coins + " $";

        // 2. Configuration de la police (On la veut blanche et lisible)
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f); // Tu peux ajuster la taille ici (1.0f = taille native)

        // 3. Calcul de la position (Ancrage Haut-Droite)
        GlyphLayout layout = new GlyphLayout(font, text);

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();

        float x = screenW - layout.width - 20; // 20px de marge à droite
        float y = screenH - 20;                // 20px de marge en haut

        // 4. Dessin
        font.draw(batch, text, x, y);
    }
    private void checkPortals() {
        for (Portal portal : portals) {
            if (player.getBounds().overlaps(portal.bounds)) {
                game.loadLevel(portal.destination);
            }
        }
    }

    private void renderHUD(Batch batch) {
        int health = player.getHealth();
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

    @Override public void resize(int width, int height) { viewport.update(width, height); }

    @Override public void dispose() {
        map.dispose();
        mapRenderer.dispose();

        // On ne dispose plus les textures des entités ici car elles sont dans Assets !
        // Seules les textures UI locales doivent être libérées
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
