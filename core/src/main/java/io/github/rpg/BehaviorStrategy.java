package io.github.rpg;

public interface BehaviorStrategy {
    void decideMove(Entity self, Player target, float delta);
}
