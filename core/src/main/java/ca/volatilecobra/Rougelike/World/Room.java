package ca.volatilecobra.Rougelike.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    public String name;
    public Vector2 size;
    public Vector2 location;
    public Tile[][] tiles;

    public List<Room> allowed_up;
    public List<Room> allowed_down;
    public List<Room> allowed_left;
    public List<Room> allowed_right;

    public List<Vector2> up_exits;
    public List<Vector2> down_exits;
    public List<Vector2> left_exits;
    public List<Vector2> right_exits;

    public static Map<String, Room> ROOMS = new HashMap<>();

    public Room(String name, Vector2 size, Vector2 location) {
        this.name = name;
        this.size = size;
        this.location = location;
        this.tiles = new Tile[(int) size.x][(int) size.y];

        if (ROOMS.containsKey(name)) {
            throw new IllegalStateException("Duplicate room found");
        }

        ROOMS.put(name, this);
    }

    public void setAllowedNeighbours(List<Room> up, List<Room> down, List<Room> left, List<Room> right) {
        allowed_up = up;
        allowed_down = down;
        allowed_left = left;
        allowed_right = right;
    }

    public void setExits(List<Vector2> up_exits, List<Vector2> down_exits, List<Vector2> left_exits, List<Vector2> right_exits) {
        this.left_exits = left_exits;
        this.right_exits = right_exits;
        this.up_exits = up_exits;
        this.down_exits = down_exits;
    }

    public void update_tile_pos(){
        this.location.x *= this.size.x;
        this.location.y *= this.size.y;
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                tiles[x][y]._world_pos.x += this.location.x;
                tiles[x][y]._world_pos.y += this.location.y;
            }
        }
    }

    public Room copy(int index, Vector2 position) {
        Room r = new Room(name + "_" + index, size, position);
        r.allowed_up = allowed_up;
        r.allowed_down = allowed_down;
        r.allowed_left = allowed_left;
        r.allowed_right = allowed_right;
        r.up_exits = up_exits;
        r.down_exits = down_exits;
        r.left_exits = left_exits;
        r.right_exits = right_exits;

        // Deep copy of tiles
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                Tile original = tiles[x][y];
                if (original != null) {
                    Tile copy = new Tile(original.name, original._tex);
                    copy._world_pos = new Vector2(original._world_pos); // copy position
                    r.tiles[x][y] = copy;
                }
            }
        }


        return r;
    }

    public void render(SpriteBatch spritebatch) {
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                if (tiles[x][y] != null)
                    tiles[x][y].render(spritebatch);
            }
        }
    }
}
