package ca.volatilecobra.Rougelike.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class Entity {

    private String _id = "";
    private Vector2 _pos = Vector2.Zero;
    private Vector2 _size = Vector2.Zero;
    private float _rot = 0;
    private Texture _tex;

    public static Map<String, Entity> ENTITIES;

    public static void Render_all(SpriteBatch sprite_batch, ShapeRenderer shape_renderer){
        for (Entity entity : ENTITIES.values()){
            entity.Render(sprite_batch, shape_renderer);
        }
    }


    public Entity(String id){
        _id = id;
        _tex = null;
    }

    public Entity(String id, Vector2 start_pos, float start_rot){
        _id = id;
        _pos = start_pos;
        _size = new Vector2(1,1);
        _rot = start_rot;
        _tex = null;
    }

    public Entity(String id, Vector2 start_pos, Vector2 size, float start_rot, Texture texture){
        _id = id;
        _pos = start_pos;
        _size = size;
        _rot = start_rot;
        _tex = texture;
    }

    public void Render(SpriteBatch sprite_batch){
        if (_tex == null){
            throw new IllegalStateException("_tex was not defined, either pass in a shape renderer to the render function or specify a texture in the constructor");
        }
        sprite_batch.draw(_tex, _pos.x, _pos.y);
    }
    public void Render(SpriteBatch sprite_batch, ShapeRenderer shape_renderer){
        if (_tex != null){
            System.err.println("WARNING: shape_renderer passed but _tex is defined, using texture");
            Render(sprite_batch);
            return;
        }

        shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
        shape_renderer.setColor(1,0,0,1);
        shape_renderer.rect(_pos.x, _pos.y, _size.x, _size.y);
        shape_renderer.end();

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
}
