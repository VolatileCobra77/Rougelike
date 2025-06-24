package ca.volatilecobra.Rougelike.Entities.Enemies;

import ca.volatilecobra.Rougelike.Entities.AI.Brain;
import ca.volatilecobra.Rougelike.Entities.Entity;
import ca.volatilecobra.Rougelike.World.WorldManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Entity {
    public Brain brain;

    public Enemy(String id, Vector2 startPos){
        super(id, startPos, new Vector2 (8,16), 0f);
        _fallback_color = new Color(1,0,0,1);
        brain = new Brain(this);
        brain.home = new Vector2(this.get_pos());

    }
    @Override
    public void update(float delta, WorldManager worldManager){

        brain.update(delta, worldManager);
        brain.update_target(Entity.Get_from_id("player"));
        super.update(delta, worldManager);
    }

    @Override
    public void draw_debug(ShapeRenderer shapeRenderer) {
        super.draw_debug(shapeRenderer);
        brain.draw_debug(shapeRenderer);
    }
    @Override
    public void draw_debug(SpriteBatch batch){

        brain.draw_debug(batch);
    }

    @Override
    public void dispose(){
        brain.dispose();
        super.dispose();
    }
}
