package ca.volatilecobra.Rougelike.Entities.AI;

import ca.volatilecobra.Rougelike.Entities.Entity;
import ca.volatilecobra.Rougelike.Utils.Physics.Hit;
import ca.volatilecobra.Rougelike.Utils.Physics.Physics;
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

    public ExecutorService pathfinderAsyncExecuter = Executors.newSingleThreadExecutor();

    public Vector2 current_target = new Vector2();
    public List<Vector2> lastPath = new ArrayList<>();
    WorldManager worldManager = null;

    boolean pathfinderDone = true;
    Vector2 walkingTarget = null;

    float baseUpdateInterval = 1f;

    float targetAquiredUpdateInterval =0.1f;

    float updateinterval = baseUpdateInterval;

    float timeSinceLock = 0f;

    float timeSinceUpdate = 0f;

    float forgetTime = 1f;

    float viewDist = 250f;

    Vector2 currentVisibleTarget = new Vector2(0,0);

    private boolean debug_update = false;

    Vector2 debug_last_hit = new Vector2(0,0);



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
       current_target = new Vector2(new_target);
    }

    public void update_target(Entity new_target){
        current_target = new Vector2(new_target.get_pos());
    }

    public void update(float delta, WorldManager worldManager){
        this.worldManager = worldManager;
        timeSinceUpdate += delta;
        timeSinceLock += delta;
        if (timeSinceUpdate >= updateinterval) {
            timeSinceUpdate -= updateinterval;
            debug_update = true;


            float dist = Math.min(viewDist, host.get_pos().dst(Entity.Get_from_id("player").get_pos()));
            Hit raycastHit = Physics.raycast(host.get_pos(), Entity.Get_from_id("player").get_pos(), dist, worldManager, 16);
            if (!raycastHit.hitObstacle && raycastHit.reachedEnd) {
                currentVisibleTarget = new Vector2(raycastHit.location);
                updateinterval = targetAquiredUpdateInterval;
                timeSinceLock = 0;
            } else if(timeSinceLock >= forgetTime) {
                updateinterval = baseUpdateInterval;
            }




            requestPathAsync(host.get_pos(), currentVisibleTarget, worldManager, path -> {
                if (path != null) {
                    lastPath = path; // Safe, runs on main thread
                    pathfinderDone = true;
                }
            });
        }
        // Current enemy position
        Vector2 currentPos = host.get_pos();

// Find the first point at least 10 units away
        for (Vector2 point : lastPath) {
            if (point.dst(currentPos) >= 10f) {
                try{
                    walkingTarget = point;
                    if (currentPos.dst(walkingTarget) >= 1f){

                        lastPath.remove(point);
                        break;
                    }
                }catch (Exception ignored){};
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
        shapeRenderer.setColor(1,0,0,1);
        if (debug_update){
            debug_update = false;
            Vector2 start = host.get_pos();
            Vector2 end = Entity.Get_from_id("player").get_pos();
            float dist = Math.min(viewDist, host.get_pos().dst(Entity.Get_from_id("player").get_pos()));
            Hit hit = Physics.raycast(start, end,dist,  worldManager, 16);
            debug_last_hit = hit.location;
        }
        shapeRenderer.line(host.get_pos().x, host.get_pos().y, debug_last_hit.x, debug_last_hit.y);

    }

    public void dispose() {
        pathfinderAsyncExecuter.shutdownNow();
    }

}
