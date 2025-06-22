package ca.volatilecobra.Rougelike.Utils.Animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class Animator {
    private final Map<String, Animation> animations = new HashMap<>();
    private String current;

    private float stateTime = 0f;

    private int current_anim_frame;


    public void add(String name, Animation animation) {
        animations.put(name, animation);
        if (current == null) current = name;
    }
    public void add(String name, Texture[] frames, float frametime){
        animations.put(name, new Animation(frames, frametime));
        if (current == null) current = name;
    }

    public void switch_to(String name) {
        if (current == name) return;
        current = name;
        current_anim_frame = 0;
        System.out.println("swapping to animation " + name);
    }

    public void update(float delta) {
        stateTime += delta;
        Animation currentAnimation = animations.get(current);
        if (stateTime >= currentAnimation.frametime){
            stateTime -= currentAnimation.frametime;
            current_anim_frame ++;
            current_anim_frame %= currentAnimation.num_frames;
        }
    }

    public Texture getFrame(String animation, int frame){
        return animations.get(animation).frames[frame];
    }

    public Texture getCurrentFrame(){
        return animations.get(current).frames[current_anim_frame];
    }

    public void draw(SpriteBatch spriteBatch, Vector2 pos){
        spriteBatch.draw(animations.get(current).frames[current_anim_frame], pos.x, pos.y);
    }

    public String getCurrent() {
        return current;
    }
}
