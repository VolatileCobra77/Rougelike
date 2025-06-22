package ca.volatilecobra.Rougelike.Utils.Animation;

import com.badlogic.gdx.graphics.Texture;

public class Animation {
    public Texture[] frames;
    public int num_frames = 0;
    public float frametime = 0.1f;

    public Animation(Texture[] frames, float frametime){
        this.frames = frames;
        num_frames = frames.length;
        this.frametime = frametime;
    }
    public void dispose(){
        for (Texture tex : frames){
            tex.dispose();
        }
    }
}
