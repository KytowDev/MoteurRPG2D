package io.github.rpg.model;

import com.badlogic.gdx.math.Rectangle;

public class Portal {

    private final Rectangle bounds;
    private final String destination;

    public Portal(Rectangle bounds, String destination) {
        this.bounds = bounds;
        this.destination = destination;
    }

    public Rectangle getBounds () { return this.bounds; }
    public String getDestination () { return this.destination; }
}
