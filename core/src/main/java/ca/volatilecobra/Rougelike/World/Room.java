package ca.volatilecobra.Rougelike.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class Room {

    public String name;
    public Vector2 size;
    public Vector2 location;
    public Tile[][] tiles;

    public List<Room> allowed_up;
    public List<Room> allowed_down;
    public List<Room> allowed_left;
    public List<Room> allowed_right;

    public List<Vector2> up_exits = new ArrayList<Vector2>();
    public List<Vector2> down_exits = new ArrayList<Vector2>();
    public List<Vector2> left_exits = new ArrayList<Vector2>();
    public List<Vector2> right_exits = new ArrayList<Vector2>();

    public HashMap<String, List<Vector2>> exits = new HashMap<>();

    public static Map<String, Room> ROOMS = new HashMap<>();
    public static Map<String, Room> BASE_ROOMS = new HashMap<>();

    public Room(String name, Vector2 size, Vector2 location) {
        this.name = name;
        this.size = size;
        this.location = location;
        this.tiles = new Tile[(int) size.x][(int) size.y];

        exits.put("up", up_exits);
        exits.put("down", down_exits);
        exits.put("left", left_exits);
        exits.put("right",right_exits);


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
        exits.clear();
        exits.put("up", up_exits);
        exits.put("down", down_exits);
        exits.put("left", left_exits);
        exits.put("right",right_exits);
    }

    public void update_tile_pos(){
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                tiles[x][y]._world_pos = new Vector2(
                    (location.x + x) * Tile.tile_size,
                    (location.y + y) * Tile.tile_size
                );

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

        r.exits = exits;

        // Deep copy of tiles
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                Tile original = tiles[x][y];
                if (original != null) {
                    Tile copy = original.copy(0);
                    copy._world_pos = new Vector2(original._world_pos); // copy position
                    r.tiles[x][y] = copy;
                }
            }
        }


        return r;
    }

    public void render(SpriteBatch spritebatch) {
        for (Tile[] tile : tiles) {
            for (Tile value : tile) {
                if (value != null) {
                    value.render(spritebatch);
                }
            }
        }

    }


}
