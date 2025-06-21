package ca.volatilecobra.Rougelike.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        generate(Room.ROOMS.values());
    }

    public void generate(Collection<Room> prototypes) {
        ArrayList<Room> roomArrayList = new ArrayList<Room>(prototypes);

        Room selected = roomArrayList.get(random.nextInt(roomArrayList.size()));
        selected = selected.copy(0, new Vector2(0,0));
        selected.update_tile_pos();
        rooms.add(selected);
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
        for (Room room : rooms) {
            room.render(batch);
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
