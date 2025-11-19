package io.github.rpg;

public class StandStillBehavior implements EnemyBehavior {

    @Override
    public void act(Monster monster, Player player, float delta) {
        monster.stopMoving();
    }
}
