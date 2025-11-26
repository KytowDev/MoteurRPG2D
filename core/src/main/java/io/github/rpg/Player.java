package io.github.rpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Player extends Entity {

    private final Weapon weapon;

    public Player(Vector2 spawn_pos, Texture idleSheet, Texture runSheet) {
        super(spawn_pos, 100f, 100);
        this.weapon = new Sword();
        int frameWidth = 16;
        int frameHeight = 28;
        this.hitbox = new Rectangle(spawn_pos.x, spawn_pos.y, frameWidth, frameHeight - 8);
        this.idleAnim = createAnim(idleSheet, frameWidth, frameHeight, 0.1f);
        this.runAnim = createAnim(runSheet, frameWidth, frameHeight, 0.08f);
    }

    private Animation<TextureRegion> createAnim(Texture sheet, int w, int h, float duration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, w, h);
        TextureRegion[] frames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) frames[i] = tmp[0][i];
        return new Animation<>(duration, frames);
    }

    @Override
    protected void decideNextMove(float delta, Array<Rectangle> collisions, Player player, Array<Entity> allEntities) {
        weapon.update(delta);
        handleInput(collisions, allEntities);
    }

    public void render(Batch batch, Camera camera) {
        Vector3 mousePos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        this.facingRight = mousePos.x > (pos.x + hitbox.width / 2);
        float angle = MathUtils.atan2(mousePos.y - (pos.y + hitbox.height / 2), mousePos.x - (pos.x + hitbox.width / 2)) * MathUtils.radiansToDegrees - 90f;
        weapon.render(batch, new Vector2(pos.x + hitbox.width / 2, pos.y + hitbox.height / 2), angle, facingRight);
    }

    private void handleInput(Array<Rectangle> collisions, Array<Entity> targets) {
        isMoving = false;
        float dist = speed * Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) move(0, dist, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) move(0, -dist, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) move(-dist, 0, collisions);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) move(dist, 0, collisions);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) weapon.attack(this, targets);
    }
}
