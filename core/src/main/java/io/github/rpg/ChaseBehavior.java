package io.github.rpg;

import com.badlogic.gdx.math.Vector2;

public class ChaseBehavior implements EnemyBehavior {

    @Override
    public void act(Monster monster, Player player, float delta) {
        // 1. On récupère les positions
        Vector2 monsterPos = monster.getPosition();
        Vector2 playerPos = player.getPosition();

        // 2. On calcule la distance à vol d'oiseau (Euclidienne)
        float distance = monsterPos.dst(playerPos);

        // 3. Logique de seuil simple :
        // Si on est trop loin (> 150px), on lâche l'affaire
        // Si on est trop près (< 10px), on s'arrête pour ne pas "fusionner" avec le joueur
        // Entre les deux : ON FONCE
        if (distance < 150 && distance > 10) {
            Vector2 direction = new Vector2(playerPos).sub(monsterPos);
            direction.nor(); // Normalise (transforme en flèche de longueur 1)

            monster.setVelocity(
                direction.x * monster.getSpeed(),
                direction.y * monster.getSpeed()
            );
        } else {
            monster.setVelocity(0, 0);
        }
    }
}
