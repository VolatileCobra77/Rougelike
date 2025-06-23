package ca.volatilecobra.Rougelike.Entities;

import ca.volatilecobra.Rougelike.Utils.Animation.Animator;
import ca.volatilecobra.Rougelike.World.Tile;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

    String _id = "";
    Vector2 _pos = new Vector2(0,0);
    Vector2 _size = new Vector2(0,0);
    float _rot = 0;
    Texture _tex;
    boolean isAnimated = false;
    public Animator animator = null;
    Vector2 _velocity = new Vector2(0,0);
    public Color _fallback_color = new Color(1,0,0,1);



    public static Map<String, Entity> ENTITIES = new HashMap<>();


    Vector2 _desiredDirection = new Vector2(0,0); // Normalized input or path direction

    float _acceleration = 600f;   // units per second^2
    float _deceleration = 400f;   // units per second^2
    float _maxVelocity = 300f;    // units per second

    public float maxHealth = 100f;

    public float health = maxHealth;

    public boolean isDead = false;

    public float respawnTime = 1f;

    public float timeSinceDeath = 0f;

    public Vector2 respawnLocation = new Vector2(0,0);


    public void damage(float ammount){

        health = Math.max(0, health - ammount);
        if (health <=0){
            isDead = true;
            _pos = new Vector2(respawnLocation);
            timeSinceDeath = 0;
        }

    }


    public void setAccel(float accel){
        _acceleration = accel;
    }

    public void setDecel(float decel){
        _deceleration = decel;
    }

    public void setMaxVel(float maxVel){
        _maxVelocity = maxVel;
    }

    // Call this every frame to update movement
    public void update(float delta, WorldManager worldManager) {
        Vector2 accelerationVector = new Vector2(0,0);
        timeSinceDeath += delta;
        if (!isDead || timeSinceDeath >= respawnTime) {
            accelerationVector = new Vector2(_desiredDirection).scl(_acceleration * delta);
        }
        if (_tex != null){
            _size.x = _tex.getWidth();
            //for an overlap to simulate it being semi orthographic, we subtract a small fixed amount
            _size.y = _tex.getHeight()-5;
        } else if (isAnimated) {

            _size.x = animator.getCurrentFrame().getHeight();

            //for an overlap to simulate being semi orthographic, we subtract a small fixed amount
            _size.y = animator.getCurrentFrame().getHeight() - 5;
            animator.update(delta);

        }



        // If there's no desired movement, decelerate
        if (_desiredDirection.isZero(0.01f)) {
            applyDeceleration(delta);
        } else {
            _velocity.add(accelerationVector);
        }

        // Clamp velocity
        if (_velocity.len() > _maxVelocity) {
            _velocity.setLength(_maxVelocity);
        }

        // Predict movement separately on each axis
        Vector2 proposedMove = new Vector2(_velocity).scl(delta);

        // Try X movement
        if (!willCollideAt(_pos.x + proposedMove.x, _pos.y, worldManager)) {
            _pos.x += proposedMove.x;
        } else {
            _velocity.x = 0; // stop horizontal movement if blocked
        }

        // Try Y movement
        if (!willCollideAt(_pos.x, _pos.y + proposedMove.y, worldManager)) {
            _pos.y += proposedMove.y;
        } else {
            _velocity.y = 0; // stop vertical movement if blocked
        }


        if (willCollideAt(_pos.x, _pos.y, worldManager)){
            Vector2 safeSpot = findNearestSafeSpot(_pos, 64, worldManager); // max search range in pixels

            if (safeSpot != null) {
                _pos.set(safeSpot);
                _velocity.setZero(); // stop momentum
            } else {
                System.out.println("No safe spot found for entity " + _id);
            }
        }
    }

    private Vector2 findNearestSafeSpot(Vector2 startPos, int maxDistance, WorldManager worldManager) {
        int step = 4; // pixels per search step
        for (int radius = step; radius <= maxDistance; radius += step) {
            for (int dx = -radius; dx <= radius; dx += step) {
                for (int dy = -radius; dy <= radius; dy += step) {
                    if (Math.abs(dx) != radius && Math.abs(dy) != radius) continue; // Only check perimeter

                    float newX = startPos.x + dx;
                    float newY = startPos.y + dy;

                    if (!willCollideAt(newX, newY, worldManager)) {
                        return new Vector2(newX, newY);
                    }
                }
            }
        }

        return null; // No safe spot found within maxDistance
    }
    public void draw_debug(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(1,1,0,1);
        shapeRenderer.rect(_pos.x, _pos.y, _size.x,_size.y);
    }
    public void draw_debug(SpriteBatch spriteBatch){

    }

    public static void draw_debug_all(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch){


        for (Entity entity: ENTITIES.values()){
            if (shapeRenderer !=null){
                entity.draw_debug(shapeRenderer);
            }else{
                entity.draw_debug(spriteBatch);
            }
        }
    }

    private boolean willCollideAt(float x, float y, WorldManager worldManager) {
        float width = _size.x;
        float height = _size.y;

        // Check tile collisions
        int minTileX = (int)Math.floor(x / Tile.tile_size);
        int maxTileX = (int)Math.floor((x + width - 1) / Tile.tile_size);
        int minTileY = (int)Math.floor(y / Tile.tile_size);
        int maxTileY = (int)Math.floor((y + height - 1) / Tile.tile_size);

        for (int tx = minTileX; tx <= maxTileX; tx++) {
            for (int ty = minTileY; ty <= maxTileY; ty++) {
                Tile tile = worldManager.getTileAt(new Vector2(tx, ty));
                if (tile != null && tile.collides) {
                    return true;
                }
            }
        }

        // Check collisions with other entities
        for (Entity other : ENTITIES.values()) {
            if (other == this) continue;

            Vector2 otherPos = other._pos;
            if (rectsOverlap(x, y, width, height, otherPos.x, otherPos.y, other._size.x, other._size.y)) {
                return true;
            }
        }

        return false;
    }

    private boolean rectsOverlap(float x1, float y1, float w1, float h1,
                                 float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 &&
            x1 + w1 > x2 &&
            y1 < y2 + h2 &&
            y1 + h1 > y2;
    }


    // Sets the desired movement direction (should be normalized or will auto-normalize)
    public void setDesiredDirection(Vector2 direction) {
        if (!direction.isZero(0.01f)) {
            _desiredDirection.set(direction).nor();
        } else {
            _desiredDirection.setZero();
        }
    }

    // Decelerate when no input is given
    void applyDeceleration(float delta) {
        if (_velocity.isZero(0.01f)) return;

        float decelAmount = _deceleration * delta;
        float currentSpeed = _velocity.len();

        if (currentSpeed <= decelAmount) {
            _velocity.setZero();
        } else {
            _velocity.setLength(currentSpeed - decelAmount);
        }
    }

    public static void update_all(float deltatime, WorldManager worldManager){
        for (Entity entity : ENTITIES.values()){
            entity.update(deltatime, worldManager);
        }
    }


    // Getters for movement properties
    public float getAcceleration() {
        return _acceleration;
    }

    public float getDeceleration() {
        return _deceleration;
    }

    public float getMaxVelocity() {
        return _maxVelocity;
    }

    public Vector2 getVelocity() {
        return new Vector2(_velocity);
    }

    public Vector2 getPosition() {
        return new Vector2(_pos);
    }

    public static void Render_all(SpriteBatch sprite_batch){
        for (Entity entity : ENTITIES.values()){
            if (entity.has_texture() || entity.isAnimated){
                entity.Render(sprite_batch);
            }
        }
    }
    public static void Render_all(ShapeRenderer shape_renderer){
        for (Entity entity : ENTITIES.values()){
            if (!entity.has_texture() && !entity.isAnimated){
                entity.Render(null, shape_renderer);
            }
        }
    }
    public static Entity Get_from_id(String id){
        return ENTITIES.get(id);
    }

    public static String Get_id_from_entity(Entity entity) {
        for (Map.Entry<String, Entity> entry : ENTITIES.entrySet()) {
            if (entry.getValue().equals(entity)) {
                return entry.getKey();
            }
        }
        return null;
    }



    public Entity(String id){
        _id = id;
        _tex = null;
        ENTITIES.put(_id, this);
    }

    public Entity(String id, Vector2 start_pos, float start_rot){
        _id = id;
        _pos = start_pos;
        _size = new Vector2(1,1);
        _rot = start_rot;
        _tex = null;
        ENTITIES.put(_id, this);
    }

    public Entity(String id, Vector2 start_pos, Vector2 size, float start_rot) {
        _id = id;
        _pos = start_pos;
        _size = size;
        _rot = start_rot;
        _tex = null;
        ENTITIES.put(_id, this);
    }

    public Entity(String id, Vector2 start_pos, Vector2 size, float start_rot, Texture texture){
        _id = id;
        _pos = start_pos;
        _size = size;
        _rot = start_rot;
        _tex = texture;
        ENTITIES.put(_id, this);
    }

    public Entity(String id, Vector2 start_pos, Vector2 size, float start_rot, Animator animator){
        _id = id;
        _pos = start_pos;
        _size = size;
        _rot = start_rot;
        _tex = null;
        isAnimated = true;
        this.animator = animator;
        ENTITIES.put(_id, this);
    }

    public Entity(String id, Vector2 start_pos, Vector2 size, float start_rot, boolean isAnimated){
        _id = id;
        _pos = start_pos;
        _size = size;
        _rot = start_rot;
        _tex = null;
        this.isAnimated = isAnimated;
        if (isAnimated){
            this.animator = new Animator();
        }
        ENTITIES.put(_id, this);
    }

    public void Render(SpriteBatch sprite_batch){
        if (!isDead || timeSinceDeath >= respawnTime) {
            isDead = false;
            if (isAnimated) {
                animator.draw(sprite_batch, _pos);
                return;
            }
            if (_tex == null) {
                throw new IllegalStateException("_tex was not defined, either pass in a shape renderer to the render function or specify a texture in the constructor");
            }


            sprite_batch.draw(_tex, _pos.x, _pos.y);
        }
    }
    public void Render(SpriteBatch sprite_batch, ShapeRenderer shape_renderer){
        if (!isDead || timeSinceDeath >= respawnTime) {
            isDead = false;
            if (_tex != null) {
                //System.err.println("WARNING: shape_renderer passed but _tex is defined, using texture");
                Render(sprite_batch);
                return;
            }
            shape_renderer.setColor(_fallback_color);
            shape_renderer.rect(_pos.x, _pos.y, _size.x, _size.y);
        }
    }


    public boolean Collides_with(Entity other){
        return _pos.x >= other.get_pos().x && _pos.y >= other.get_pos().y && _pos.x + _size.x <= other.get_pos().x + other.get_size().x && _pos.y + _size.y <= other.get_pos().y + other.get_size().y;
    }

    public Vector2 get_size(){
        return _size;
    }

    public void set_size(Vector2 size){
        _size = size;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Vector2 get_pos() {
        return _pos;
    }

    public void Move(Vector2 new_pos){
        this._pos = new_pos;
    }

    public void Move(Vector2 new_pos, List<Entity> other_colliders){
        // do collision detection later
        this._pos = new_pos;
    }


    public float get_rot() {
        return _rot;
    }

    public void set_rot(float _rot) {
        this._rot = _rot;
    }
    public boolean has_texture(){
        return _tex!=null;
    }
    public boolean has_anim(){
        return isAnimated;
    }

    public void dispose(){
        if (_tex != null){
            _tex.dispose();
        }
        if (animator != null){
            animator.dispose();
        }
    }

    public static <T> List<T> getAllSubclass(Class<T> type) {
        List<T> returnList = new ArrayList<>();
        for (Entity entity : ENTITIES.values()) {
            if (type.isInstance(entity)) {
                returnList.add(type.cast(entity));
            }
        }
        return returnList;
    }
    public static void Dispose_all(){
        for (Entity entity : ENTITIES.values()){
            entity.dispose();
        }
    }

}
