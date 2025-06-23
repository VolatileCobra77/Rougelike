package ca.volatilecobra.Rougelike.Entities.AI;

import ca.volatilecobra.Rougelike.World.Tile;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class AStar {
    public Vector2 target = new Vector2(0,0);
    PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
    HashSet<Node> closedSet = new HashSet<>();
    public List<Vector2> lastPath = null;
    public boolean isDone = true;
    int maxDist = 1000;

    int maxIterations = 10000;
    int iterations = 0;

    public AStar(){

    }
    public List<Vector2> findPath(Vector2 start, WorldManager worldManager){
        Node startNode = new Node(start, null, target);
        openSet.add(startNode);
        while (!openSet.isEmpty()) {
            Node nextNode = openSet.poll();
            iterations ++;
            if (iterations  >= maxIterations){
                List<Vector2> outputList = new ArrayList<>();
                Node current = nextNode;
                while (current != null) {
                    outputList.add(current.position);
                    current = current.parent;
                }
                Collections.reverse(outputList);
                System.out.println("ABORTING itteration max reached.");
                return outputList;
            }


            closedSet.add(nextNode);
            if (nextNode.position.epsilonEquals(target, 10f)) {
                List<Vector2> outputList = new ArrayList<>();
                Node current = nextNode;
                while (current != null) {
                    outputList.add(current.position);
                    current = current.parent;
                }
                Collections.reverse(outputList);
                return outputList;

            }


            int[][] directions = new int[][] {
                { 8,  0}, // right
                {-8,  0}, // left
                {  0, 8}, // up
                {  0,-8}, // down
//                { 8, 8}, // up-right
//                {-8, 8}, // up-left
//                { 8,-8}, // down-right
//                {-8,-8}  // down-left
            };

            for (int[] dir : directions) {
                int x = (int) nextNode.position.x + dir[0];
                int y = (int) nextNode.position.y + dir[1];
                Vector2 newPos = new Vector2(x, y);
                Node newNode = new Node(newPos, nextNode, target);

                Tile nodeTile = worldManager.getTileAt(new Vector2(newPos.x / Tile.tile_size, newPos.y/Tile.tile_size));
                if (nodeTile == null || !nodeTile.collides) {
                    // Check if this node was already visited
                    if (!closedSet.contains(newNode) && newNode.position.dst(start) <= maxDist) {
                        openSet.add(newNode);
                    }
                }
            }

        }
        isDone = true;
        return new ArrayList<Vector2>();




    }




}
