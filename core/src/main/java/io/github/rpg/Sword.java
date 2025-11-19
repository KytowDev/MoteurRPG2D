package io.github.rpg;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Sword implements Weapon {

    private float cooldown = 0;
    private final float attackSpeed = 0.5f;
    private final int damage = 25;
    private final float range = 24f;

    @Override
    public void update(float delta) {
        if (cooldown > 0) {
            cooldown -= delta;
        }
    }

    @Override
    public void attack(Entity user, Array<Entity> targets) {
        if (cooldown > 0) return;

        cooldown = attackSpeed;

        float hitX = user.facingRight ? user.getPosition().x + 16 : user.getPosition().x - range;
        float hitY = user.getPosition().y;
        Rectangle damageRect = new Rectangle(hitX, hitY, range, 28);

        for (Entity target : targets) {
            if (target == user) continue;

            if (damageRect.overlaps(target.getBounds())) {
                target.takeDamage(damage);
            }
        }
    }
}
