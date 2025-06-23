package ca.volatilecobra.Rougelike.World;

import ca.volatilecobra.Rougelike.Entities.AI.Node;
import ca.volatilecobra.Rougelike.World.Tile;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class RoomGenerationAStar {
    public Vector2 target = new Vector2(0, 0);
    private PriorityQueue<Node> openSet;
    private HashSet<Node> closedSet;
    private Map<Vector2, Float> gScores;

    public List<Vector2> lastPath = null;
    public boolean isDone = true;
    public boolean couldFindPath = true;
    boolean couldFindPathSnapshot = true;

    private final int maxDist = 1000;         // max search radius in pixels
    private final int maxIterations = 5000;  // safety cap to avoid infinite search
    private int iterations;

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

    // Directions (8-pixel steps), define once to avoid repeated allocations
    private static final Vector2[] directions = new Vector2[]{
        new Vector2(8, 0),   // right
        new Vector2(-8, 0),  // left
        new Vector2(0, 8),   // up
        new Vector2(0, -8) ,  // down
////          Uncomment diagonals if needed:
//        new Vector2(8, 8),   // up-right
//        new Vector2(-8, 8),  // up-left
//        new Vector2(8, -8),  // down-right
//        new Vector2(-8, -8)  // down-left
    };

    public RoomGenerationAStar() {
        openSet = new PriorityQueue<>();
        closedSet = new HashSet<>();
        gScores = new HashMap<>();
        INSTANCES.add(this);
    }

    public List<Vector2> findPath(Vector2 start, WorldManager worldManager) {
        isDone = false;
        iterations = 0;

        openSet.clear();
        closedSet.clear();
        gScores.clear();

        Node startNode = new Node(start, null, target);
        openSet.add(startNode);
        gScores.put(start, 0f);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            iterations++;

            if (iterations >= maxIterations) {
                System.out.println("ABORTING: max iterations reached (" + maxIterations + ")");
                couldFindPath = false;
                return reconstructPath(currentNode);
            }

            updateNodeSnapshots();

            closedSet.add(currentNode);

            // Goal check with some epsilon for position fuzziness
            if (currentNode.position.epsilonEquals(target, 10f)) {
                isDone = true;
                couldFindPath = true;
                return reconstructPath(currentNode);
            }

            for (Vector2 dir : directions) {
                Vector2 neighborPos = currentNode.position.cpy().add(dir);

                // Check search radius limit (use squared distance for performance)
                if (neighborPos.dst2(start) > maxDist * maxDist) {
                    continue; // Skip nodes too far away
                }

                // Convert to tile coordinates for collision check
                Vector2 tilePos = new Vector2(neighborPos.x / Tile.tile_size, neighborPos.y / Tile.tile_size);
                Tile tile = worldManager.getTileAt(tilePos);
                if (tile != null && tile.collides) {
                    continue; // Can't walk here
                }

                Node neighborNode = new Node(neighborPos, currentNode, target);

                // Skip if in closed set
                if (closedSet.contains(neighborNode)) {
                    continue;
                }

                float tentativeG = currentNode.gCost + getDistance(currentNode.position, neighborPos);

                Float bestG = gScores.get(neighborPos);
                if (bestG == null || tentativeG < bestG) {
                    gScores.put(neighborPos, tentativeG);
                    openSet.add(neighborNode);
                }
            }
        }

        isDone = true;
        couldFindPath = false;
        // No path found
        System.out.println("Couldn't find path!");
        return Collections.emptyList();
    }

    private List<Vector2> reconstructPath(Node node) {
        List<Vector2> path = new ArrayList<>();
        Node current = node;
        while (current != null) {
            path.add(current.position);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private float getDistance(Vector2 a, Vector2 b) {
        // Manhattan distance scaled by tile size (assuming tile_size = 8)
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y)) / 8f;
    }
    public void destroy(){
        INSTANCES.remove(this);
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
}
