package io.github.rpg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Monster extends Entity {

    private BehaviorStrategy behavior;

    public Monster(Vector2 spawn_pos, Texture idleSheet, Texture runSheet,
                   float speed, BehaviorStrategy behavior,
                   int frameWidth, int frameHeight, int framesCount) {

        super(spawn_pos, speed); // Vitesse personnalisée
        this.behavior = behavior;

        // Le MONSTRE crée sa propre hitbox (simple, taille réelle)
        // Tu peux la réduire ici si besoin, comme pour le joueur
        this.hitbox = new Rectangle(
            spawn_pos.x,
            spawn_pos.y,
            frameWidth,
            frameHeight
        );

        // Le MONSTRE crée ses propres animations avec SES dimensions
        TextureRegion[][] tmpIdle = TextureRegion.split(idleSheet, frameWidth, frameHeight);

        // S'assure de ne pas crasher si le split échoue
        int actualFramesCount = (tmpIdle.length > 0) ? tmpIdle[0].length : 0;
        int count = Math.min(framesCount, actualFramesCount);

        TextureRegion[] idleFrames = new TextureRegion[count];
        for (int i=0; i < count; i++) {
            idleFrames[i] = tmpIdle[0][i];
        }
        this.idleAnim = new Animation<TextureRegion>(0.1f, idleFrames);

        TextureRegion[][] tmpRun = TextureRegion.split(runSheet, frameWidth, frameHeight);
        actualFramesCount = (tmpRun.length > 0) ? tmpRun[0].length : 0;
        count = Math.min(framesCount, actualFramesCount);

        TextureRegion[] runFrames = new TextureRegion[count];
        for (int i=0; i < count; i++) {
            runFrames[i] = tmpRun[0][i];
        }
        this.runAnim = new Animation<TextureRegion>(0.08f, runFrames);
    }

    @Override
    protected void decideNextMove(float delta, Player player) {
        this.behavior.decideMove(this, player, delta);
    }

    public void setBehavior(BehaviorStrategy newBehavior) {
        this.behavior = newBehavior;
    }
}
