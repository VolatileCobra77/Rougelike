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
        //bannars
        Tile bannar_blue_top = new Tile("bannar_blue_top", new Texture("/tiles/bannar/blue_top.png"));
        Tile bannar_blue_bottom = new Tile("bannar_blue_top", new Texture("/tiles/bannar/blue_top.png"));
        Tile bannar_red_top = new Tile("bannar_red_top", new Texture("/tiles/bannar/red_top.png"));
        Tile bannar_red_bottom = new Tile("bannar_red_bottom", new Texture("/tiles/bannar/red_top.png"));

        //blank and void tiles
        Tile blank = new Tile("blank", new Texture("/tiles/blank.png"));
        Tile void_tile = new Tile("void", new Texture("/tiles/void.png"));

        //doors
        //    blank door
        Tile door_blank_top_right = new Tile("door_blank_top_right", new Texture("/tiles/door/blank/top_right.png"));
        Tile door_blank_top_mid = new Tile("door_blank_top_mid", new Texture("/tiles/door/blank/top_mid.png"));
        Tile door_blank_top_left = new Tile("door_blank_top_left", new Texture("/tiles/door/blank/top_left.png"));
        Tile door_blank_right_bottom = new Tile("door_blank_right_bottom", new Texture("/tiles/door/blank/right_bottom.png"));
        Tile door_blank_mid_bottom = new Tile("door_blank_mid_bottom", new Texture("/tiles/door/blank/mid_bottom.png"));
        Tile door_blank_left_bottom = new Tile("door_blank_left_bottom", new Texture("/tiles/door/blank/left_bottom.png"));

        //   locked_door
        Tile door_locked_top_right = new Tile("door_locked_top_right", new Texture("/tiles/door/locked/top_right.png"));
        Tile door_locked_top_mid = new Tile("door_locked_top_mid", new Texture("/tiles/door/locked/top_mid.png"));
        Tile door_locked_top_left = new Tile("door_locked_top_left", new Texture("/tiles/door/locked/top_left.png"));
        Tile door_locked_right_bottom = new Tile("door_locked_right_bottom", new Texture("/tiles/door/locked/right_bottom.png"));
        Tile door_locked_mid_bottom = new Tile("door_locked_mid_bottom", new Texture("/tiles/door/locked/mid_bottom.png"));
        Tile door_locked_left_bottom = new Tile("door_locked_left_bottom", new Texture("/tiles/door/locked/left_bottom.png"));


        //    unlocked_door
        Tile door_unlocked_top_right = new Tile("door_unlocked_top_right", new Texture("/tiles/door/unlocked/top_right.png"));
        Tile door_unlocked_top_mid = new Tile("door_unlocked_top_mid", new Texture("/tiles/door/unlocked/top_mid.png"));
        Tile door_unlocked_top_left = new Tile("door_unlocked_top_left", new Texture("/tiles/door/unlocked/top_left.png"));
        Tile door_unlocked_right_bottom = new Tile("door_unlocked_right_bottom", new Texture("/tiles/door/unlocked/right_bottom.png"));
        Tile door_unlocked_mid_bottom = new Tile("door_unlocked_mid_bottom", new Texture("/tiles/door/unlocked/mid_bottom.png"));
        Tile door_unlocked_left_bottom = new Tile("door_unlocked_left_bottom", new Texture("/tiles/door/unlocked/left_bottom.png"));

        //floor
        Tile floor_pillar_1 = new Tile("floor_pillar_1", new Texture("/tiles/floor/pillar/1.png"));
        Tile floor_pillar_2 = new Tile("floor_pillar_2", new Texture("/tiles/floor/pillar/2.png"));

        Tile floor_regular = new Tile("floor_regular", new Texture("/tiles/floor/regular.png"));

        Tile floor_spikes_0_percent = new Tile("floor_spikes_0_percent", new Texture("/tiles/floor/spikes/0%.png"));
        Tile floor_spikes_25_percent = new Tile("floor_spikes_25_percent", new Texture("/tiles/floor/spikes/25%.png"));
        Tile floor_spikes_75_percent = new Tile("floor_spikes_75_percent", new Texture("/tiles/floor/spikes/75%.png"));
        Tile floor_spikes_100_percent = new Tile("floor_spikes_100_percent", new Texture("/tiles/floor/spikes/100%.png"));
        Tile floor_spikes_dormant = new Tile("floor_spikes_dormant", new Texture("/tiles/floor/spikes/dormant.png"));

        //misc
        Tile skull = new Tile("skull", new Texture("/tiles/skull.png"));
        Tile void_from_wall = new Tile("void_from_wall", new Texture("/tiles/void_from_wall.png"));

        //stairway
        Tile stairway_ascending_top = new Tile("stairway_ascending_top", new Texture("/tiles/stairway/ascending_top.png"));
        Tile stairway_ascending_bottom = new Tile("stairway_ascending_bottom", new Texture("/tiles/stairway/ascending_bottom.png"));
        Tile stairway_decending_top = new Tile("stairway_decending_top", new Texture("/tiles/stairway/decending_top.png"));
        Tile stairway_decending_bottom = new Tile("stairway_decending_bottom", new Texture("/tiles/stairway/decending_bottom.png"));

        //wall
        Tile wall_2_bottom = new Tile("wall_2_bottom", new Texture("/tiles/wall/2_bottom.png"));
        Tile wall_2_top = new Tile("wall_2_top", new Texture("/tiles/wall/2_top.png"));
        Tile wall_3_bottom = new Tile("wall_3_bottom", new Texture("/tiles/wall/3_bottom.png"));
        Tile wall_3_top = new Tile("wall_3_top", new Texture("/tiles/wall/3_top.png"));

        Tile wall_bannar_blue_bottom = new Tile("wall_bannar_blue_bottom", new Texture("/tiles/wall/bannar_blue_bottom.png"));
        Tile wall_bannar_blue_top = new Tile("wall_bannar_blue_top", new Texture("/tiles/wall/bannar_blue_top.png"));
        Tile wall_bannar_red_1_bottom = new Tile("wall_bannar_red_1_bottom", new Texture("/tiles/wall/bannar_red_1_bottom.png"));
        Tile wall_bannar_red_1_top = new Tile("wall_bannar_red_1_top", new Texture("/tiles/wall/bannar_red_1_top.png"));
        Tile wall_bannar_red_bottom = new Tile("wall_bannar_red_bottom", new Texture("/tiles/wall/bannar_red_bottom.png"));
        Tile wall_bannar_red_top = new Tile("wall_bannar_red_top", new Texture("/tiles/wall/bannar_red_top.png"));

        Tile wall_top = new Tile("wall_top", new Texture("/tiles/wall/top.png"));
        Tile wall_bottom = new Tile("wall_bottom", new Texture("/tiles/wall/bottom.png"));

        Tile wall_candle_bottom = new Tile("wall_candle_bottom", new Texture("/tiles/wall/candle_bottom.png"));
        Tile wall_candle_mid = new Tile("wall_candle_mid", new Texture("/tiles/wall/candle_mid.png"));
        Tile wall_candle_top = new Tile("wall_candle_top", new Texture("/tiles/wall/candle_top.png"));


        //wall corners
        Tile wall_corner_dl = new Tile("wall_corner_dl", new Texture("/tiles/wall/corner/dl.png"));
        Tile wall_corner_dr = new Tile("wall_corner_dr", new Texture("/tiles/wall/corner/dr.png"));
        Tile wall_corner_ul = new Tile("wall_corner_ul", new Texture("/tiles/wall/corner/ul.png"));
        Tile wall_corner_ur = new Tile("wall_corner_ur", new Texture("/tiles/wall/corner/ur.png"));


        //wall ends
        Tile wall_horizontal_end = new Tile("wall_horizontal_end", new Texture("/tiles/wall/horizontal/end.png"));
        Tile wall_horizontal_t = new Tile("wall_horizontal_t", new Texture("/tiles/wall/horizontal/t.png"));
        Tile wall_horizontal = new Tile("wall_horizontal", new Texture("/tiles/wall/horizontal/wall.png"));


        //wall extras
        Tile wall_ledge = new Tile("wall_ledge", new Texture("/tiles/wall/ledge.png"));
        Tile wall_mid_top = new Tile("wall_mid_top", new Texture("/tiles/wall/mid_top.png"));

        //wall pillars
        Tile wall_pillar_1_mid = new Tile("wall_pillar_1_mid", new Texture("/tiles/wall/pillar/1_mid.png"));
        Tile wall_pillar_1_top = new Tile("wall_pillar_1_top", new Texture("/tiles/wall/pillar/1_top.png"));
        Tile wall_pillar_2_mid = new Tile("wall_pillar_2_mid", new Texture("/tiles/wall/pillar/2_mid.png"));
        Tile wall_pillar_2_top = new Tile("wall_pillar_2_top", new Texture("/tiles/wall/pillar/2_top.png"));
        Tile wall_pillar_3_bottom = new Tile("wall_pillar_3_bottom", new Texture("/tiles/wall/pillar/3_bottom.png"));
        Tile wall_pillar_3_mid = new Tile("wall_pillar_3_mid", new Texture("/tiles/wall/pillar/3_mid.png"));

        List<Tile> wall_tops = List.of(wall_top, wall_2_top, wall_3_top, wall_candle_top, wall_mid_top, wall_bannar_blue_top, wall_bannar_red_top, wall_bannar_red_1_top, wall_pillar_1_top, wall_pillar_2_top);
        List<Tile> wall_bottoms = List.of(wall_bottom, wall_candle_bottom, wall_2_bottom, wall_3_bottom, wall_bannar_red_bottom, wall_bannar_blue_bottom, wall_pillar_3_bottom, wall_bannar_red_1_bottom);

        List<Tile> door_bottoms = List.of(door_blank_left_bottom, door_blank_mid_bottom, door_blank_right_bottom, door_locked_left_bottom, door_locked_mid_bottom)

        List<Tile> floor_tiles = List.of(floor_regular, floor_pillar_2, floor_pillar_1, floor_spikes_dormant);
        List<Tile> any_tile = List.of(
            bannar_blue_top,
            bannar_blue_bottom,
            bannar_red_top,
            bannar_red_bottom,
            blank,
            void_tile,
            door_blank_top_right,
            door_blank_top_mid,
            door_blank_top_left,
            door_blank_right_bottom,
            door_blank_mid_bottom,
            door_blank_left_bottom,
            door_locked_top_right,
            door_locked_top_mid,
            door_locked_top_left,
            door_locked_right_bottom,
            door_locked_mid_bottom,
            door_locked_left_bottom,
            door_unlocked_top_right,
            door_unlocked_top_mid,
            door_unlocked_top_left,
            door_unlocked_right_bottom,
            door_unlocked_mid_bottom,
            door_unlocked_left_bottom,
            floor_pillar_1,
            floor_pillar_2,
            floor_regular,
            floor_spikes_0_percent,
            floor_spikes_25_percent,
            floor_spikes_75_percent,
            floor_spikes_100_percent,
            floor_spikes_dormant,
            skull,
            void_from_wall,
            stairway_ascending_top,
            stairway_ascending_bottom,
            stairway_decending_top,
            stairway_decending_bottom,
            wall_2_bottom,
            wall_2_top,
            wall_3_bottom,
            wall_3_top,
            wall_bannar_blue_bottom,
            wall_bannar_blue_top,
            wall_bannar_red_1_bottom,
            wall_bannar_red_1_top,
            wall_bannar_red_bottom,
            wall_bannar_red_top,
            wall_top,
            wall_bottom,
            wall_candle_bottom,
            wall_candle_mid,
            wall_candle_top,
            wall_corner_dl,
            wall_corner_dr,
            wall_corner_ul,
            wall_corner_ur,
            wall_horizontal_end,
            wall_horizontal_t,
            wall_horizontal,
            wall_ledge,
            wall_mid_top,
            wall_pillar_1_mid,
            wall_pillar_1_top,
            wall_pillar_2_mid,
            wall_pillar_2_top,
            wall_pillar_3_bottom,
            wall_pillar_3_mid
        );

        //random stuff i want disabled
        bannar_blue_top.setAllowedNeighbors(List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of());
        bannar_blue_bottom.setAllowedNeighbors(List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of());
        bannar_red_top.setAllowedNeighbors(List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of());
        bannar_red_bottom.setAllowedNeighbors(List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of());

        blank.setAllowedNeighbors(List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of(),List.of());


        //blank door
        door_blank_top_left.setAllowedNeighbors(List.of(void_tile),List.of(door_blank_left_bottom), wall_tops, List.of(door_blank_top_mid),any_tile,any_tile,wall_bottoms,List.of(door_blank_mid_bottom));
        door_blank_top_mid.setAllowedNeighbors(List.of(void_tile),List.of(door_blank_mid_bottom), List.of(door_blank_top_left), List.of(door_blank_top_right), List.of(void_tile), List.of(void_tile) ,List.of(door_blank_left_bottom), List.of(door_blank_right_bottom));
        door_blank_top_right.setAllowedNeighbors(List.of(void_tile), List.of(door_blank_right_bottom),List.of(door_blank_top_mid), wall_tops, any_tile, any_tile, List.of(door_blank_mid_bottom), wall_bottoms);
        door_blank_left_bottom.setAllowedNeighbors(List.of(door_blank_top_left), floor_tiles, wall_bottoms, List.of(door_blank_mid_bottom),wall_tops,List.of(door_blank_top_mid), floor_tiles,floor_tiles);
        door_blank_mid_bottom.setAllowedNeighbors(List.of(door_blank_top_mid), floor_tiles, List.of(door_blank_left_bottom), List.of(door_blank_right_bottom), List.of(door_blank_top_left), List.of(door_blank_top_right), floor_tiles, floor_tiles);
        door_blank_right_bottom.setAllowedNeighbors(List.of(door_blank_top_right), floor_tiles, List.of(door_blank_mid_bottom), wall_bottoms, List.of(door_blank_top_mid), wall_tops, floor_tiles, floor_tiles);

        // Locked doors
        door_locked_top_left.setAllowedNeighbors(List.of(void_tile), List.of(door_locked_left_bottom), wall_tops, List.of(door_locked_top_mid), any_tile, any_tile, wall_bottoms, List.of(door_locked_mid_bottom));
        door_locked_top_mid.setAllowedNeighbors(List.of(void_tile), List.of(door_locked_mid_bottom), List.of(door_locked_top_left), List.of(door_locked_top_right), List.of(void_tile), List.of(void_tile), List.of(door_locked_left_bottom), List.of(door_locked_right_bottom));
        door_locked_top_right.setAllowedNeighbors(List.of(void_tile), List.of(door_locked_right_bottom), List.of(door_locked_top_mid), wall_tops, any_tile, any_tile, List.of(door_locked_mid_bottom), wall_bottoms);
        door_locked_left_bottom.setAllowedNeighbors(List.of(door_locked_top_left), floor_tiles, wall_bottoms, List.of(door_locked_mid_bottom), wall_tops, List.of(door_locked_top_mid), floor_tiles, floor_tiles);
        door_locked_mid_bottom.setAllowedNeighbors(List.of(door_locked_top_mid), floor_tiles, List.of(door_locked_left_bottom), List.of(door_locked_right_bottom), List.of(door_locked_top_left), List.of(door_locked_top_right), floor_tiles, floor_tiles);
        door_locked_right_bottom.setAllowedNeighbors(List.of(door_locked_top_right), floor_tiles, List.of(door_locked_mid_bottom), wall_bottoms, List.of(door_locked_top_mid), wall_tops, floor_tiles, floor_tiles);

        // Unlocked doors
        door_unlocked_top_left.setAllowedNeighbors(List.of(void_tile), List.of(door_unlocked_left_bottom), wall_tops, List.of(door_unlocked_top_mid), any_tile, any_tile, wall_bottoms, List.of(door_unlocked_mid_bottom));
        door_unlocked_top_mid.setAllowedNeighbors(List.of(void_tile), List.of(door_unlocked_mid_bottom), List.of(door_unlocked_top_left), List.of(door_unlocked_top_right), List.of(void_tile), List.of(void_tile), List.of(door_unlocked_left_bottom), List.of(door_unlocked_right_bottom));
        door_unlocked_top_right.setAllowedNeighbors(List.of(void_tile), List.of(door_unlocked_right_bottom), List.of(door_unlocked_top_mid), wall_tops, any_tile, any_tile, List.of(door_unlocked_mid_bottom), wall_bottoms);
        door_unlocked_left_bottom.setAllowedNeighbors(List.of(door_unlocked_top_left), floor_tiles, wall_bottoms, List.of(door_unlocked_mid_bottom), wall_tops, List.of(door_unlocked_top_mid), floor_tiles, floor_tiles);
        door_unlocked_mid_bottom.setAllowedNeighbors(List.of(door_unlocked_top_mid), floor_tiles, List.of(door_unlocked_left_bottom), List.of(door_unlocked_right_bottom), List.of(door_unlocked_top_left), List.of(door_unlocked_top_right), floor_tiles, floor_tiles);
        door_unlocked_right_bottom.setAllowedNeighbors(List.of(door_unlocked_top_right), floor_tiles, List.of(door_unlocked_mid_bottom), wall_bottoms, List.of(door_unlocked_top_mid), wall_tops, floor_tiles, floor_tiles);

        //floors
        List<Tile> floor_tiles_and_wall_tops = new ArrayList<>(floor_tiles);
        floor_tiles_and_wall_tops.addAll(wall_tops);

        List<Tile> floor_tiles_and_horizontal_wall = new ArrayList(floor_tiles);
        floor_tiles_and_horizontal_wall.add(wall_horizontal);

        floor_pillar_1.setAllowedNeighbors(List.of(wall_pillar_1_mid), floor_tiles_and_wall_tops, floor_tiles_and_horizontal_wall,floor_tiles_and_horizontal_wall, wall_bottoms, wall_bottoms, any_tile, any_tile );
        floor_pillar_2.setAllowedNeighbors(List.of(wall_pillar_2_mid), floor_tiles_and_wall_tops, floor_tiles_and_horizontal_wall,floor_tiles_and_horizontal_wall, wall_bottoms, wall_bottoms, any_tile, any_tile );
        floor_regular.setAllowedNeighbors(wall_bottoms, wall_tops,)





    }
}
