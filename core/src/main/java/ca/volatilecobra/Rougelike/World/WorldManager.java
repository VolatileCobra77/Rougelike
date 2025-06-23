package ca.volatilecobra.Rougelike.World;

import ca.volatilecobra.Rougelike.GlobalVariables;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class WorldManager {

    private int num_rooms;
    private List<Room> rooms;
    private Set<Vector2> occupied = new HashSet<>();
    private Random random = new Random();

    public WorldManager(int num_rooms) {
        this.num_rooms = num_rooms;
        this.rooms = new ArrayList<>();
        generate(Room.BASE_ROOMS.values());
    }

    public Tile getTileAt(Vector2 pos) {
        for (Room room : rooms) {
            Vector2 roomPos = room.location; // top-left of the room in tile coords
            Vector2 roomSize = room.size;

            float minX = roomPos.x;
            float minY = roomPos.y;
            float maxX = roomPos.x + roomSize.x;
            float maxY = roomPos.y + roomSize.y;

            // Check if pos is inside this room's bounds
            if (pos.x >= minX && pos.x < maxX && pos.y >= minY && pos.y < maxY) {
                int localX = (int)(pos.x - roomPos.x);
                int localY = (int)(pos.y - roomPos.y);
                return room.tiles[localX][localY];
            }
        }
        return null; // Not inside any room
    }

    public Room getRoomAt(Vector2 pos){
        for (Room room : rooms) {
            Vector2 roomPos = room.location; // top-left of the room in tile coords
            Vector2 roomSize = room.size;

            float minX = roomPos.x;
            float minY = roomPos.y;
            float maxX = roomPos.x + roomSize.x;
            float maxY = roomPos.y + roomSize.y;

            // Check if pos is inside this room's bounds
            if (pos.x >= minX && pos.x < maxX && pos.y >= minY && pos.y < maxY) {
                return room;
            }
        }
        return null; // Not inside any room
    }

    public void draw_debug(ShapeRenderer shapeRenderer){

    }

    public void generate(Collection<Room> prototypes) {
        outerLoop:
        for (int i = 0; i < num_rooms; i++) {
            List<Room> roomsShuffled = new ArrayList<>(prototypes);
            Collections.shuffle(roomsShuffled, random);
            Vector2 location = new Vector2(random.nextInt(-100, 100), random.nextInt(-100,100));

            Room rm = roomsShuffled.get(i%roomsShuffled.size()).copy(i, location);
            rm.update_tile_pos();

            int tries = 0;
            while (isColliding(rm)){
                rm.location = new Vector2(random.nextInt(-100, 100), random.nextInt(-100,100));
                tries++;
                if (tries >= 100){
                    Room.ROOMS.remove(rm);
                    break outerLoop;
                }
            }

            rooms.add(rm);
            System.out.println("Room placed at " + rm.location);
        }
    }
    private boolean isOccupied(Vector2 location) {
        for (Vector2 pos : occupied) {
            if (pos.epsilonEquals(location, 0.01f)) return true;
        }
        return false;
    }

    private List<Room> getAllowedRooms(Room base, Direction dir) {
        return switch (dir) {
            case UP -> base.allowed_up;
            case DOWN -> base.allowed_down;
            case LEFT -> base.allowed_left;
            case RIGHT -> base.allowed_right;
        };
    }

    private Vector2 getExitOffset(Room from, Room to, Direction dir) {
        Vector2 offset = new Vector2();

        switch (dir) {
            case UP -> {
                if (from.up_exits.isEmpty() || to.down_exits.isEmpty()) return null;
                Vector2 fromExit = from.up_exits.get(random.nextInt(from.up_exits.size()));
                Vector2 toExit = to.down_exits.get(random.nextInt(to.down_exits.size()));
                offset.y = from.size.y;
                offset.x = fromExit.x - toExit.x;
            }
            case DOWN -> {
                if (from.down_exits.isEmpty() || to.up_exits.isEmpty()) return null;
                Vector2 fromExit = from.down_exits.get(random.nextInt(from.down_exits.size()));
                Vector2 toExit = to.up_exits.get(random.nextInt(to.up_exits.size()));
                offset.y = -to.size.y;
                offset.x = fromExit.x - toExit.x;
            }
            case LEFT -> {
                if (from.left_exits.isEmpty() || to.right_exits.isEmpty()) return null;
                Vector2 fromExit = from.left_exits.get(random.nextInt(from.left_exits.size()));
                Vector2 toExit = to.right_exits.get(random.nextInt(to.right_exits.size()));
                offset.x = -to.size.x;
                offset.y = fromExit.y - toExit.y;
            }
            case RIGHT -> {
                if (from.right_exits.isEmpty() || to.left_exits.isEmpty()) return null;
                Vector2 fromExit = from.right_exits.get(random.nextInt(from.right_exits.size()));
                Vector2 toExit = to.left_exits.get(random.nextInt(to.left_exits.size()));
                offset.x = from.size.x;
                offset.y = fromExit.y - toExit.y;
            }
        }

        return offset;
    }

    public void render(SpriteBatch batch) {

        float camLeft = GlobalVariables.CAMERA.position.x - GlobalVariables.CAMERA.viewportWidth * 0.5f * GlobalVariables.CAMERA.zoom;
        float camRight = GlobalVariables.CAMERA.position.x + GlobalVariables.CAMERA.viewportWidth * 0.5f * GlobalVariables.CAMERA.zoom;
        float camBottom = GlobalVariables.CAMERA.position.y - GlobalVariables.CAMERA.viewportHeight * 0.5f * GlobalVariables.CAMERA.zoom;
        float camTop = GlobalVariables.CAMERA.position.y + GlobalVariables.CAMERA.viewportHeight * 0.5f * GlobalVariables.CAMERA.zoom;


        for (Room room : rooms) {
            for (int x = 0; x < room.tiles.length; x++) {
                for (int y = 0; y < room.tiles[x].length; y++) {
                    Tile tile = room.tiles[x][y];
                    if (tile == null) continue;

                    Vector2 pos = tile._world_pos;
                    float tileSize = Tile.tile_size;

                    // Cull: skip tile if completely outside camera bounds
                    if (pos.x + tileSize < camLeft || pos.x > camRight ||
                        pos.y + tileSize < camBottom || pos.y > camTop) {
                        continue;
                    }

                    tile.render(batch);
                }
            }
        }

    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public void render_debug(ShapeRenderer shapeRenderer){

        for(Room room :rooms){
            for (Tile[] tileSetX : room.tiles){
                for (Tile tile :tileSetX){
                    if (tile.collides){
                        shapeRenderer.setColor(1f,0f,0f,0.5f);
                    }else{
                        shapeRenderer.setColor(0.5f,0.5f,0f,0.5f);
                    }
                    shapeRenderer.rect(tile._world_pos.x, tile._world_pos.y, Tile.tile_size, Tile.tile_size);
                }
            }
        }
    }
}
