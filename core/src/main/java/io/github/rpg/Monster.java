package io.github.rpg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Monster extends Entity {

    private final EnemyBehavior behavior;

    public Monster(Vector2 pos, Texture idle, Texture run, float spd, EnemyBehavior behavior, int w, int h, int frames) {
        super(pos, spd, 50);
        this.behavior = behavior;

        this.hitbox = new Rectangle(pos.x, pos.y, w, h);

        TextureRegion[][] tmpIdle = TextureRegion.split(idle, w, h);
        TextureRegion[] idleFrames = new TextureRegion[frames];
        for (int i=0; i < frames; i++) idleFrames[i] = tmpIdle[0][i];
        this.idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);

        TextureRegion[][] tmpRun = TextureRegion.split(run, w, h);
        TextureRegion[] runFrames = new TextureRegion[frames];
        for (int i=0; i < frames; i++) runFrames[i] = tmpRun[0][i];
        this.runAnim = new Animation<TextureRegion>(0.1f, runFrames);
    }

    @Override
    protected void decideNextMove(float delta, Player player, Array<Entity> allEntities) {
        behavior.act(this, player, delta);
    }
}
