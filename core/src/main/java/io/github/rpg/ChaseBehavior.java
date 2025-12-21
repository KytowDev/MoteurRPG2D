package io.github.rpg;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ChaseBehavior implements EnemyBehavior {

    @Override
    public void act(Monster monster, Player player, Array<Rectangle> collisions, float delta) {
        if (monster.getBounds().overlaps(player.getBounds())) {
            // CORRECTION ICI : 1 dégât au lieu de 10
            player.receiveDamage(1, monster.getPosition());
        }

        float distance = monster.getPosition().dst(player.getPosition());
        if (distance < 150 && distance > 10) {
            Vector2 dir = new Vector2(player.getPosition()).sub(monster.getPosition()).nor();
            monster.move(dir.x * monster.getSpeed() * delta, dir.y * monster.getSpeed() * delta, collisions);
        }
    }
}
