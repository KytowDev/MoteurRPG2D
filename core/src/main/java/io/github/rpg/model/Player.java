package io.github.rpg.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import io.github.rpg.model.Sword;
import io.github.rpg.model.Weapon;

public class Player extends Entity {

    private final Weapon weapon;
    private float aimAngle = 0f; // NOUVEAU
    // Constructeur simplifié : plus de textures ici !
    public Player(Vector2 spawn_pos, int health, float speed) {
        super(spawn_pos, speed, health); // On passe la vitesse du JSON (ex: 100)
        this.type = "player";
        this.weapon = new Sword();
        this.hitbox = new Rectangle(spawn_pos.x, spawn_pos.y, 14, 20);
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        // Mise à jour de la logique de l'arme (cooldowns)
        weapon.update(delta);

        // Gestion des entrées (Clavier/Souris)
        handleInput(collisions, allEntities);
    }

    private void handleInput(Array<Rectangle> collisions, Array<Entity> targets) {
        float dist = speed * Gdx.graphics.getDeltaTime();
        isMoving = false;

        // --- 1. Déplacement (ZQSD) ---
        if (Gdx.input.isKeyPressed(Input.Keys.W)) move(0, dist, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) move(0, -dist, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) move(-dist, 0, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) move(dist, 0, collisions);

        // --- 2. Orientation (Regarder la souris) ---
        // Astuce : Comme la caméra est centrée sur le joueur, si la souris est
        // à droite du milieu de l'écran, alors le joueur regarde à droite.
        // Cela évite d'avoir besoin de l'objet "Camera" ici (plus propre).
        if (Gdx.input.getX() > Gdx.graphics.getWidth() / 2f) {
            this.setFacingRight(true); // Utilise le setter
        } else {
            this.setFacingRight(false);
        }

        // --- 3. Attaque ---
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            weapon.attack(this, targets);
        }
    }

    // Getter pour que le Renderer puisse récupérer l'arme et la dessiner
    public Weapon getWeapon() {
        return this.weapon;
    }
    public void setAimAngle(float angle) {
        this.aimAngle = angle;
    }

    public float getAimAngle() {
        return this.aimAngle;
    }

}
