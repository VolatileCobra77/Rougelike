package ca.volatilecobra.Rougelike.Mods;

import ca.volatilecobra.Rougelike.World.Room;
import ca.volatilecobra.Rougelike.World.Tile;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class DefaultTextures extends Mod {



    @Override
    public String getName() {
        return "ca.volatilecobra.Rougelike.Mods.DefaultTextures";
    }
    private int c = 0;
    @Override
    public void onLoad() {
        Tile wall_top = new Tile("wall_top", new Texture("tiles/wall/top.png"), true);
        Tile wall_bottom = new Tile("wall_bottom", new Texture("tiles/wall/bottom.png"), true);
        Tile wall_corner_dl = new Tile("wall_corner_dl", new Texture("tiles/wall/corner/dl.png"), true);
        Tile wall_corner_dr = new Tile("wall_corner_dr", new Texture("tiles/wall/corner/dr.png"), true);
        Tile wall_corner_ul = new Tile("wall_corner_ul", new Texture("tiles/wall/corner/ul.png"), true);
        Tile wall_corner_ur = new Tile("wall_corner_ur", new Texture("tiles/wall/corner/ur.png"), true);
        Tile wall_vertical = new Tile("wall_vertical", new Texture("tiles/wall/vertical/wall.png"), true);
        Tile wall_vertical_t = new Tile("wall_vertical_t", new Texture("tiles/wall/vertical/t.png"), true);
        Tile wall_vertical_end = new Tile("wall_vertical_end", new Texture("tiles/wall/vertical/end.png"), true);

        Tile end_right_top = new Tile("wall_end_right_top", new Texture("tiles/wall/end_right_top.png"), true);
        Tile end_left_top = new Tile("wall_end_left_top", new Texture("tiles/wall/end_left_top.png"), true);

        Tile floor_regular = new Tile("floor_regular", new Texture("tiles/floor/regular.png"));
        Tile floor_spikes = new Tile("floor_spikes", new Texture("tiles/floor/spikes/dormant.png"));

        Tile void_tile = new Tile("void", new Texture("tiles/void.png"));
        Tile void_from_wall = new Tile("void_from_wall", new Texture("tiles/void_from_wall.png"));


        Room square = new Room("square", new Vector2(20,20), new Vector2(0,0));
        fill(square.tiles, floor_regular,square.location);
        border(square.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur,wall_corner_dl, wall_corner_dr);
        add_exit(square, new Vector2 (9, 19), 2, 0);
        square.up_exits.add(new Vector2 (9, 19));
        add_exit(square, new Vector2 (9, 0), 2, 0);
        square.down_exits.add(new Vector2 (9, 0));
        add_exit(square, new Vector2 (0, 9), 0, 2);
        square.left_exits.add(new Vector2 (0, 9));
        add_exit(square, new Vector2 (19, 9), 0, 2);
        square.right_exits.add(new Vector2 (19, 9));


        Room t_junc = new Room("t_junc", new Vector2(10,10), new Vector2(0,0));
        t_junc.tiles = new Tile[][]{
            {wall_corner_ul.copy(0), wall_top.copy(0),         wall_top.copy(1),         wall_top.copy(2),         wall_top.copy(3),          wall_top.copy(4),         wall_top.copy(5),         wall_top.copy(6),         wall_top.copy(7),         wall_corner_ur.copy(0)},
            {wall_vertical.copy(0),  wall_bottom.copy(0),      wall_bottom.copy(1),      wall_bottom.copy(2),      wall_bottom.copy(3),       wall_bottom.copy(4),      wall_bottom.copy(5),      wall_bottom.copy(6),      wall_bottom.copy(7),      wall_vertical.copy(0) },
            {wall_vertical.copy(0),  floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),     floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    wall_vertical.copy(0) },
            {floor_regular.copy(0),  floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),     floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0) },
            {floor_regular.copy(0),  floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),     floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0) },
            {wall_vertical.copy(0),  floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),     floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    floor_regular.copy(0),    wall_vertical.copy(0) },
            {wall_corner_dl.copy(0), wall_top.copy(0),         wall_top.copy(1),         wall_corner_ur.copy(0),   floor_regular.copy(0),     floor_regular.copy(0),    wall_corner_ul.copy(5),   wall_top.copy(2),         wall_top.copy(2),         wall_corner_dr.copy(0)},
            {wall_bottom.copy(0),    wall_bottom.copy(0),      wall_bottom.copy(1),      wall_vertical.copy(2),    floor_regular.copy(0),     floor_regular.copy(0),    wall_vertical.copy(3),    wall_bottom.copy(4),      wall_bottom.copy(5),      wall_bottom.copy(0)   },
            {void_from_wall.copy(0), void_from_wall.copy(0),   void_from_wall.copy(0),   wall_vertical.copy(0),    floor_regular.copy(0),     floor_regular.copy(0),    wall_vertical.copy(3),    void_from_wall.copy(0),   void_from_wall.copy(0),   void_from_wall.copy(0)},
            {void_tile.copy(0),      void_tile.copy(0),        void_tile.copy(0),        wall_vertical.copy(0),    floor_regular.copy(0),     floor_regular.copy(0),    wall_vertical.copy(3),    void_tile.copy(0),        void_tile.copy(0),        void_tile.copy(0)     }
        };

        t_junc.left_exits.add(new Vector2(0,5));
        t_junc.right_exits.add(new Vector2(10,5));
        t_junc.down_exits.add(new Vector2(5,0));

        //rotate 90deg to the right because its flipped when rendered for some reason, idk im just doing this because im too lazy to find annother way that does not require this

        Tile[][] original = t_junc.tiles;
        int width = original.length;
        int height = original[0].length;

        Tile[][] rotated = new Tile[height][width];  // swapped dimensions

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rotated[y][width - x - 1] = original[x][y];
            }
        }

        t_junc.tiles = rotated;


        for (int x = 0; x < t_junc.tiles.length; x++) {
            for (int y = 0; y < t_junc.tiles[0].length; y++) {
                Tile tile = t_junc.tiles[x][y];
                if (tile != null) {
                    tile._world_pos = new Vector2(
                        t_junc.location.x + x * Tile.tile_size,
                        t_junc.location.y + y * Tile.tile_size
                    );
                }
            }
        }


        // UP exit room
        Room up_room = new Room("up_room", new Vector2(10, 10), new Vector2(0, 0));
        fill(up_room.tiles, floor_regular, up_room.location);
        border(up_room.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr);
        add_exit(up_room, new Vector2(4, 9), 2, 0); // exit at top center
        up_room.up_exits.add(new Vector2(4, 9));

