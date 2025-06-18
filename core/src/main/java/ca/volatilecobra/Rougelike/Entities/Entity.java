package ca.volatilecobra.Rougelike.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {

    private String _id = "";
    private Vector2 _pos = new Vector2(0,0);
    private Vector2 _size = new Vector2(0,0);
    private float _rot = 0;
    private Texture _tex;
    private Vector2 _velocity = new Vector2(0,0);
    public Color _fallback_color = new Color(1,0,0,1);

    public static Map<String, Entity> ENTITIES = new HashMap<>();


    private Vector2 _desiredDirection = new Vector2(0,0); // Normalized input or path direction

    private final float _acceleration = 600f;   // units per second^2
    private final float _deceleration = 400f;   // units per second^2
    private final float _maxVelocity = 300f;    // units per second

    // Call this every frame to update movement
    public void update(float delta) {
        Vector2 accelerationVector = new Vector2(_desiredDirection).scl(_acceleration * delta);

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

        // Move the entity
        _pos.add(new Vector2(_velocity).scl(delta));
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
    private void applyDeceleration(float delta) {
        if (_velocity.isZero(0.01f)) return;

        float decelAmount = _deceleration * delta;
        float currentSpeed = _velocity.len();

        if (currentSpeed <= decelAmount) {
            _velocity.setZero();
        } else {
            _velocity.setLength(currentSpeed - decelAmount);
        }
    }

    public static void update_all(float deltatime){
        for (Entity entity : ENTITIES.values()){
            entity.update(deltatime);
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
            if (entity.has_texture()){
                entity.Render(sprite_batch);
            }
        }
    }
    public static void Render_all(ShapeRenderer shape_renderer){
        for (Entity entity : ENTITIES.values()){
            if (!entity.has_texture()){
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

    public Entity(String id, Vector2 start_pos, Vector2 size, float start_rot, Texture texture){
        _id = id;
        _pos = start_pos;
        _size = size;
        _rot = start_rot;
        _tex = texture;
        ENTITIES.put(_id, this);
    }

    public void Render(SpriteBatch sprite_batch){
        if (_tex == null){
            throw new IllegalStateException("_tex was not defined, either pass in a shape renderer to the render function or specify a texture in the constructor");
        }
        sprite_batch.draw(_tex, _pos.x, _pos.y);
    }
    public void Render(SpriteBatch sprite_batch, ShapeRenderer shape_renderer){
        if (_tex != null){
            //System.err.println("WARNING: shape_renderer passed but _tex is defined, using texture");
            Render(sprite_batch);
            return;
        }
        shape_renderer.setColor(_fallback_color);
        shape_renderer.rect(_pos.x, _pos.y, _size.x, _size.y);

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
}
