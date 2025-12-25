package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;

public class Portal {

    public final Rectangle bounds;
    public final String destination;

    public Portal(Rectangle bounds, String destination) {
        this.bounds = bounds;
        this.destination = destination;
    }
}