// DOWN exit room
        Room down_room = new Room("down_room", new Vector2(10, 10), new Vector2(0, 0));
        fill(down_room.tiles, floor_regular, down_room.location);
        border(down_room.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr);
        add_exit(down_room, new Vector2(4, 0), 2, 0); // exit at bottom center
        down_room.down_exits.add(new Vector2(4, 0));

// LEFT exit room
        Room left_room = new Room("left_room", new Vector2(10, 10), new Vector2(0, 0));
        fill(left_room.tiles, floor_regular, left_room.location);
        border(left_room.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr);
        add_exit(left_room, new Vector2(0, 4), 0, 2); // exit at left center
        left_room.left_exits.add(new Vector2(0, 4));

// RIGHT exit room
        Room right_room = new Room("right_room", new Vector2(10, 10), new Vector2(0, 0));
        fill(right_room.tiles, floor_regular, right_room.location);
        border(right_room.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr);
        add_exit(right_room, new Vector2(9, 4), 0, 2); // exit at right center
        right_room.right_exits.add(new Vector2(9, 4));

        // Small 6x6 room with a right exit
        Room smallRight = new Room("smallRight", new Vector2(6,6), new Vector2(0,0));
        fill(smallRight.tiles, floor_regular, smallRight.location);
        border(smallRight.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr);
        add_exit(smallRight, new Vector2(5, 2), 0, 2);
        smallRight.right_exits.add(new Vector2(5, 2));

