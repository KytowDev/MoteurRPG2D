package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity {

    public Player(Vector2 spawn_pos, Texture idleSheet, Texture runSheet) {
        super(spawn_pos, 150f); // Le joueur a une vitesse de 150

        // Le JOUEUR connaît ses propres dimensions
        int frameWidth = 16;
        int frameHeight = 28;
        int framesCount = 4;

        // Le JOUEUR crée sa propre hitbox
        int hitboxHeightReduction = 8;
        this.hitbox = new Rectangle(
            spawn_pos.x,
            spawn_pos.y,
            frameWidth,
            frameHeight - hitboxHeightReduction
        );

        // Le JOUEUR crée ses propres animations
        TextureRegion[][] tmpIdle = TextureRegion.split(idleSheet, frameWidth, frameHeight);
        TextureRegion[] idleFrames = new TextureRegion[framesCount];
        for (int i=0; i < framesCount; i++) {
            idleFrames[i] = tmpIdle[0][i];
        }
        this.idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);

        TextureRegion[][] tmpRun = TextureRegion.split(runSheet, frameWidth, frameHeight);
        TextureRegion[] runFrames = new TextureRegion[framesCount];
        for (int i=0; i < framesCount; i++) {
            runFrames[i] = tmpRun[0][i];
        }
        this.runAnim = new Animation<TextureRegion>(0.08f, runFrames);
    }

    @Override
    protected void decideNextMove(float delta, Player player) {
        handleInput();
    }

    private void handleInput() {
        stopMoving();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveUp();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            moveDown();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveLeft();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            moveRight();
        }
    }
}
