package io.github.rpg;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class Entity {

    public enum State {
        IDLE,
        RUNNING
    }

    protected Vector2 position;
    protected Vector2 velocity;
    protected Rectangle hitbox;

    protected State currentState;
    protected boolean facingRight = true;
    protected float stateTime;
    protected float speed; // La vitesse N'EST PLUS une constante

    protected Animation<TextureRegion> idleAnim;
    protected Animation<TextureRegion> runAnim;

    public Entity(Vector2 spawn_pos, float speed) {
        this.position = spawn_pos;
        this.speed = speed; // La vitesse est maintenant unique à chaque entité
        this.velocity = new Vector2(0,0);
        this.stateTime = 0f;
        this.currentState = State.IDLE;
        // L'animation et la hitbox sont initialisées par l'enfant (Player, Monster)
    }

    public final void update(float delta, Array<Rectangle> collisionRects, Player player) {
        stateTime += delta;

        decideNextMove(delta, player);
        applyCollisions(delta, collisionRects);
    }

    protected abstract void decideNextMove(float delta, Player player);

    protected void applyCollisions(float delta, Array<Rectangle> collisionRects) {
        if (this.hitbox == null) return; // Sécurité si l'enfant n'a pas de hitbox

        float moveX = velocity.x * delta;
        hitbox.x += moveX;

        for (Rectangle rect : collisionRects) {
            if (hitbox.overlaps(rect)) {
                hitbox.x -= moveX;
                velocity.x = 0;
                break;
            }
        }

        float moveY = velocity.y * delta;
        hitbox.y += moveY;

        for (Rectangle rect : collisionRects) {
            if (hitbox.overlaps(rect)) {
                hitbox.y -= moveY;
                velocity.y = 0;
                break;
            }
        }

        // C'est la version corrigée qui gère l'offset
        position.set(hitbox.x, hitbox.y);
    }

    public void moveUp() {
        this.velocity.y = this.speed; // Utilise la vitesse de l'instance
        this.currentState = State.RUNNING;
    }
    public void moveDown() {
        this.velocity.y = -this.speed;
        this.currentState = State.RUNNING;
    }
    public void moveRight() {
        this.velocity.x = this.speed;
        this.currentState = State.RUNNING;
        this.facingRight = true;
    }
    public void moveLeft() {
        this.velocity.x = -this.speed;
        this.currentState = State.RUNNING;
        this.facingRight = false;
    }

    public void stopMoving() {
        if (!velocity.isZero()) {
            this.velocity.set(0,0);
            this.currentState = State.IDLE;
        }
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public void render(SpriteBatch batch) {
        if (idleAnim == null || runAnim == null) return; // Sécurité

        Animation<TextureRegion> animation;
        if (currentState == State.RUNNING) {
            animation = runAnim;
        } else {
            animation = idleAnim;
        }

        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

        if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }
        if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        // On suppose que la hitbox est aux pieds (position.y)
        // et que le constructeur de l'enfant a géré l'offset
        batch.draw(currentFrame, this.position.x, this.position.y);
    }

    public void dispose(){}
}
