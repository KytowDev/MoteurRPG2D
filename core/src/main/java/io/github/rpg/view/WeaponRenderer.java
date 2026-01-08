package io.github.rpg.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import io.github.rpg.utils.Assets;
import io.github.rpg.model.Entity;
import io.github.rpg.model.Weapon;

public class WeaponRenderer {

    private static final float SWING_ARC = 120f;

    public void render(Weapon weapon, Entity owner, Batch batch) {
        if (weapon == null) return;

        String texturePath = "swords/weapon_" + weapon.getType() + ".png";
        if (!Assets.manager.isLoaded(texturePath)) return;

        Texture texture = Assets.manager.get(texturePath, Texture.class);
        TextureRegion region = new TextureRegion(texture);

        float x = owner.getPosition().x + owner.getBounds().width / 2f;
        float y = owner.getPosition().y + owner.getBounds().height / 2f;
        float originX = region.getRegionWidth() / 2f;
        float originY = 0;

        float angle = 0f;
        if (owner instanceof io.github.rpg.model.Player) {
            angle = ((io.github.rpg.model.Player) owner).getAimAngle();
        } else {
            angle = owner.isFacingRight() ? -15f : 15f;
        }

        if (weapon.isAttacking()) {
            float progress = 1f - weapon.getAttackProgress();
            float wave = MathUtils.sin(progress * MathUtils.PI * 2);
            float directionMult = owner.isFacingRight() ? 1f : -1f;

            angle += wave * (SWING_ARC / 2f) * directionMult;
        }

        //if (region.isFlipX()) region.flip(true, false); inutile

        if (!owner.isFacingRight()) {
            region.flip(true, false);
        }

        batch.draw(region,
            x - originX, y - originY,
            originX, originY,
            region.getRegionWidth(), region.getRegionHeight(),
            1, 1,
            angle);
    }
}
