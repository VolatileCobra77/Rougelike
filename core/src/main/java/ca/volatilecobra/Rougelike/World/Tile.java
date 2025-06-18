package ca.volatilecobra.Rougelike.World;

import ca.volatilecobra.Rougelike.Entities.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class Tile {

    public Texture _tex;
    public Vector2 _world_pos;

    public List<Tile> allowed_up = new ArrayList<>();
    public List<Tile> allowed_down = new ArrayList<>();
    public List<Tile> allowed_left = new ArrayList<>();
    public List<Tile> allowed_right = new ArrayList<>();

    public List<Tile> allowed_up_left = new ArrayList<>();
    public List<Tile> allowed_up_right = new ArrayList<>();
    public List<Tile> allowed_down_left = new ArrayList<>();
    public List<Tile> allowed_down_right = new ArrayList<>();

    public String name;

    // Global tile registry
    public static final Map<String, Tile> TILES = new HashMap<>();

    public Tile(String name, Texture tex) {
        this.name = name;
        this._tex = tex;
        this._world_pos = new Vector2();

        if (TILES.containsKey(name)) {
            throw new IllegalArgumentException("Tile with name '" + name + "' already exists.");
        }

        TILES.put(name, this);
    }

    public static Tile getByName(String name) {
        return TILES.get(name);
    }

    public void setAllowedNeighbors(
        List<Tile> up,
        List<Tile> down,
        List<Tile> left,
        List<Tile> right,
        List<Tile> upLeft,
        List<Tile> upRight,
        List<Tile> downLeft,
        List<Tile> downRight
    ) {
        this.allowed_up = up;
        this.allowed_down = down;
        this.allowed_left = left;
        this.allowed_right = right;
        this.allowed_up_left = upLeft;
        this.allowed_up_right = upRight;
        this.allowed_down_left = downLeft;
        this.allowed_down_right = downRight;
    }
    public void render(SpriteBatch spriteBatch){
        spriteBatch.draw(_tex, _world_pos.x, _world_pos.y);
    }

    public static Tile GetFromId(String name){
        return TILES.get(name);
    }

    public static String GetIdFromInstance(Tile tile){
        for (Map.Entry<String, Tile> entry : TILES.entrySet()) {
            if (entry.getValue().equals(tile)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public static void create_defualts(){
        // Floor
        Tile floor = new Tile("floor", new Texture("tiles/floor.png"));

        // Wall - Up
        Tile wall_up_top = new Tile("wall_up_top", new Texture("tiles/wall/up_top.png"));
        Tile wall_up_bottom = new Tile("wall_up_bottom", new Texture("tiles/wall/up_bottom.png"));

        // Wall - Down
        Tile wall_down_top = new Tile("wall_down_top", new Texture("tiles/wall/down_top.png"));
        Tile wall_down_bottom = new Tile("wall_down_bottom", new Texture("tiles/wall/down_bottom.png"));

        // Wall - Left
        Tile wall_left_top = new Tile("wall_left_top", new Texture("tiles/wall/left_top.png"));
        Tile wall_left_bottom = new Tile("wall_left_bottom", new Texture("tiles/wall/left_bottom.png"));

        // Wall - Right
        Tile wall_right_top = new Tile("wall_right_top", new Texture("tiles/wall/right_top.png"));
        Tile wall_right_bottom = new Tile("wall_right_bottom", new Texture("tiles/wall/right_bottom.png"));

        // Corners
        Tile wall_corner_ul_top = new Tile("wall_corner_ul_top", new Texture("tiles/wall/corner/ul_top.png"));
        Tile wall_corner_ul_bot = new Tile("wall_corner_ul_bot", new Texture("tiles/wall/corner/ul_bottom.png"));

        Tile wall_corner_ur_top = new Tile("wall_corner_ur_top", new Texture("tiles/wall/corner/ur_top.png"));
        Tile wall_corner_ur_bot = new Tile("wall_corner_ur_bot", new Texture("tiles/wall/corner/ur_bottom.png"));

        Tile wall_corner_dl_top = new Tile("wall_corner_dl_top", new Texture("tiles/wall/corner/dl_top.png"));
        Tile wall_corner_dl_bot = new Tile("wall_corner_dl_bot", new Texture("tiles/wall/corner/dl_bottom.png"));

        Tile wall_corner_dr_top = new Tile("wall_corner_dr_top", new Texture("tiles/wall/corner/dr_top.png"));
        Tile wall_corner_dr_bot = new Tile("wall_corner_dr_bot", new Texture("tiles/wall/corner/dr_bottom.png"));

        // Doors
        Tile door_horizontal = new Tile("door_horizontal", new Texture("tiles/door/horizontal.png"));
        Tile door_vertical = new Tile("door_vertical", new Texture("tiles/door/vertical.png"));

        List<Tile> allWalls = List.of(
            wall_up_top, wall_up_bottom,
            wall_down_top, wall_down_bottom,
            wall_left_top, wall_left_bottom,
            wall_right_top, wall_right_bottom,
            wall_corner_ul_top, wall_corner_ul_bot,
            wall_corner_ur_top, wall_corner_ur_bot,
            wall_corner_dl_top, wall_corner_dl_bot,
            wall_corner_dr_top, wall_corner_dr_bot
        );

        List<Tile> floorish = List.of(floor, door_horizontal, door_vertical);

        // Floor neighbors
        floor.setAllowedNeighbors(floorish, floorish, floorish, floorish,
            List.of(), List.of(), List.of(), List.of());

        // Wall up (top and bottom)
        wall_up_bottom.setAllowedNeighbors(List.of(wall_up_top), floorish, allWalls, allWalls,
            List.of(), List.of(), List.of(), List.of());

        wall_up_top.setAllowedNeighbors(floorish, List.of(wall_up_bottom), allWalls, allWalls,
            List.of(), List.of(), List.of(), List.of());

        // Wall down
        wall_down_bottom.setAllowedNeighbors(List.of(wall_down_top), floorish, allWalls, allWalls,
            List.of(), List.of(), List.of(), List.of());

        wall_down_top.setAllowedNeighbors(floorish, List.of(wall_down_bottom), allWalls, allWalls,
            List.of(), List.of(), List.of(), List.of());

        // Wall left
        wall_left_bottom.setAllowedNeighbors(List.of(wall_left_top), floorish, floorish, allWalls,
            List.of(), List.of(), List.of(), List.of());

        wall_left_top.setAllowedNeighbors(floorish, List.of(wall_left_bottom), floorish, allWalls,
            List.of(), List.of(), List.of(), List.of());

        // Wall right
        wall_right_bottom.setAllowedNeighbors(List.of(wall_right_top), floorish, allWalls, floorish,
            List.of(), List.of(), List.of(), List.of());

        wall_right_top.setAllowedNeighbors(floorish, List.of(wall_right_bottom), allWalls, floorish,
            List.of(), List.of(), List.of(), List.of());

        // Corners (similar logic, vary left/right/up/down for each pair)
        wall_corner_ul_bot.setAllowedNeighbors(List.of(wall_corner_ul_top), floorish, floorish, allWalls,
            List.of(), List.of(), List.of(), List.of());

        wall_corner_ul_top.setAllowedNeighbors(floorish, List.of(wall_corner_ul_bot), floorish, allWalls,
            List.of(), List.of(), List.of(), List.of());

        wall_corner_ur_bot.setAllowedNeighbors(List.of(wall_corner_ur_top), floorish, allWalls, floorish,
            List.of(), List.of(), List.of(), List.of());

        wall_corner_ur_top.setAllowedNeighbors(floorish, List.of(wall_corner_ur_bot), allWalls, floorish,
            List.of(), List.of(), List.of(), List.of());

        wall_corner_dl_bot.setAllowedNeighbors(List.of(wall_corner_dl_top), floorish, floorish, allWalls,
            List.of(), List.of(), List.of(), List.of());

        wall_corner_dl_top.setAllowedNeighbors(floorish, List.of(wall_corner_dl_bot), floorish, allWalls,
            List.of(), List.of(), List.of(), List.of());

        wall_corner_dr_bot.setAllowedNeighbors(List.of(wall_corner_dr_top), floorish, allWalls, floorish,
            List.of(), List.of(), List.of(), List.of());

        wall_corner_dr_top.setAllowedNeighbors(floorish, List.of(wall_corner_dr_bot), allWalls, floorish,
            List.of(), List.of(), List.of(), List.of());

        // Doors
        door_horizontal.setAllowedNeighbors(floorish, floorish, List.of(wall_left_bottom, door_horizontal), List.of(wall_right_bottom, door_horizontal),
            List.of(), List.of(), List.of(), List.of());

        door_vertical.setAllowedNeighbors(List.of(wall_up_bottom, door_vertical), List.of(wall_down_top, door_vertical), floorish, floorish,
            List.of(), List.of(), List.of(), List.of());
    }
}
