package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {

    private final Weapon weapon;

    public Player(Vector2 spawn_pos, Texture idleSheet, Texture runSheet) {
        super(spawn_pos, 150f, 100);

        this.weapon = new Sword();

        int frameWidth = 16;
        int frameHeight = 28;
        this.hitbox = new Rectangle(spawn_pos.x, spawn_pos.y, frameWidth, frameHeight - 8);

        TextureRegion[][] tmpIdle = TextureRegion.split(idleSheet, frameWidth, frameHeight);
        TextureRegion[] idleFrames = new TextureRegion[4];
        for (int i=0; i < 4; i++) idleFrames[i] = tmpIdle[0][i];
        this.idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);

        TextureRegion[][] tmpRun = TextureRegion.split(runSheet, frameWidth, frameHeight);
        TextureRegion[] runFrames = new TextureRegion[4];
        for (int i=0; i < 4; i++) runFrames[i] = tmpRun[0][i];
        this.runAnim = new Animation<TextureRegion>(0.08f, runFrames);
    }

    @Override
    protected void decideNextMove(float delta, Player player, Array<Entity> allEntities) {
        weapon.update(delta);
        handleInput(allEntities);
    }

    private void handleInput(Array<Entity> targets) {
        stopMoving();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveUp();
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDown();
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveLeft();
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveRight();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            weapon.attack(this, targets);
        }
    }
}
