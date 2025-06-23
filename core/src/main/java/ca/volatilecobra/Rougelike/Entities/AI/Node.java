package ca.volatilecobra.Rougelike.Entities.AI;

import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;

public class Node implements Comparable<Node> {
    public Vector2 position;
    public Node parent;
    public float hCost, gCost, fCost;

    public Node(Vector2 position, Node parent, Vector2 goal) {
        this.position = position;
        this.parent = parent;

        gCost = (parent != null)
            ? parent.gCost + getDistance(parent.position, this.position)
            : 0;

        hCost = getDistance(this.position, goal);
        fCost = gCost + hCost;
    }

    private float getDistance(Vector2 a, Vector2 b) {
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y)) / 8f; // adjust for tile_size if needed
    }

    @Override
    public int compareTo(Node other) {
        if (this.fCost != other.fCost)
            return Float.compare(this.fCost, other.fCost);
        return Float.compare(this.hCost, other.hCost); // tie-breaker toward goal
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) return false;
        Node other = (Node) o;
        return (int) this.position.x == (int) other.position.x &&
            (int) this.position.y == (int) other.position.y;
    }

    @Override
    public int hashCode() {
        return 31 * ((int) position.x) + ((int) position.y);
    }
}
