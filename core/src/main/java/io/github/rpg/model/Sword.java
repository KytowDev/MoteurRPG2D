package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Sword implements Weapon {

    private final float ATTACK_SPEED = 0.4f;        // Temps total de l'attaque (cooldown)
    private final float ATTACK_ANIM_DURATION = 0.2f;// Temps de l'animation visible
    private final int DAMAGE = 10;
    private final float RANGE = 15f;
    private final float HITBOX_HEIGHT = 28f;

    private float cooldown = 0;
    private float attackAnimTimer = 0;

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

        // calcul de la hitbox
        float hitX = user.isFacingRight()
            ? user.getPosition().x + 14
            : user.getPosition().x + 2 - RANGE;

        Rectangle damageRect = new Rectangle(hitX, user.getPosition().y, RANGE, HITBOX_HEIGHT);

        for (Entity target : targets) {
            if (target != user && damageRect.overlaps(target.getBounds())) {
                target.receiveDamage(DAMAGE, user.getPosition());
            }
        }
    }


    @Override
    public String getType() {
        return "rusty_sword";
    }

    @Override
    public boolean isAttacking() {
        return attackAnimTimer > 0;
    }

    @Override
    public float getAttackProgress() {
        return attackAnimTimer / ATTACK_ANIM_DURATION;
    }
}
