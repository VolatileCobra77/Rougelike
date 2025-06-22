package ca.volatilecobra.Rougelike.Entities.AI;

import ca.volatilecobra.Rougelike.Entities.Entity;
import com.badlogic.gdx.math.Vector2;

public class Node implements Comparable<Node> {
    public Vector2 position;
    public Node parent;
    public float hCost, gCost, fCost;
    public boolean walkable = true;

    public Node(Vector2 position, Node parent, Vector2 goal) {
        this.position = position;
        this.parent = parent;

        // Cost from start to this node
        if (parent != null)
            gCost = parent.gCost + getDistance(parent.position, this.position);
        else
            gCost = 0;

        // Heuristic from this node to goal
        hCost = getDistance(this.position, goal);

        fCost = gCost + hCost;
    }

    @Override
    public int compareTo(Node other) {
        return Float.compare(this.fCost, other.fCost);
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return position.epsilonEquals(other.position, 0.01f); // or exact comparison
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }


    private float getDistance(Vector2 a, Vector2 b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // Manhattan distance
    }
}
