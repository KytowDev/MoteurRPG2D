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
        if (currentFrame.isFlipX() == facingRight) {
            currentFrame.flip(true, false);
        }

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

        // 1. Récupération de la config pour connaitre la vraie taille du sprite
        io.github.rpg.model.EntityConfig config = io.github.rpg.utils.DataManager.get(type);

        // Si pas de config, on utilise des valeurs par défaut (ex: 16x16)
        int frameWidth = (config != null) ? config.width : 16;
        int frameHeight = (config != null) ? config.height : 16;

        // 2. Récupération du chemin de l'image
        String path = getPathForType(type, isMoving); // Votre méthode dynamique d'avant

        if (!Assets.manager.isLoaded(path)) return null;

        Texture texture = Assets.manager.get(path, Texture.class);

        // 3. Découpage avec les dimensions du JSON !
        // C'est ici que le bug se produisait (ça utilisait getFrameWidth() qui renvoyait 16)
        TextureRegion[][] tmp = TextureRegion.split(texture, frameWidth, frameHeight);

        // On prend la première ligne de l'image (tmp[0]) comme animation
        float frameDuration = 0.1f;
        Animation<TextureRegion> anim = new Animation<>(frameDuration, tmp[0]);

        animationCache.put(key, anim);
        return anim;
    }

    private String getPathForType(String type, boolean isMoving) {
        io.github.rpg.model.EntityConfig config = io.github.rpg.utils.DataManager.get(type);

        // Fallback joueur si config introuvable
        if (config == null) return "anims/knight/idle.png";

        String path = config.texturePath;
        if (!path.endsWith("/")) path += "/";

        // Nouvelle convention simple :
        return path + (isMoving ? "run.png" : "idle.png");
    }
}
