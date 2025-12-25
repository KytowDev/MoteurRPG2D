package io.github.rpg.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import io.github.rpg.utils.Assets;
import io.github.rpg.model.Entity;
import io.github.rpg.model.Weapon;

public class WeaponRenderer {

    private static final float SWING_ARC = 120f; // L'épée parcourt 120 degrés

    public void render(Weapon weapon, Entity owner, Batch batch) {
        if (weapon == null) return;

        // 1. Récupération texture
        String texturePath = "swords/weapon_" + weapon.getType() + ".png";
        if (!Assets.manager.isLoaded(texturePath)) return;

        Texture texture = Assets.manager.get(texturePath, Texture.class);
        TextureRegion region = new TextureRegion(texture);

        // 2. Positionnement
        float x = owner.getPosition().x + owner.getBounds().width / 2f;
        float y = owner.getPosition().y + owner.getBounds().height / 2f;
        float originX = region.getRegionWidth() / 2f;
        float originY = 0;

        // 3. Calcul de l'angle de base
        float angle = 0f;
        if (owner instanceof io.github.rpg.model.Player) {
            angle = ((io.github.rpg.model.Player) owner).getAimAngle();
        } else {
            angle = owner.isFacingRight() ? -15f : 15f;
        }

        // --- CORRECTION : Retour à l'ancienne logique de swing ---
        if (weapon.isAttacking()) {
            float progress = 1f - weapon.getAttackProgress();

            // On remet PI * 2 pour avoir le mouvement complet (Centre -> Haut -> Bas -> Centre)
            float wave = MathUtils.sin(progress * MathUtils.PI * 2);

            // On divise l'arc par 2 pour que ça oscille de -60 à +60 autour de la souris
            // au lieu de faire 0 à 120 sur le côté.
            float directionMult = owner.isFacingRight() ? 1f : -1f; // J'ai remis 1f/-1f comme avant

            angle += wave * (SWING_ARC / 2f) * directionMult;
        }
        // ---------------------------------------------------------

        // 4. Gestion du Flip
        if (region.isFlipX()) region.flip(true, false);

        // Si on regarde à gauche, on inverse l'image horizontalement (miroir)
        if (!owner.isFacingRight()) {
            region.flip(true, false);
        }


        // 5. Dessin
        batch.draw(region,
            x - originX, y - originY,
            originX, originY,
            region.getRegionWidth(), region.getRegionHeight(),
            1, 1,
            angle);
    }
}
