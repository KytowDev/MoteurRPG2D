package io.github.rpg;

public class StandStillBehavior implements BehaviorStrategy {

    @Override
    public void decideMove(Entity self, Player target, float delta) {
        self.stopMoving();
    }
}
