package io.github.rpg;

import com.badlogic.gdx.math.Vector2;

public class ChaseBehavior implements BehaviorStrategy {

    @Override
    public void decideMove(Entity self, Player target, float delta) {
        if (target == null) {
            self.stopMoving();
            return;
        }

        self.stopMoving();

        Vector2 selfPos = self.getPosition();
        Vector2 targetPos = target.getPosition();

        float dx = targetPos.x - selfPos.x;
        float dy = targetPos.y - selfPos.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) self.moveRight();
            else self.moveLeft();
        } else {
            if (dy > 0) self.moveUp();
            else self.moveDown();
        }
    }
}
