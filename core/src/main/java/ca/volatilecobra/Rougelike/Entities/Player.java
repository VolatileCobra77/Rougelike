package ca.volatilecobra.Rougelike.Entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Player extends Entity{


    public Player(Vector2 start_pos){
        super("player", start_pos, new Vector2(10,10), 0, null);
        _fallback_color = new Color(0,1,0,1);
    }

}
