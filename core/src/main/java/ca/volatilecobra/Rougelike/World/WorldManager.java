package ca.volatilecobra.Rougelike.World;

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
        generate(Room.ROOMS.values());
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

    public void draw_debug(ShapeRenderer shapeRenderer){

    }

    public void generate(Collection<Room> prototypes) {
        ArrayList<Room> roomArrayList = new ArrayList<Room>(prototypes);

        Room selected = roomArrayList.get(random.nextInt(roomArrayList.size()));
        selected = selected.copy(0, new Vector2(0,0));
        selected.update_tile_pos();
        for (int j = 0; j < selected.size.x; j++) {
            for (int k = 0; k < selected.size.y; k++) {
                occupied.add(new Vector2((selected.location.x + j),(selected.location.y + k)));
            }
        }
        rooms.add(selected);
        Room prev = selected;
        int lastSuccessful =0;
        String prev_dir = "";
        HashSet<Room> attemptedRooms = new HashSet<>();
        for (int i = 0; i < num_rooms-1; i++) {
            System.out.println("Creating room " + i);

            Room room_selected = roomArrayList.get(random.nextInt(roomArrayList.size()));
            if(attemptedRooms.containsAll(roomArrayList)){
                System.err.println("could not fully generate rooms, trying last room");
                try{
                    prev = rooms.get(lastSuccessful);
                    lastSuccessful -=1;
                    System.out.println(prev.name);
                    attemptedRooms = new HashSet<>();
                }catch (Exception e){
                    System.err.println("Couldnt generate any rooms on any prevous rooms, dungeon full.");
                    return;
                }

            }


            while (attemptedRooms.contains(room_selected)){
                room_selected = roomArrayList.get(random.nextInt(roomArrayList.size()));
            }
            List<String> candidates = new ArrayList<String>();

            if (!prev.left_exits.isEmpty() && !room_selected.right_exits.isEmpty() && !prev_dir.equals("left")) {
                candidates.add("right");
            }
            if (!prev.right_exits.isEmpty() && !room_selected.left_exits.isEmpty() && !prev_dir.equals("right")) {
                candidates.add("left");
            }
            if (!prev.up_exits.isEmpty() && !room_selected.down_exits.isEmpty() && !prev_dir.equals("up")){
                candidates.add("down");
            }
            if (!prev.down_exits.isEmpty() && !room_selected.up_exits.isEmpty() && !prev_dir.equals("down")){
                candidates.add("up");
            }
            if (candidates.isEmpty()){
                System.out.println("No candidates for combination " + prev.name + " and " + room_selected.name);
                attemptedRooms.add(room_selected);
                i= Math.max(lastSuccessful, i-1);
                continue;
            }
            System.out.println(candidates);
            String selection = candidates.get(random.nextInt(candidates.size()));
            prev_dir = selection;
            System.out.println(selection);
            Room next = room_selected.copy(0, new Vector2(0,0));
            switch (selection) {
                case "right": {
                    Vector2 prevExit = prev.left_exits.get(random.nextInt(prev.left_exits.size()));
                    Vector2 nextExit = next.right_exits.get(random.nextInt(next.right_exits.size()));

                    float newX = prev.location.x + prevExit.x - nextExit.x;
                    float newY = prev.location.y + prevExit.y - nextExit.y;
                    next.location = new Vector2(newX, newY);
                    break;
                }

                case "left": {
                    Vector2 prevExit = prev.right_exits.get(random.nextInt(prev.right_exits.size()));
                    Vector2 nextExit = next.left_exits.get(random.nextInt(next.left_exits.size()));

                    float newX = prev.location.x + prevExit.x - nextExit.x;
                    float newY = prev.location.y + prevExit.y - nextExit.y;
                    next.location = new Vector2(newX, newY);
                    break;
                }

                case "up": {
                    Vector2 prevExit = prev.down_exits.get(random.nextInt(prev.down_exits.size()));
                    Vector2 nextExit = next.up_exits.get(random.nextInt(next.up_exits.size()));

                    float newX = prev.location.x + prevExit.x - nextExit.x;
                    float newY = prev.location.y + prevExit.y - nextExit.y + 1;
                    next.location = new Vector2(newX, newY);
                    break;
                }

                case "down": {
                    Vector2 prevExit = prev.up_exits.get(random.nextInt(prev.up_exits.size()));
                    Vector2 nextExit = next.down_exits.get(random.nextInt(next.down_exits.size()));

                    float newX = prev.location.x + prevExit.x - nextExit.x;
                    float newY = prev.location.y + prevExit.y - nextExit.y - 1;
                    next.location = new Vector2(newX, newY);
                    break;
                }

            }
            //check if any tile of the room is occupied
            List<Vector2> checkedTiles = new ArrayList<>();
            for (int j = 0; j < next.size.x; j++) {
                for (int k = 0; k < next.size.y; k++) {
                    if (occupied.contains(new Vector2((next.location.x + j),(next.location.y + k)))){
                      Room.ROOMS.remove(next.name);
                      attemptedRooms.add(room_selected);
                      i = Math.max(lastSuccessful, i-1);
                      continue;
                    };
                    checkedTiles.add(new Vector2((next.location.x + j),(next.location.y + k)));
                }
            }

            occupied.addAll(checkedTiles);

            //clear attempted rooms to reset it.
            attemptedRooms = new HashSet<>();
            occupied.add(next.location);
            next.update_tile_pos();
            rooms.add(next);
            prev = next;
            lastSuccessful = i;
            System.out.println("Placed room " + next.name + " at " + next.location);
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
        for (Room room : rooms) {
            room.render(batch);
        }
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
