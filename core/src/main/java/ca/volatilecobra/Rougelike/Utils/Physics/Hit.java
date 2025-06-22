package ca.volatilecobra.Rougelike.Utils.Physics;

import com.badlogic.gdx.math.Vector2;

public class Hit {
    public Vector2 location;
    public boolean hitObstacle;
    public boolean reachedEnd;

    public Hit(Vector2 location, boolean hitObstacle, boolean reachedEnd) {
        this.location = location;
        this.hitObstacle = hitObstacle;
        this.reachedEnd = reachedEnd;
    }
}
