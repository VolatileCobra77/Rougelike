package ca.volatilecobra.Rougelike.Entities.AI;

import ca.volatilecobra.Rougelike.Entities.Entity;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Brain {
    public Entity host;
    public float updateInterval = 0.1f;
    public ExecutorService pathfinderAsyncExecuter = Executors.newSingleThreadExecutor();
    public Vector2 current_target = new Vector2();
    public List<Vector2> lastPath = new ArrayList<>();
    boolean pathfinderDone = true;
    Vector2 walkingTarget = null;

    private float timeSinceLastUpdate = 0f;
    public void requestPathAsync(Vector2 start, Vector2 goal, WorldManager world, Consumer<List<Vector2>> onComplete) {
        pathfinderDone = false;
        pathfinderAsyncExecuter.submit(() -> {
            AStar astar = new AStar();
            astar.target = goal;
            List<Vector2> path = astar.findPath(start, world);

            // Pass path to main thread
            Gdx.app.postRunnable(() -> onComplete.accept(path));
        });
    }


    public Brain(){

    }

    public void update_target(Vector2 new_target){
       current_target = new_target;
    }

    public void update_target(Entity new_target){
        current_target = new_target.get_pos();
    }

    public void update(float delta, WorldManager worldManager){
        timeSinceLastUpdate += delta;
        if (timeSinceLastUpdate >= updateInterval && pathfinderDone){
            timeSinceLastUpdate -= updateInterval;

            requestPathAsync(host.get_pos(), current_target, worldManager, path -> {
                if (path != null) {
                    lastPath = path; // Safe, runs on main thread
                    System.out.println("PATHFINDER FINISHED");
                    pathfinderDone = true;
                }
            });
            System.out.println("FINDING PATH");
        }
        // Current enemy position
        Vector2 currentPos = host.get_pos();

// Find the first point at least 10 units away
        for (Vector2 point : lastPath) {
            if (point.dst(currentPos) >= 10f) {
                walkingTarget = point;
                break;
            }
        }

// If found, set desired direction
        if (walkingTarget != null) {
            Vector2 desiredDirection = new Vector2(walkingTarget).sub(currentPos).nor();
            host.setDesiredDirection(desiredDirection); // Example method
        }

    }
    public void draw_debug(ShapeRenderer shapeRenderer){
        Vector2 lastVector = null;
        shapeRenderer.setColor(1,0,0,1);
        try{
            for (Vector2 vector : lastPath) {
                if (lastVector == null) {
                    lastVector = vector;
                    continue;
                }

                shapeRenderer.line(lastVector, vector);
                lastVector = vector;
            }
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
    }

    public void dispose() {
        pathfinderAsyncExecuter.shutdownNow();
    }

}
