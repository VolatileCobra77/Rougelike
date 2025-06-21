package ca.volatilecobra.Rougelike.Mods;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public abstract class Mod {
    /**
     * @return The fqn of the mod's class
     */
    abstract String getName();

    /**
     * Called when mod is loaded
     */
    abstract void onLoad();

    /**
     * Called during the rendering process, when the ShapeRenderer is active. Used to generate shapes not from texture files
     * @param shapeRenderer the active ShapeRenderer from the rendering pipeline. called after all base game calls, so will always be on top of base game objects
     */
    abstract void Render_shapes(ShapeRenderer shapeRenderer);

    /**
     * Called during the rendering process, when the SpriteBatch is active. Used to render Texture objects to the screen amongst others.
     * @param spriteBatch the active SpriteBatch object. called after all base game calls, so will always be on top of base game objects
     */
    abstract void Render_objects(SpriteBatch spriteBatch);

    /**
     * Renders shapes seperate of the cameras projection matrix
     * @param shapeRenderer the shape renderer used
     *
     */

    abstract void Render_shapes_ui(ShapeRenderer shapeRenderer);

    /**
     * Renders objects seperate of the cameras projeection matrix
     * @param spriteBatch Sprite batch to render objects
     */
    abstract void Render_objects_ui(SpriteBatch spriteBatch);

    /**
     * Renders objects when GlobalVars.DEBUG_ACTIVE is true, aka f3 presed in game
     * @param spriteBatch Sprite batch to render objects
     */

    abstract void Render_debug_objects(SpriteBatch spriteBatch);

    /**
     * Renders shapes when the GlobalVars.DEBUG_ACTIVE is true, aka f3 pressed in game
     * @param shapeRenderer Shape renderer used
     */
    abstract void Render_debug_shapes(ShapeRenderer shapeRenderer);

    /**
     * Renders objects when the GlobalVars.DEBUG_ACTIVE is true, and seperate of the cameras projection matrix
     * @param spriteBatch Sprite batch used
     */

    abstract void Render_debug_objects_ui(SpriteBatch spriteBatch);

    /**
     * Renders shapes when the GlobalVars.DEBUG_ACTIVE is true, and seperate of the cameras projection matrix
     * @param shapeRenderer the shape renderer used
     */

    abstract void Render_debug_shapes_ui(ShapeRenderer shapeRenderer);

    /**
     * Called every frame, implement repeating logic such as enemy ai updates and custom controls here
     * @param dt time between frames.
     */
    abstract void Update(float dt);


    /**
     * @param directory The directory associated with the mod
     */
    public String directory = "";


    /**
     * Use this function instead of using new Texture("/path/to/texture") as that is realtive to the built in assets folder, which you do not have access to.
     * @param file the name of the file and path relative to <the mod's directory>/assets.
     * @return The Texture object associated with the passed in file, used to render objects with the SpriteBatch supplied to Render_objects.
     * @throws IOException When the funciton cannot find the directory.
     * @throws RuntimeException When the functioncannot find the file.
     */
    public Texture loadTexture(String file) throws IOException {
        FileHandle textureFile = Gdx.files.absolute(directory+"/assets").child(file);
        if (!textureFile.exists()){
            throw new RuntimeException("Missing texture: " + textureFile.path());
        }

        return new Texture(textureFile);
    }
}
