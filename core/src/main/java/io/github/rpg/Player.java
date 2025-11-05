package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player {

    private static final float SPEED = 150f;
    private Texture sprite;
    private Vector2 position;
    private Vector2 velocity;
    private Texture sheet;
    private Animation<TextureRegion> animWalk;

    public Player(Vector2 spawn_pos, Texture sprite) {
        this.position = spawn_pos;
        this.sprite = sprite;
        this.velocity = new Vector2(0,0);
    }

    public void update(float delta) {
        position.add(velocity.cpy().scl(delta));
    }

    public void moveUp() {
        this.velocity.y = SPEED;
    }

    public void moveDown() {
        this.velocity.y = -SPEED;
    }

    public void moveRight() {
        this.velocity.x = SPEED;
    }

    public void moveLeft() {
        this.velocity.x = -SPEED;
    }

    public void stopMoving() {
        this.velocity.set(0,0);
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void render(SpriteBatch batch) {
        batch.draw(this.sprite, this.position.x, this.position.y);

    }

    public void dispose(){}
}
