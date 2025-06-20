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
        Tile wall_top = new Tile("wall_top", new Texture("tiles/wall/top.png"));
        Tile wall_bottom = new Tile("wall_bottom", new Texture("tiles/wall/bottom.png"));
        Tile wall_corner_dl = new Tile("wall_corner_dl", new Texture("tiles/wall/corner/dl.png"));
        Tile wall_corner_dr = new Tile("wall_corner_dr", new Texture("tiles/wall/corner/dr.png"));
        Tile wall_corner_ul = new Tile("wall_corner_ul", new Texture("tiles/wall/corner/ul.png"));
        Tile wall_corner_ur = new Tile("wall_corner_ur", new Texture("tiles/wall/corner/ur.png"));
        Tile wall_vertical = new Tile("wall_vertical", new Texture("tiles/wall/vertical/wall.png"));
        Tile wall_vertical_t = new Tile("wall_vertical_t", new Texture("tiles/wall/vertical/t.png"));
        Tile wall_vertical_end = new Tile("wall_vertical_end", new Texture("tiles/wall/vertical/end.png"));

        Tile floor_regular = new Tile("floor_regular", new Texture("tiles/floor/regular.png"));
        Tile floor_spikes = new Tile("floor_spikes", new Texture("tiles/floor/spikes/dormant.png"));

        Tile void_tile = new Tile("void", new Texture("tiles/void.png"));
        Tile void_from_wall = new Tile("void_from_wall", new Texture("tiles/void_from_wall.png"));

        List<Room> rooms = new ArrayList<>();

        Vector2 size = new Vector2(5, 5);

        // Room 1: Simple square room
        Room squareRoom = new Room("square", size, new Vector2(0, 0));
        fill(squareRoom.tiles, floor_regular, squareRoom.location);
        border(squareRoom.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr, squareRoom.location);

        squareRoom.setExits(
            List.of(new Vector2(2, 4)),  // up
            List.of(new Vector2(2, 0)),  // down
            List.of(new Vector2(0, 2)),  // left
            List.of(new Vector2(4, 2))   // right
        );

        rooms.add(squareRoom);

        // Room 2: Spike pit
        Room spikeRoom = new Room("spike", size, new Vector2(0, 0));
        fill(spikeRoom.tiles, floor_spikes, spikeRoom.location);
        border(spikeRoom.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr, spikeRoom.location);
        spikeRoom.setExits(
            List.of(new Vector2(2, 4)),
            List.of(new Vector2(2, 0)),
            List.of(new Vector2(0, 2)),
            List.of(new Vector2(4, 2))
        );

        rooms.add(spikeRoom);

        // Room 3: Dead-end room (only connects down)


        // Room 4: Crossroad room (T-shaped)
        Room tRoom = new Room("t_room", size, new Vector2(0, 0));
        fill(tRoom.tiles, floor_regular, tRoom.location);
        border(tRoom.tiles, wall_vertical, wall_top, wall_bottom, wall_corner_ul, wall_corner_ur, wall_corner_dl, wall_corner_dr, tRoom.location);
        tRoom.setExits(
            List.of(new Vector2(2, 4)),
            List.of(new Vector2(2, 0)),
            List.of(new Vector2(0, 2)),
            List.of(new Vector2(4, 2))
        );

        rooms.add(tRoom);

        // Link allowed neighbors manually
        squareRoom.setAllowedNeighbours(List.of(tRoom, squareRoom, spikeRoom), List.of(spikeRoom, tRoom,squareRoom), List.of(tRoom,spikeRoom,squareRoom), List.of(tRoom,spikeRoom,squareRoom));
        spikeRoom.setAllowedNeighbours(List.of(tRoom, squareRoom, spikeRoom), List.of(spikeRoom, tRoom,squareRoom), List.of(tRoom,spikeRoom,squareRoom), List.of(tRoom,spikeRoom,squareRoom));
        tRoom.setAllowedNeighbours(List.of(tRoom, squareRoom, spikeRoom), List.of(spikeRoom, tRoom,squareRoom), List.of(tRoom,spikeRoom,squareRoom), List.of(tRoom,spikeRoom,squareRoom));


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
                tiles[x][y] = new Tile(filler.name + c, filler._tex);
                // Calculate world position based on room position + tile grid coords
                tiles[x][y]._world_pos = new Vector2(x  * Tile.tile_size , y  * Tile.tile_size);
                c++;
            }
        }
    }


    private void border(Tile[][] tiles, Tile vertical, Tile top, Tile bottom,
                               Tile ul, Tile ur, Tile dl, Tile dr, Vector2 roomPosition) {
        int w = tiles.length;
        int h = tiles[0].length;

        for (int x = 1; x < w - 1; x++) {
            tiles[x][h - 1] = new Tile(top.name + c, top._tex);
            tiles[x][h - 1]._world_pos = new Vector2(
                 + x * Tile.tile_size,
                 + (h - 1) * Tile.tile_size);
            c++;

            tiles[x][0] = new Tile(bottom.name + c, bottom._tex);
            tiles[x][0]._world_pos = new Vector2(
                x * Tile.tile_size,
                0);
            c++;
        }

        for (int y = 1; y < h - 1; y++) {
            tiles[0][y] = new Tile(vertical.name + c, vertical._tex);
            tiles[0][y]._world_pos = new Vector2(
                 0,
                 y * Tile.tile_size);
            c++;

            tiles[w - 1][y] = new Tile(vertical.name + c, vertical._tex);
            tiles[w - 1][y]._world_pos = new Vector2(
                 + (w - 1) * Tile.tile_size,
                 + y * Tile.tile_size);
            c++;
        }

        tiles[0][h - 1] = new Tile(ul.name + c, ul._tex);
        tiles[0][h - 1]._world_pos = new Vector2(
              0,
             (h - 1) * Tile.tile_size);
        c++;

        tiles[w - 1][h - 1] = new Tile(ur.name + c, ur._tex);
        tiles[w - 1][h - 1]._world_pos = new Vector2(
             + (w - 1) * Tile.tile_size,
             + (h - 1) * Tile.tile_size);
        c++;

        tiles[0][0] = new Tile(dl.name + c, dl._tex);
        tiles[0][0]._world_pos = new Vector2(
              0,
              0);
        c++;

        tiles[w - 1][0] = new Tile(dr.name + c, dr._tex);
        tiles[w - 1][0]._world_pos = new Vector2(
            (w - 1) * Tile.tile_size,
            0);
    }




    @Override
    public void Render_shapes(ShapeRenderer shapeRenderer) {

    }

    @Override
    public void Render_objects(SpriteBatch spriteBatch) {

    }

    @Override
    public void Update(float dt) {

    }

}