// Medium 8x10 room with bottom exit
        Room mediumDown = new Room("mediumDown", new Vector2(8,10), new Vector2(0,0));
        fill(mediumDown.tiles, floor_regular, mediumDown.location);
        border(mediumDown.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr);
        add_exit(mediumDown, new Vector2(3, 0), 2, 0);
        mediumDown.down_exits.add(new Vector2(3, 0));

// Large 12x10 room with left exit, custom wall pattern (e.g. some missing walls)
        Room largeLeft = new Room("largeLeft", new Vector2(12,10), new Vector2(0,0));

// Manually create tiles with a pattern for walls and floors
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 10; y++) {
                if (x == 0 && y >= 3 && y <= 5) {
                    // leave space for exit on left side at y=3..5
                    largeLeft.tiles[x][y] = floor_regular.copy(0);
                } else if (x == 0 || x == 11 || y == 0 || y == 9) {
                    // border walls - just a simple wall_vertical or wall_top/bottom on edges
                    if (y == 0) largeLeft.tiles[x][y] = wall_bottom.copy(0);
                    else if (y == 9) largeLeft.tiles[x][y] = wall_top.copy(0);
                    else largeLeft.tiles[x][y] = wall_vertical.copy(0);
                } else {
                    largeLeft.tiles[x][y] = floor_regular.copy(0);
                }
            }
        }
// Add exits after the tile setup
        add_exit(largeLeft, new Vector2(0, 4), 0, 3);
        largeLeft.left_exits.add(new Vector2(0, 4));





