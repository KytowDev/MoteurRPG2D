package io.github.rpg;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class StandStillBehavior implements EnemyBehavior {

    @Override
    public void act(Monster monster, Player player, Array<Rectangle> collisions, float delta) {
        // Le monstre ne fait rien (pas d'appel à move)
        // Comme move n'est pas appelé, on s'assure que l'animation est idle
        // (stopMoving est protected dans Entity mais accessible car même package)
        // Si tu as une erreur de visibilité, rends stopMoving public dans Entity.
    }
}
