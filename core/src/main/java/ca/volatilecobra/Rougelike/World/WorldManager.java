package ca.volatilecobra.Rougelike.World;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class WorldManager {

    private int width, height;
    private float tileSize;
    private Tile[][] worldGrid;
    private List<Tile> placedTiles = new ArrayList<>();
    private Random random = new Random();

    public WorldManager(int width, int height, float tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        worldGrid = new Tile[width][height];
        this.generate(Tile.TILES.values());
    }

    /**
     * Generate the world using the provided tile prototypes.
     * @param prototypes A collection of all available tile prototypes.
     */
    public void generate(Collection<Tile> prototypes) {
        List<Tile> tileList = new ArrayList<>(prototypes);
        if (tileList.isEmpty()){
            Tile.create_defualts();
            tileList = new ArrayList<Tile>(Tile.TILES.values());
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                List<Tile> possibleTiles = new ArrayList<>(tileList);

                // Filter by compatibility with left neighbor
                if (x > 0) {
                    Tile leftNeighbor = worldGrid[x - 1][y];
                    possibleTiles.removeIf(t -> !leftNeighbor.allowed_right.contains(t));
                }

                // Filter by compatibility with down neighbor
                if (y > 0) {
                    Tile downNeighbor = worldGrid[x][y - 1];
                    possibleTiles.removeIf(t -> !downNeighbor.allowed_up.contains(t));
                }

                if (possibleTiles.isEmpty()) {
                    System.err.println("No compatible tile found at position: " + x + "," + y + " backtracking");
                    x--;
                    y--;

                }

                // Pick random tile from remaining possibilities
                Tile chosen = possibleTiles.get(random.nextInt(possibleTiles.size()));

                // Set tile world position
                chosen._world_pos = new Vector2(x * tileSize, y * tileSize);

                // Store in grid and list
                worldGrid[x][y] = chosen;
                placedTiles.add(chosen);
            }
        }
    }

    /**
     * Render the generated world.
     * @param batch SpriteBatch to draw with.
     */
    public void render(SpriteBatch batch) {
        for (Tile tile : placedTiles) {
            tile.render(batch);
        }
    }
}
