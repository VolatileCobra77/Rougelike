package ca.volatilecobra.Rougelike.Entities.AI;

import ca.volatilecobra.Rougelike.Entities.Entity;
import ca.volatilecobra.Rougelike.Utils.Physics.Hit;
import ca.volatilecobra.Rougelike.Utils.Physics.Physics;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    public float baseUpdateInterval = 1f;

    public float baseAccel = 500;

    public float targetAquiredAccel = 1000;

    public float targetAquiredDecel = 400;

    public float baseDecel = 200;

    public float targetAquiredMaxVel = 300;

    public float baseMaxVel = 150;

    public float targetAquiredUpdateInterval =0.1f;

    public float updateinterval = baseUpdateInterval;

    public float timeSinceLock = 0f;

    public float timeSinceUpdate = 0f;

    public float forgetTime = 1f;

    public float viewDist = 250f;

    public float timeRoaming = 0f;

    public float maxTimeRoaming = 120f;

    Vector2 currentVisibleTarget = new Vector2(0,0);

    private boolean debug_update = false;

    Vector2 debug_last_hit = new Vector2(0,0);


    Task current_task = Task.FOLLOW;

    public float max_dist_from_home = 1000f;


    public Vector2 home;

    boolean hasReachedTarget = true;

    Vector2 roamingTarget = null;


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
                current_task = Task.FOLLOW;
                updateinterval = targetAquiredUpdateInterval;
                timeSinceLock = 0;
                host.setAccel(targetAquiredAccel);
                host.setMaxVel(targetAquiredMaxVel);
                host.setDecel(targetAquiredDecel);
            } else if(timeSinceLock >= forgetTime && current_task == Task.FOLLOW) {
                host.setAccel(baseAccel);
                host.setMaxVel(baseMaxVel);
                host.setDecel(baseDecel);
                updateinterval = baseUpdateInterval;
                current_task = Task.ROAM;
            }


            Vector2 target = null;

            switch(current_task){
                case GO_HOME:{
                    System.out.println("Going Home");
                    target = new Vector2(home);
                    if (new Vector2(host.get_pos()).epsilonEquals(new Vector2(home), 100f)){
                        current_task = Task.ROAM;
                        Random rnd = new Random();
                        target = new Vector2(home).add(new Vector2(rnd.nextInt((int)-(max_dist_from_home - 50), (int)max_dist_from_home-50),rnd.nextInt((int)-(max_dist_from_home - 50), (int)max_dist_from_home-50)));
                        roamingTarget = new Vector2(target);
                        hasReachedTarget = false;

                    }
                    break;
                }
                case ROAM:{
                    System.out.println("Roaming");
                    timeRoaming +=updateinterval;

                    int[][] directions = new int[][]{
                        {0,-1},
                        {1,0},
                        {0,1},
                        {-1,0}
                    };
                    Random rand = new Random();
                    int[] choice = directions[rand.nextInt(4)];

                    Vector2 pos_modified = new Vector2(host.get_pos()).add(new Vector2(choice[0], choice[1]).scl(rand.nextInt(100, 300)));


                    if (pos_modified.dst(home) >= max_dist_from_home || timeRoaming >= maxTimeRoaming){

                        current_task = Task.GO_HOME;
                        timeRoaming = 0;
                        target = new Vector2(home);
                        break;
                    }
                    if (hasReachedTarget){
                        target = new Vector2(pos_modified);
                        roamingTarget = new Vector2(target);
                        hasReachedTarget = false;
                    }
                    hasReachedTarget = host.get_pos().epsilonEquals(roamingTarget, 60f);



                    break;
                }
                case FOLLOW:{
                    System.out.println("following");
                    target = currentVisibleTarget;
                    break;
                }

            }

            requestPathAsync(host.get_pos(), target, worldManager, path -> {
                if (path != null) {
                    lastPath = path; // Safe, runs on main thread
                    pathfinderDone = true;
                }
            });
        }
        // Current enemy position
        Vector2 currentPos = new Vector2(host.get_pos());

// Find the first point at most 10 units away
        for (Vector2 point : lastPath) {
            if (point.dst(currentPos) <= 40f) {
                try{
                    walkingTarget = point;
                    if (currentPos.dst(walkingTarget) <= 40f){

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
    public void draw_debug(SpriteBatch batch){
        BitmapFont font = new BitmapFont();
        font.setColor(0,1,1,1);
        int inc =0;
        font.draw(batch, current_task.name(), host.get_pos().x, host.get_pos().y);
        inc+=20;
        font.draw(batch, new Vector2(host.get_pos()).toString(), host.get_pos().x, host.get_pos().y-inc);
        inc+=20;
        font.draw(batch, "HOME " + home.toString(), host.get_pos().x, host.get_pos().y-inc);
        inc+=20;
        font.draw(batch, "TARGET " + walkingTarget, host.get_pos().x, host.get_pos().y -inc);
        if (current_task == Task.ROAM) {
            inc+=20;
            font.draw(batch, "TIME ROAMING " + timeRoaming, host.get_pos().x, host.get_pos().y - inc);
            inc+=20;
            font.draw(batch, "HAS REACHED ROAM TARGET " + hasReachedTarget, host.get_pos().x, host.get_pos().y - inc);
            inc += 20;
            font.draw(batch, "ROAM TARGET " + roamingTarget, host.get_pos().x, host.get_pos().y - inc);
        };
    }

    public void dispose() {
        pathfinderAsyncExecuter.shutdownNow();
    }

}
