package io.github.rpg;

public interface EnemyBehavior {
    void act(Monster monster, Player player, float delta);
}