//        List<Tile> all_tiles = new ArrayList<Tile>(Tile.TILES.values());
//
//
//        wall_top.setAllowedNeighbors(List.of(void_tile, floor_regular, floor_spikes), List.of(wall_bottom), List.of(wall_top, wall_corner_dl,wall_corner_dr, wall_corner_ur, wall_corner_ul), List.of(wall_top, wall_corner_dl,wall_corner_dr, wall_corner_ur, wall_corner_ul), all_tiles,all_tiles, all_tiles, all_tiles);
//        wall_bottom.setAllowedNeighbors(List.of(wall_top,wall_corner_dl, wall_corner_dr,wall_corner_ur, wall_corner_ul), List.of(floor_regular, floor_spikes, void_from_wall) ,List.of(wall_bottom, wall_vertical), List.of(wall_bottom, wall_vertical), all_tiles,all_tiles,all_tiles,all_tiles);
//        wall_corner_dl.setAllowedNeighbors(List.of(wall_vertical_t, wall_vertical_end, wall_vertical), List.of(wall_bottom), all_tiles, List.of(wall_top), all_tiles, all_tiles,all_tiles,all_tiles);
//        wall_corner_dr.setAllowedNeighbors(List.of(wall_vertical_t, wall_vertical_end, wall_vertical), List.of(wall_bottom), List.of(wall_top) ,all_tiles, all_tiles, all_tiles,all_tiles,all_tiles);
//        wall_corner_ul.setAllowedNeighbors( all_tiles,List.of(wall_vertical_t, wall_vertical_end, wall_vertical), all_tiles, List.of(wall_top), all_tiles, all_tiles,all_tiles,all_tiles);
//        wall_corner_ur.setAllowedNeighbors( all_tiles,List.of(wall_vertical_t, wall_vertical_end, wall_vertical), List.of(wall_top) ,all_tiles, all_tiles, all_tiles,all_tiles,all_tiles);
//
//        wall_vertical.setAllowedNeighbors(List.of(wall_corner_ur, wall_corner_ul, wall_vertical, wall_vertical_t, wall_vertical_end), List.of(wall_corner_dl, wall_corner_dr, wall_vertical), all_tiles,all_tiles,all_tiles,all_tiles,all_tiles,all_tiles);
//        wall_vertical_t.setAllowedNeighbors(List.of(void_tile, floor_regular,floor_spikes), List.of(wall_vertical, wall_corner_dl, wall_corner_dr), List.of(wall_top, wall_corner_ur), List.of(wall_top, wall_corner_ul), all_tiles, all_tiles,all_tiles,all_tiles);
//        wall_vertical_end.setAllowedNeighbors(List.of(void_tile, floor_regular,floor_spikes, wall_bottom), all_tiles,all_tiles,all_tiles,all_tiles,all_tiles, all_tiles,all_tiles);
//
//        floor_regular.setAllowedNeighbors(List.of(wall_bottom, floor_regular,floor_spikes), List.of(wall_top, floor_regular, floor_spikes, wall_vertical_t, wall_vertical_end), List.of(wall_vertical, floor_regular, floor_spikes),List.of(wall_vertical, floor_regular, floor_spikes),all_tiles,all_tiles,all_tiles,all_tiles);
//        floor_spikes.setAllowedNeighbors(List.of(wall_bottom, floor_regular,floor_spikes), List.of(wall_top, floor_regular, floor_spikes, wall_vertical_t, wall_vertical_end), List.of(wall_vertical, floor_regular, floor_spikes),List.of(wall_vertical, floor_regular, floor_spikes),all_tiles,all_tiles,all_tiles,all_tiles);
//
//        void_tile.setAllowedNeighbors(all_tiles,all_tiles,all_tiles,all_tiles,all_tiles,all_tiles,all_tiles,all_tiles);
//        void_from_wall.setAllowedNeighbors(List.of(wall_bottom),all_tiles,all_tiles,all_tiles,all_tiles,all_tiles,all_tiles,all_tiles);
    }
    private void fill(Tile[][] tiles, Tile filler, Vector2 roomPosition) {
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                tiles[x][y] = filler.copy(c);
                tiles[x][y]._world_pos = new Vector2(x * Tile.tile_size, y * Tile.tile_size);
                c++;
            }
        }
    }

    private void add_exit(Room room, Vector2 location, int width, int height) {
        Tile floor = Tile.getByName("floor_regular");
        Tile wallEndLeft = Tile.getByName("wall_end_left_top");
        Tile wallEndRight = Tile.getByName("wall_end_right_top");

        if (width > 0) {
            for (int i = 0; i < width; i++) {
                int x = (int) location.x + i;
                int y1 = (int) location.y;
                int y2 = (int) location.y + 1;

                room.tiles[x][y1] = floor.copy(i);
                room.tiles[x][y1]._world_pos = new Vector2(x * Tile.tile_size, y1 * Tile.tile_size);

                try {
                    room.tiles[x][y2] = floor.copy(i + 1000);
                    room.tiles[x][y2]._world_pos = new Vector2(x * Tile.tile_size, y2 * Tile.tile_size);
                } catch (ArrayIndexOutOfBoundsException e) {
                    room.tiles[x][y1 - 1] = floor.copy(i + 1000);
                    room.tiles[x][y1 - 1]._world_pos = new Vector2(x * Tile.tile_size, (y1 - 1) * Tile.tile_size);
                }
            }

            // Place wall_end tiles on either side of the exit (top row only)
            int leftX = (int) location.x - 1;
            int rightX = (int) location.x + width;
            int wallY = (int) location.y + 1;

            if (leftX >= 0 && wallY >= 0 && leftX < room.tiles.length && wallY < room.tiles[0].length) {
                room.tiles[leftX][wallY] = wallEndLeft.copy(9999);
                room.tiles[leftX][wallY]._world_pos = new Vector2(leftX * Tile.tile_size, wallY * Tile.tile_size);
            }

            if (rightX >= 0 && wallY >= 0 && rightX < room.tiles.length && wallY < room.tiles[0].length) {
                room.tiles[rightX][wallY] = wallEndRight.copy(9998);
                room.tiles[rightX][wallY]._world_pos = new Vector2(rightX * Tile.tile_size, wallY * Tile.tile_size);
            }
        } else if (height > 0) {
            for (int i = 0; i < height; i++) {
                int x1 = (int) location.x;
                int y = (int) location.y + i;

                room.tiles[x1][y] = floor.copy(i);
                room.tiles[x1][y]._world_pos = new Vector2(x1 * Tile.tile_size, y * Tile.tile_size);
            }
        }
    }




    private void border(Tile[][] tiles, Tile vertical, Tile top, Tile bottom,
                        Tile ul, Tile ur, Tile dl, Tile dr) {
        int w = tiles.length;
        int h = tiles[0].length;

        for (int x = 1; x < w - 1; x++) {
            tiles[x][h - 1] = top.copy(c);
            tiles[x][h - 1]._world_pos = new Vector2(x * Tile.tile_size, (h - 1) * Tile.tile_size);
            c++;

            tiles[x][h - 2] = bottom.copy(c);
            tiles[x][h - 2]._world_pos = new Vector2(x * Tile.tile_size, (h - 2) * Tile.tile_size);
            c++;

            tiles[x][0] = bottom.copy(c);
            tiles[x][0]._world_pos = new Vector2(x * Tile.tile_size, 0);
            c++;

            tiles[x][1] = top.copy(c);
            tiles[x][1]._world_pos = new Vector2(x * Tile.tile_size, Tile.tile_size);
            c++;
        }

        for (int y = 1; y < h - 1; y++) {
            tiles[0][y] = vertical.copy(c);
            tiles[0][y]._world_pos = new Vector2(0, y * Tile.tile_size);
            c++;

            tiles[w - 1][y] = vertical.copy(c);
            tiles[w - 1][y]._world_pos = new Vector2((w - 1) * Tile.tile_size, y * Tile.tile_size);
            c++;
        }

        tiles[0][h - 1] = ul.copy(c);
        tiles[0][h - 1]._world_pos = new Vector2(0, (h - 1) * Tile.tile_size);
        c++;

        tiles[w - 1][h - 1] = ur.copy(c);
        tiles[w - 1][h - 1]._world_pos = new Vector2((w - 1) * Tile.tile_size, (h - 1) * Tile.tile_size);
        c++;

        tiles[0][1] = dl.copy(c);
        tiles[0][1]._world_pos = new Vector2(0, Tile.tile_size);
        c++;

        tiles[w - 1][1] = dr.copy(c);
        tiles[w - 1][1]._world_pos = new Vector2((w - 1) * Tile.tile_size, Tile.tile_size);
        c++;

        tiles[w - 1][0] = bottom.copy(c);
        tiles[w - 1][0]._world_pos = new Vector2((w - 1) * Tile.tile_size, 0);
        c++;

        tiles[0][0] = bottom.copy(c);
        tiles[0][0]._world_pos = new Vector2(0, 0);
    }





    @Override
    public void Render_shapes(ShapeRenderer shapeRenderer) {

    }

    @Override
    public void Render_objects(SpriteBatch spriteBatch) {

    }

    @Override
    void Render_shapes_ui(ShapeRenderer shapeRenderer) {

    }

    @Override
    void Render_objects_ui(SpriteBatch spriteBatch) {

    }

    @Override
    void Render_debug_objects(SpriteBatch spriteBatch) {

    }

    @Override
    void Render_debug_shapes(ShapeRenderer shapeRenderer) {

    }

    @Override
    void Render_debug_objects_ui(SpriteBatch spriteBatch) {

    }

    @Override
    void Render_debug_shapes_ui(ShapeRenderer shapeRenderer) {

    }

    @Override
    public void Update(float dt) {

    }

}
