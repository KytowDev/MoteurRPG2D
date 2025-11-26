package io.github.rpg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Sword implements Weapon {

    private float cooldown = 0;
    private float attackAnimTimer = 0;
    private final float ATTACK_SPEED = 0.4f;
    private final float ATTACK_ANIM_DURATION = 0.2f;
    private final float SWING_ARC = 120f;
    private final int damage = 25;
    private final float range = 15f;
    private final float knockbackPower = 250f;
    private final TextureRegion texture;
    private final float pivotOffsetX;
    private final float pivotOffsetY = 0f;

    public Sword() {
        this.texture = new TextureRegion(new Texture("swords/weapon_rusty_sword.png"));
        this.pivotOffsetX = texture.getRegionWidth() / 2f;
    }

    @Override
    public void update(float delta) {
        if (cooldown > 0) cooldown -= delta;
        if (attackAnimTimer > 0) attackAnimTimer -= delta;
    }

    @Override
    public void attack(Entity user, Array<Entity> targets) {
        if (cooldown > 0) return;
        cooldown = ATTACK_SPEED;
        attackAnimTimer = ATTACK_ANIM_DURATION;
        float hitX = user.facingRight ? user.getPosition().x + 14 : user.getPosition().x + 2 - range;
        Rectangle damageRect = new Rectangle(hitX, user.getPosition().y, range, 28);
        for (Entity target : targets) {
            if (target != user) hitEntity(user, target, damageRect);
        }
    }

    private void hitEntity(Entity user, Entity target, Rectangle damageRect) {
        if (damageRect.overlaps(target.getBounds())) {
            target.receiveDamage(damage, user.getPosition());
        }
    }

    @Override
    public void render(Batch batch, Vector2 anchorPosition, float baseAngle, boolean facingRight) {
        float finalAngle = baseAngle;
        if (attackAnimTimer > 0) {
            float progress = 1f - (attackAnimTimer / ATTACK_ANIM_DURATION);
            float wave = MathUtils.sin(progress * MathUtils.PI * 2);
            float directionMult = facingRight ? 1f : -1f;
            finalAngle += wave * (SWING_ARC / 2f) * directionMult;
        }
        batch.draw(texture, anchorPosition.x - pivotOffsetX, anchorPosition.y - pivotOffsetY, pivotOffsetX, pivotOffsetY, texture.getRegionWidth(), texture.getRegionHeight(), 1, 1, finalAngle);
    }
}
