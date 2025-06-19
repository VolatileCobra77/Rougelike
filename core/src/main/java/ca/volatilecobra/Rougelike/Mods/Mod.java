package ca.volatilecobra.Rougelike.Mods;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Mod {
    String getName();
    void onLoad();
    void Render_shapes(ShapeRenderer shapeRenderer);
    void Render_objects(SpriteBatch spriteBatch);
    void Update(float dt);
}
