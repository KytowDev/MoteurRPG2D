package io.github.rpg.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.rpg.utils.Assets;
import io.github.rpg.model.Entity;
import io.github.rpg.model.Player;
import io.github.rpg.model.Weapon;
import java.util.HashMap;
import java.util.Map;

public class EntityRenderer {

    // Cache pour ne pas recréer les animations à chaque frame
    private final Map<String, Animation<TextureRegion>> animationCache = new HashMap<>();

    private final WeaponRenderer weaponRenderer = new WeaponRenderer();

    public void render(Entity entity, Batch batch) {
        String type = entity.getType();
        boolean isMoving = entity.isMoving();
        Animation<TextureRegion> anim = getAnimation(type, isMoving);

        if (anim == null) return;

        TextureRegion currentFrame = anim.getKeyFrame(entity.getStateTime(), true);

        boolean facingRight = entity.isFacingRight();
        if (currentFrame.isFlipX() == facingRight) {
            currentFrame.flip(true, false);
        }

        boolean isColored = false;

        if (entity.getImmunityTimer() > 0) {
            if (((int)(entity.getStateTime() * 20) % 2 == 0)) {
                batch.setColor(1, 0, 0, 0.5f);
                isColored = true;
            }
        }

        batch.draw(currentFrame, entity.getPosition().x, entity.getPosition().y);

        if (isColored) {
            batch.setColor(1f, 1f, 1f, 1f);
        }

        if (entity instanceof Player) {
            Player player = (Player) entity;
            Weapon weapon = player.getWeapon();
            weaponRenderer.render(weapon, player, batch);
        }
    }

    private Animation<TextureRegion> getAnimation(String type, boolean isMoving) {
        String key = type + (isMoving ? "_run" : "_idle");

        if (animationCache.containsKey(key)) {
            return animationCache.get(key);
        }

        io.github.rpg.model.EntityConfig config = io.github.rpg.utils.DataManager.get(type);

        int frameWidth = (config != null) ? config.width : 16;
        int frameHeight = (config != null) ? config.height : 16;

        String path = getPathForType(type, isMoving);

        if (!Assets.manager.isLoaded(path)) return null;

        Texture texture = Assets.manager.get(path, Texture.class);

        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);

        float frameDuration = 0.1f;
        Animation<TextureRegion> anim = new Animation<>(frameDuration, tmp[0]);

        animationCache.put(key, anim);
        return anim;
    }

    private String getPathForType(String type, boolean isMoving) {
        io.github.rpg.model.EntityConfig config = io.github.rpg.utils.DataManager.get(type);

        if (config == null) return "anims/knight/idle.png";

        String path = config.texturePath;
        if (!path.endsWith("/")) path += "/";

        return path + (isMoving ? "run.png" : "idle.png");
    }
}
