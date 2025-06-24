package ca.volatilecobra.Rougelike.Entities.AI;

import ca.volatilecobra.Rougelike.Entities.Entity;
import ca.volatilecobra.Rougelike.GlobalVariables;
import ca.volatilecobra.Rougelike.Utils.Physics.Hit;
import ca.volatilecobra.Rougelike.Utils.Physics.Physics;
import ca.volatilecobra.Rougelike.World.Tile;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
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

    public float detonatingTime = 0f;
    public float explosionTime = 5f;

    public Vector2 ogSize = new Vector2(0,0);


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
    public AStar pathfinder = new AStar();

    public float timeRoaming = 0f;

    public float maxTimeRoaming = 120f;

    public float timeSinceRoamingSuccessMax = 10f;

    Vector2 currentVisibleTarget = new Vector2(0,0);

    private boolean debug_update = false;

    Vector2 debug_last_hit = new Vector2(0,0);


    Task current_task = Task.FOLLOW;

    public float max_dist_from_home = 1000f;


    public Vector2 home;

    boolean hasReachedTarget = true;

    float timeSinceRoamingSuccess = 0;


    Vector2 roamingTarget = null;


    public void requestPathAsync(Vector2 start, Vector2 goal, WorldManager world, Consumer<List<Vector2>> onComplete) {
        pathfinderDone = false;
        pathfinderAsyncExecuter.submit(() -> {
            Thread.currentThread().setName("AStar-Enemy_" + host.get_id());
            pathfinder.target = goal;
            List<Vector2> path = pathfinder.findPath(start, world);

            // Pass path to main thread
            Gdx.app.postRunnable(() -> onComplete.accept(path));
        });
    }


    Vector2 getRandomVec2(int x_max, int x_min, int y_max, int y_min){
        Random rnd = new Random();
        Vector2 vec =new Vector2(rnd.nextInt(x_min, x_max), rnd.nextInt(y_min,y_max));

        Vector2 scaled = new Vector2(new Vector2(vec).x / Tile.tile_size, new Vector2(vec).y / Tile.tile_size);
        Tile tile = worldManager.getTileAt(scaled);
        while (tile !=null && tile.collides){

            vec =new Vector2(rnd.nextInt(x_min, x_max), rnd.nextInt(y_min,y_max));

            scaled = new Vector2(new Vector2(vec).x / Tile.tile_size, new Vector2(vec).y / Tile.tile_size);
            tile = worldManager.getTileAt(scaled);
        };
        return vec;
    }

    public Brain(Entity host){
        this.host = host;
        this.ogSize = new Vector2(host.get_size());
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

        if (timeSinceUpdate >= updateinterval && (!host.isDead || host.timeSinceDeath >= host.respawnTime)) {
            timeSinceUpdate -= updateinterval;
            debug_update = true;


            float dist = Math.min(viewDist, host.get_pos().dst(Entity.Get_from_id("player").get_pos()));
            Hit raycastHit = Physics.raycast(host.get_pos(), Entity.Get_from_id("player").get_pos(), dist, worldManager, 16);
            if (!raycastHit.hitObstacle && raycastHit.reachedEnd && !GlobalVariables.DEBUG_INVISIBLE) {
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
                current_task = Task.IDLE;
            }


            Vector2 target = null;

            switch(current_task){
                case GO_HOME:{
                    target = new Vector2(home);
                    if (new Vector2(host.get_pos()).epsilonEquals(new Vector2(home), 100f)){
                        current_task = Task.IDLE;
                        timeRoaming = 0;
                        hasReachedTarget = true;

                    }
                    break;
                }
                case ROAM:{
                    timeRoaming +=updateinterval;
                    timeSinceRoamingSuccess += updateinterval;
                    int[][] directions = new int[][]{
                        {0,-1},
                        {1,0},
                        {0,1},
                        {-1,0}
                    };
                    Random rand = new Random();
                    int[] choice = directions[rand.nextInt(4)];

                    Vector2 pos_modified = getRandomVec2(300,100,300,100);


                    if (pos_modified.dst(home) >= max_dist_from_home || timeRoaming >= maxTimeRoaming){

                        current_task = Task.GO_HOME;
                        timeRoaming = 0;
                        target = new Vector2(home);
                        break;
                    }
                    if (hasReachedTarget || !pathfinder.couldFindPath() || timeSinceRoamingSuccess >= timeSinceRoamingSuccessMax){
                        timeSinceRoamingSuccess = 0;
                        current_task = Task.IDLE;
                    }
                    hasReachedTarget = host.get_pos().epsilonEquals(roamingTarget, 60f);

                    host.setDecel(1000f);


                    break;
                }
                case FOLLOW:{
                    target = currentVisibleTarget;
                    break;
                }

                case IDLE: {

                    HashMap<Task, Float> decisions = new HashMap<>();
                    decisions.put(Task.ROAM, 0.5f);
                    decisions.put(Task.IDLE, 0.29f);
                    decisions.put(Task.GO_HOME, 0.2f);
                    decisions.put(Task.DETONATE, 0.01f);

                    AtomicReference<Float> total= new AtomicReference<>((float) 0);
                    Random rnd = new Random();
                    float val = rnd.nextFloat(0,1);
                    final Task[] selected = new Task[]{Task.GO_HOME};
                    final boolean[] isFirst = {true};
                    decisions.forEach((task, weight) -> {
                        total.updateAndGet(v -> ((v + weight)));


                        float diffWeight = Math.abs(val - weight);
                        float diffSelected = Math.abs(val - decisions.get(selected[0]));
                        System.out.println("Diff Weight: " + diffWeight);
                        System.out.println("Diff Selected: " + diffSelected);

                        selected[0] = (diffWeight < diffSelected)|| isFirst[0] ? task : selected[0];
                        isFirst[0] = false;

                    });

                    // values must add up to 1 (100%)
                    assert total.get() == 1f;


                    current_task = selected[0];

                    switch (current_task){
                        case ROAM:{
                            target = getRandomVec2( 500,300, 500,300);
                            roamingTarget = new Vector2(target);
                            hasReachedTarget = false;
                            break;
                        }
                        case GO_HOME:{
                            target = new Vector2(home);
                            break;
                        }

                    }





                }
                case DETONATE:{
                    detonatingTime += updateinterval;
                    if (detonatingTime >= explosionTime){
                        host.isDead = true;
                        host.timeSinceDeath =0;
                        current_task = Task.IDLE;
                        detonatingTime = 0;
                        host.set_size(new Vector2(ogSize));
                        break;
                    }

                    //host.set_size(host.get_size().scl(2f));






                    break;
                }

            }

            requestPathAsync(new Vector2(host.getTilePos()).scl(Tile.tile_size).add(new Vector2(host.get_size()).scl(0.5f)), target, worldManager, path -> {
                if (path != null) {
                    lastPath = path; // Safe, runs on main thread
                    pathfinderDone = true;
                }
            });
        }
        // Current enemy position
        Vector2 currentPos = new Vector2(host.get_pos());

// Find the first point at most 10 units away
        Vector2 closestPoint = null;
        float closestDistance = Float.MAX_VALUE;

        for (Vector2 point : lastPath) {
            float distance = point.dst(currentPos);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPoint = point;
            }
        }

        if (closestPoint != null && closestDistance <= 40f) {
            walkingTarget = closestPoint;
            if (closestDistance <= 10f) {
                lastPath.remove(closestPoint);
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
        shapeRenderer.setColor(1,0,0,0.3f);
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
        shapeRenderer.setColor(1,0,1,1);
        if (debug_update){
            debug_update = false;
            Vector2 start = host.get_pos();
            Vector2 end = Entity.Get_from_id("player").get_pos();
            float dist = Math.min(viewDist, host.get_pos().dst(Entity.Get_from_id("player").get_pos()));
            Hit hit = Physics.raycast(start.cpy(), end.cpy(),dist,  worldManager, 16);
            debug_last_hit = hit.location;
        }

        shapeRenderer.line(host.get_pos().x, host.get_pos().y, debug_last_hit.x, debug_last_hit.y);

        if (current_task == Task.ROAM){
            shapeRenderer.setColor(1,0,1,1);
            shapeRenderer.circle(roamingTarget.x, roamingTarget.y, 10);
        }

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
        inc +=20;
        font.draw(batch, "FOUND PATH " + pathfinder.couldFindPath(), host.get_pos().x, host.get_pos().y-inc);
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
