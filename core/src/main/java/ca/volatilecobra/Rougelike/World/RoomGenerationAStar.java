package ca.volatilecobra.Rougelike.World;

import ca.volatilecobra.Rougelike.Entities.AI.AStar;
import ca.volatilecobra.Rougelike.Entities.AI.Node;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class RoomGenerationAStar {
    public Vector2 target = new Vector2(0, 0);

    private PriorityQueue<Node> openSet;
    private HashSet<Node> closedSet;
    private Map<Vector2, Float> gScores;

    private final int maxIterations = 10000;
    boolean couldFindPathSnapshot = true;

    public static final Object pathfindingLock = new Object();

    public static final List<RoomGenerationAStar> INSTANCES = Collections.synchronizedList(new ArrayList<>());

    //  This is the method you were missing
    public static List<RoomGenerationAStar> getInstancesSnapshot() {
        synchronized (INSTANCES) {
            return new ArrayList<>(INSTANCES);
        }
    }

    public synchronized boolean couldFindPath(){
        return couldFindPathSnapshot;
    }

    // Make thread-safe copies
    public synchronized void updateNodeSnapshots() {
        openSetSnapshot = new HashSet<>(openSet);
        closedSetSnapshot = new HashSet<>(closedSet);
        lastPathSnapshot = lastPath == null ? null : new ArrayList<>(lastPath);
        couldFindPathSnapshot = couldFindPath;
    }
    private Set<Node> openSetSnapshot = Collections.emptySet();
    private Set<Node> closedSetSnapshot = Collections.emptySet();
    private List<Vector2> lastPathSnapshot = null;

    public synchronized Set<Node> getOpenSetSnapshot() {
        return openSetSnapshot;
    }

    public synchronized Set<Node> getClosedSetSnapshot() {
        return closedSetSnapshot;
    }

    public synchronized List<Vector2> getLastPathSnapshot() {
        return lastPathSnapshot;
    }

    public List<Vector2> lastPath = null;
    public boolean isDone = true;
    public boolean couldFindPath = true;

    private static final Vector2[] directions = new Vector2[]{
        new Vector2(Tile.tile_size, 0),
        new Vector2(-Tile.tile_size, 0),
        new Vector2(0, Tile.tile_size),
        new Vector2(0, -Tile.tile_size)
    };

    public RoomGenerationAStar() {
        openSet = new PriorityQueue<>();
        closedSet = new HashSet<>();
        gScores = new HashMap<>();
    }


    public List<Vector2> findPath(Vector2 start, WorldManager worldManager) {
        synchronized (pathfindingLock) {

            isDone = false;
            openSet.clear();
            closedSet.clear();
            gScores.clear();
            updateNodeSnapshots();

            Node startNode = new Node(start, null, target);
            openSet.add(startNode);
            gScores.put(start, 0f);

            int iterations = 0;

            while (!openSet.isEmpty()) {
                Node currentNode = openSet.poll();
                iterations++;

                if (iterations >= maxIterations) {
                    System.out.println("ABORTING: max iterations reached");
                    isDone = true;
                    couldFindPath = false;
                    return reconstructPath(currentNode);
                }

                closedSet.add(currentNode);

                if (currentNode.position.epsilonEquals(target, 18f)) {
                    isDone = true;
                    couldFindPath = true;
                    lastPath = reconstructPath(currentNode);
                    return lastPath;
                }

                for (Vector2 dir : directions) {
                    Vector2 neighborPos = currentNode.position.cpy().add(dir);

                    Vector2 tilePos = new Vector2(neighborPos.x / Tile.tile_size, neighborPos.y / Tile.tile_size);
                    Tile tile = worldManager.getTileAt(tilePos);
                    if (tile != null)
                        continue;

                    Node neighborNode = new Node(neighborPos, currentNode, target);
                    if (closedSet.contains(neighborNode))
                        continue;

                    float tentativeG = currentNode.gCost + getDistance(currentNode.position, neighborPos);

                    // Add penalty if neighbor tile is adjacent to a wall
                    if (isNearWall(tilePos, worldManager)) {
                        tentativeG += 5.0f;
                    }

                    Float existingG = gScores.get(neighborPos);

                    if (existingG == null || tentativeG < existingG) {
                        gScores.put(neighborPos, tentativeG);
                        openSet.add(neighborNode);
                    }
                }


            }
        }

        isDone = true;
        couldFindPath = false;
        System.out.println("Couldn't find path!");
        lastPath = Collections.emptyList();
        return lastPath;
    }
    private boolean isNearWall(Vector2 tilePos, WorldManager worldManager) {
        Vector2[] adjacentTiles = new Vector2[] {
            new Vector2(tilePos.x + 1, tilePos.y),
            new Vector2(tilePos.x - 1, tilePos.y),
            new Vector2(tilePos.x, tilePos.y + 1),
            new Vector2(tilePos.x, tilePos.y - 1)
        };

        for (Vector2 adj : adjacentTiles) {
            Tile tile = worldManager.getTileAt(adj);
            if (tile != null && tile.collides) {
                return true;
            }
        }
        return false;
    }


    private List<Vector2> reconstructPath(Node node) {
        List<Vector2> path = new ArrayList<>();
        while (node != null) {
            path.add(node.position);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private float getDistance(Vector2 a, Vector2 b) {
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y)) / Tile.tile_size;
    }
}
