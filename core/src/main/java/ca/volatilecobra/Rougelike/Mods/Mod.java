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
