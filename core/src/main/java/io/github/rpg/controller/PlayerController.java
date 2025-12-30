package io.github.rpg.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.rpg.model.Entity;
import io.github.rpg.model.Player;

public class PlayerController {

    private final Player player;

    public PlayerController(Player player) {
        this.player = player;
    }

    public void update(float delta, Array<Rectangle> collisions, Array<Entity> targets) {

        player.setMoving(false);
        // On récupère la vitesse définie dans le JSON via le getter du Player

        float dist = player.getSpeed() * delta;

        // --- 1. Déplacements (ZQSD) ---
        // On appelle directement les méthodes publiques de 'Entity' (move)
        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.move(0, dist, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.move(0, -dist, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.move(-dist, 0, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.move(dist, 0, collisions);

        // --- 2. Orientation (Regarder la souris) ---
        if (Gdx.input.getX() > Gdx.graphics.getWidth() / 2f) {
            player.setFacingRight(true);
        } else {
            player.setFacingRight(false);
        }

        // --- 3. Attaque ---
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // On récupère l'arme via le getter pour lancer l'attaque
            player.getWeapon().attack(player, targets);
        }
    }
}
