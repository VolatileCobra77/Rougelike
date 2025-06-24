package ca.volatilecobra.Rougelike.World;

import ca.volatilecobra.Rougelike.Entities.AI.AStar;
import ca.volatilecobra.Rougelike.Entities.AI.Node;
import ca.volatilecobra.Rougelike.GlobalVariables;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class WorldManager {

    private int num_rooms;
    private List<Room> rooms;
    private Set<Vector2> occupied = new HashSet<>();
    private Random random = new Random();

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private List<List<Vector2>> paths = Collections.synchronizedList(new ArrayList<>());
    private List<ExitNode> allExits = Collections.synchronizedList(new ArrayList<>());

    private Vector2 bounds = new Vector2();

    public WorldManager(int num_rooms, int boundx, int boundy) {
        this.num_rooms = num_rooms;
        this.rooms = new ArrayList<>();
        bounds.x = boundx;
        bounds.y = boundy;
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
    class ExitNode {
        public Vector2 position;
        public Room room;

        public ExitNode(Vector2 position, Room room) {
            this.position = position;
            this.room = room;
        }
    }

    public void generate(Collection<Room> prototypes) {

        outerLoop:
        for (int i = 0; i < num_rooms; i++) {
            List<Room> roomsShuffled = new ArrayList<>(prototypes);
            Collections.shuffle(roomsShuffled, random);
            Vector2 location = new Vector2(random.nextInt((int)-bounds.x, (int)bounds.x), random.nextInt((int)-bounds.y, (int)bounds.y));

            Room rm = roomsShuffled.get(i%roomsShuffled.size()).copy(i, location);


            int tries = 0;
            AtomicBoolean valid = new AtomicBoolean(true);
            while (isColliding(rm) || !valid.get()){
                rm.location = new Vector2(random.nextInt((int)-bounds.x, (int)bounds.x), random.nextInt((int)-bounds.y, (int)bounds.y));
                tries++;
                if (tries >= 100){
                    Room.ROOMS.remove(rm);
                    System.err.println("Could not create all rooms, skipping remainder. Created " + i + " rooms.");
                    break outerLoop;
                }
                valid.set(true);
                rm.exits.forEach((String exit, List<Vector2> vectors) ->{
                    vectors.forEach((Vector2 vector)->{
                        if (isOccupied(new Vector2(vector).scl(2))) valid.set(false);
                    });
                });

            }
            rm.exits.forEach((String exit, List<Vector2> vectors) -> {
                vectors.forEach((Vector2 vector) -> {
                    allExits.add(new ExitNode(vector.cpy().add(rm.location), rm));
                });
            });


            rm.update_tile_pos();
            rooms.add(rm);
            System.out.println("Room placed at " + rm.location);






        }
        List<ExitNode> shuffled = new ArrayList<>(allExits);
        Collections.shuffle(shuffled);
        ExitNode n1 = new ExitNode(new Vector2(0,0), null);
        ExitNode n2 = new ExitNode(new Vector2(100,100), null);
        shuffled = new ArrayList<ExitNode>();

//        shuffled.add(n1);
//        shuffled.add(n2);
//
//        rooms = new ArrayList<>();

        while (shuffled.size() > 1) {
            ExitNode exit = shuffled.get(0);
            Collections.shuffle(shuffled);
            ExitNode otherNode = shuffled.get(shuffled.size() - 1);

            if (otherNode.position.epsilonEquals(exit.position, 10f)) continue;

            Vector2 startPos = new Vector2(exit.position).scl(Tile.tile_size);
            Vector2 endPos = new Vector2(otherNode.position).scl(Tile.tile_size);

            System.out.println("Finding path from " + startPos + " to " + endPos);

            RoomGenerationAStar rmAStar = new RoomGenerationAStar();
            rmAStar.target = endPos;

            List<Vector2> path = rmAStar.findPath(startPos, this);

            if (rmAStar.couldFindPath()) {
                System.out.println("success");
                paths.add(path);

                boolean prevWasCorner = false;

                for (int i = 0; i < path.size() - 1; i++) {
                    Vector2 current = path.get(i);
                    Vector2 next = path.get(i + 1);

                    float dx = next.x - current.x;
                    float dy = next.y - current.y;

                    // If the previous segment was a corner, skip placing this hallway piece
                    if (prevWasCorner) {
                        prevWasCorner = false; // reset flag for next iteration
                        continue;
                    }

                    boolean isCorner = false;
                    if (i < path.size() - 2) {
                        Vector2 nextNext = path.get(i + 2);
                        float dx2 = nextNext.x - next.x;
                        float dy2 = nextNext.y - next.y;

                        boolean dir1Horizontal = dy == 0 && dx != 0;
                        boolean dir1Vertical = dx == 0 && dy != 0;
                        boolean dir2Horizontal = dy2 == 0 && dx2 != 0;
                        boolean dir2Vertical = dx2 == 0 && dy2 != 0;

                        if ((dir1Horizontal && dir2Vertical) || (dir1Vertical && dir2Horizontal)) {
                            isCorner = true;
                        }
                    }
                    Room hallway;
                    Vector2 hallwayLocation;

                    if (isCorner) {
                        // Mark that previous was corner to skip next hallway placement
                        prevWasCorner = true;
                        // You can handle corner placement here later if you want
                        hallway = Room.ROOMS.get("x_junction").copy(i, new Vector2(0, 0));
                        hallwayLocation = new Vector2(current.x, current.y);
                    } else {

                        if (Math.abs(dx) > Math.abs(dy)) {
                            // Horizontal hallway piece
                            hallway = Room.ROOMS.get("hallway_horizontal").copy(i, new Vector2(0, 0));
                            hallwayLocation = new Vector2(Math.min(current.x, next.x), current.y + 1);
                        } else {
                            // Vertical hallway piece
                            hallway = Room.ROOMS.get("hallway_vertical").copy(i, new Vector2(0, 0));
                            hallwayLocation = new Vector2(current.x - 1, Math.min(current.y, next.y));
                        }

                        hallwayLocation.scl(1f / Tile.tile_size);


                        // Do your placement here if you want
                    }
                    hallway.location = hallwayLocation;
                    hallway.update_tile_pos();

                    rooms.add(hallway);
                }



                allExits.remove(exit);
                allExits.remove(otherNode);
                shuffled.remove(exit);
                shuffled.remove(otherNode);
            }
        }

        System.out.println("All pathfinding tasks complete.");

        // Shutdown executor if no longer needed
        executor.shutdown();






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

    private boolean isColliding(Room rm){
        int maxX = (int)(rm.location.x + rm.size.x);
        int maxY = (int)(rm.location.y + rm.size.y);
        int minX = (int)rm.location.x;
        int minY = (int)rm.location.y;
        List<Vector2> passedCheck = new ArrayList<>();
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                Vector2 vec = new Vector2(x,y);
                if (isOccupied(vec)) return true;
                passedCheck.add(vec);
            }
        }
        occupied.addAll(passedCheck);
        return false;
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


        for (List<Vector2> path : paths){
            Vector2 prevPoint = null;
            for (Vector2 vec :path){
                if (prevPoint == null){
                    prevPoint = vec;
                    continue;
                }
                shapeRenderer.line(prevPoint.x, prevPoint.y, vec.x, vec.y);
                prevPoint = vec;
            }
        }
         for (RoomGenerationAStar astar : RoomGenerationAStar.getInstancesSnapshot()){
            Set<Node> closedSetSnapshot = astar.getClosedSetSnapshot();
            Set<Node> openSetSnapshot = astar.getOpenSetSnapshot();
            shapeRenderer.setColor(0,1,0,1);
            for (Node node :closedSetSnapshot){
                shapeRenderer.circle(node.position.x, node.position.y, 4);
            }
            shapeRenderer.setColor(0,1,1,1);
            for (Node node :openSetSnapshot){
                shapeRenderer.circle(node.position.x, node.position.y, 4);
            }
        }
    }
    public void dispose(){
        executor.shutdownNow();
    }
}
