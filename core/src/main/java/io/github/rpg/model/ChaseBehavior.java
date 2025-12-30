package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ChaseBehavior implements EnemyBehavior {

    @Override
    public void act(Monster monster, Player player, Array<Rectangle> collisions, float delta) {
        if (monster.getBounds().overlaps(player.getBounds())) {
            player.receiveDamage(monster.getDamage(), monster.getPosition());
        }

        float distance = monster.getPosition().dst(player.getPosition());
        if (distance < 150 && distance > 10) {
            Vector2 dir = new Vector2(player.getPosition()).sub(monster.getPosition()).nor();
            monster.move(dir.x * monster.getSpeed() * delta, dir.y * monster.getSpeed() * delta, collisions);
        }
    }
}
