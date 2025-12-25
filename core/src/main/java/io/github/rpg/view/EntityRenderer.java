package io.github.rpg.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.rpg.utils.Assets;
import io.github.rpg.model.Entity;
import io.github.rpg.model.Player;
import io.github.rpg.model.Weapon; // Import nécessaire pour l'interface Weapon

import java.util.HashMap;
import java.util.Map;

public class EntityRenderer {

    // Cache pour ne pas recréer les animations à chaque frame
    private final Map<String, Animation<TextureRegion>> animationCache = new HashMap<>();

    // --- CORRECTION ICI : Déclaration et Instanciation du WeaponRenderer ---
    private final WeaponRenderer weaponRenderer = new WeaponRenderer();

    public void render(Entity entity, Batch batch) {
        // 1. Récupérer l'animation correspondante
        String type = entity.getType();
        boolean isMoving = entity.isMoving();
        Animation<TextureRegion> anim = getAnimation(type, isMoving);

        if (anim == null) return;

        // 2. Obtenir la frame à afficher
        TextureRegion currentFrame = anim.getKeyFrame(entity.getStateTime(), true);

        // 3. Gestion de l'orientation (Flip)
        boolean facingRight = entity.isFacingRight();
        if (currentFrame.isFlipX() != !facingRight) {
            currentFrame.flip(true, false);
        }

        // 4. Effet de dégâts (Clignotement rouge)
        // CORRECTION DU BUG ICI :
        // On ne stocke pas l'objet Color, mais on retient qu'il faut remettre à BLANC à la fin.
        // (Ou on sauvegarde les composantes r,g,b,a si vous utilisez des teintes globales)
        boolean isColored = false;

        if (entity.getImmunityTimer() > 0) {
            if (((int)(entity.getStateTime() * 20) % 2 == 0)) {
                batch.setColor(1, 0, 0, 0.5f); // Rouge semi-transparent
                isColored = true;
            }
        }

        // 5. Dessin de l'entité
        batch.draw(currentFrame, entity.getPosition().x, entity.getPosition().y);

        // CORRECTION : On remet toujours la couleur à BLANC (1, 1, 1, 1) après avoir dessiné
        if (isColored) {
            batch.setColor(1f, 1f, 1f, 1f);
        }

        // Appel au rendu de l'arme
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

        String path = getPathForType(type, isMoving);
        if (!Assets.manager.isLoaded(path)) {
            return null;
        }

        Texture texture = Assets.manager.get(path, Texture.class);
        int frameWidth = getFrameWidth(type);
        int frameHeight = texture.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);

        float frameDuration = (type.equals("player") && isMoving) ? 0.08f : 0.1f;
        Animation<TextureRegion> anim = new Animation<>(frameDuration, tmp[0]);

        animationCache.put(key, anim);
        return anim;
    }

    private int getFrameWidth(String type) {
        switch (type) {
            case "player": return 16;
            case "dwarf_npc": return 16;
            case "big_monster": return 32;
            default: return 16;
        }
    }

    private String getPathForType(String type, boolean isMoving) {
        String suffix = isMoving ? "_run.png" : "_idle.png";

        switch (type) {
            case "player":
                return "anims/knight/knight" + suffix;
            case "big_monster":
                return "anims/bigmonster/bigmonster" + suffix;
            case "dwarf_npc":
                return "anims/dwarf_m/dwarf_m" + suffix;
            default:
                return "anims/knight/knight_idle.png";
        }
    }
}
