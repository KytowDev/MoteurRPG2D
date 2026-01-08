package io.github.rpg.model;

public interface InteractableStrategy extends EnemyBehavior {
    boolean onInteract(Monster me, Player player);
}
