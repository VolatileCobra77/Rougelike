package ca.volatilecobra.Rougelike.Utils.Physics;

import ca.volatilecobra.Rougelike.World.Tile;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.math.Vector2;

public class Physics {
    public static Hit raycast(Vector2 start, Vector2 end, float maxDist, WorldManager worldManager, float stepSize) {
        Vector2 direction = new Vector2(end).sub(start);
        float totalDist = direction.len();

        // Clamp distance to maxDist
        boolean reachedMax = false;
        if (totalDist > maxDist) {
            totalDist = maxDist;
            reachedMax = true;
            end = new Vector2(start).mulAdd(direction.nor(), maxDist);
        } else {
            direction.nor();
        }

        int steps = Math.max(1, Math.round(totalDist / stepSize));

        for (int i = 0; i <= steps; i++) {
            float t = i / (float) steps;
            Vector2 point = new Vector2(start).mulAdd(direction, totalDist * t);

            int tileX = (int) Math.floor(point.x / Tile.tile_size);
            int tileY = (int) Math.floor(point.y / Tile.tile_size);

            Tile tile = worldManager.getTileAt(new Vector2(tileX, tileY));
            if (tile != null && tile.collides) {
                return new Hit(point, true, false); // hit obstacle
            }
        }

        return new Hit(new Vector2(end), false, !reachedMax); // no obstacle hit
    }


}
