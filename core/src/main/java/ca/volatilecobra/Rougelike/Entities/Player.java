package ca.volatilecobra.Rougelike.Entities;

import ca.volatilecobra.Rougelike.Utils.Animation.Animation;
import ca.volatilecobra.Rougelike.Utils.Animation.Animator;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity{


    public Player(Vector2 start_pos){
        super("player", start_pos, new Vector2(10,10), 0f, true);
        // Walking Up
        Texture[] walk_up_textures = new Texture[4];
        for (int i = 0; i < 4; i++) {
            walk_up_textures[i] = new Texture("entities/player/walking/up/" + i + ".png");
        }
        Animation walk_up_anim = new Animation(walk_up_textures, 0.1f);

// Walking Down
        Texture[] walk_down_textures = new Texture[4];
        for (int i = 0; i < 4; i++) {
            walk_down_textures[i] = new Texture("entities/player/walking/down/" + i + ".png");
        }
        Animation walk_down_anim = new Animation(walk_down_textures, 0.1f);

// Walking Side (Right by default)
        Texture[] walk_right_textures = new Texture[4];
        for (int i = 0; i < 4; i++) {
            walk_right_textures[i] = new Texture("entities/player/walking/left/" + i + ".png");
        }
        Animation walk_right_anim = new Animation(walk_right_textures, 0.1f);

// Walking Left (flip horizontally)
        Texture[] walk_left_textures = new Texture[4];
        for (int i = 0; i < 4; i++) {
            walk_left_textures[i] = new Texture("entities/player/walking/right/" + i + ".png");
            walk_left_textures[i].setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // Optional
        }
        Animation walk_left_anim = new Animation(walk_left_textures, 0.1f);

        animator.add("walking_left", walk_left_anim);
        animator.add("walking_right", walk_right_anim);
        animator.add("walking_up", walk_up_anim);
        animator.add("walking_down", walk_down_anim);

        Texture[] idle = new Texture[4];
        idle[0] = new Texture("entities/player/walking/up/0.png");
        idle[1] = new Texture("entities/player/walking/right/0.png");
        idle[2] = new Texture("entities/player/walking/down/0.png");
        idle[3] = new Texture("entities/player/walking/left/0.png");

        Animation idle_anim = new Animation(idle, 0.1f);
        animator.add("idle", idle_anim);
        animator.switch_to("idle");
        _fallback_color = new Color(0,1,0,1);

    }

    @Override
    public void update(float delta, WorldManager worldManager) {

        // Animation switching
        if (_desiredDirection.isZero(0.01f)) {
            animator.switch_to("idle");
        } else {
            // Determine primary direction
            if (Math.abs(_desiredDirection.x) > Math.abs(_desiredDirection.y)) {
                if (_desiredDirection.x > 0) {
                    animator.switch_to("walking_right");
                } else {
                    animator.switch_to("walking_left");
                }
            } else {
                if (_desiredDirection.y > 0) {
                    animator.switch_to("walking_up");
                } else {
                    animator.switch_to("walking_down");
                }
            }
        }
        super.update(delta, worldManager);
    }

    public void set_pos(Vector2 new_pos){
        _pos = new_pos;
    }

}
