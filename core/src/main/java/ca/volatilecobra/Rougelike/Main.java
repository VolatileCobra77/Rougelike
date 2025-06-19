package ca.volatilecobra.Rougelike;

import ca.volatilecobra.Rougelike.Entities.Entity;
import ca.volatilecobra.Rougelike.Entities.Player;
import ca.volatilecobra.Rougelike.Mods.Modloader;
import ca.volatilecobra.Rougelike.World.WorldManager;
import ca.volatilecobra.SettingsManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import net.bytebuddy.agent.builder.AgentBuilder;

import static ca.volatilecobra.Rougelike.Entities.Entity.ENTITIES;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shape_renderer;
    private Texture image;
    public Player localPlayer;
    public WorldManager world;
    public BitmapFont font;



    @Override
    public void create() {
        Modloader.SearchForMods("assets/mods");
        Modloader.SearchForMods(System.getProperty("user.dir") + "/mods");
        Modloader.loadMods();
        batch = new SpriteBatch();
        shape_renderer = new ShapeRenderer();
        font = new BitmapFont();
        localPlayer = new Player(new Vector2(0,0));
        System.out.println("Entity count: " + ENTITIES.size());
        world = new WorldManager(100,100,10);

    }

    private void _process_keyboard_inputs(){
        Vector2 inputDir = new Vector2();

        if (SettingsManager.get().controls.forward.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.y += 1;
        if (SettingsManager.get().controls.backward.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.y -= 1;
        if (SettingsManager.get().controls.left.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.x -= 1;
        if (SettingsManager.get().controls.right.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.x += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.F3)) GlobalVariables.DEBUG_ENABLED = !GlobalVariables.DEBUG_ENABLED;


        localPlayer.setDesiredDirection(inputDir);
    }

    private void draw_debug(SpriteBatch batch){
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() , 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Frametime: " + Gdx.graphics.getDeltaTime() , 10, Gdx.graphics.getHeight() - 20);
    }
    private void draw_debug(ShapeRenderer shapeRenderer){

    }




    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        Modloader.UpdateMods(delta);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        _process_keyboard_inputs();
        Entity.update_all(delta);





        batch.begin();
        Entity.Render_all(batch);
        Modloader.RenderMods(batch);
        world.render(batch);
        if (GlobalVariables.DEBUG_ENABLED) draw_debug(batch);
        batch.end();
        shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
        Entity.Render_all(shape_renderer);
        Modloader.RenderMods(shape_renderer);
        if (GlobalVariables.DEBUG_ENABLED) draw_debug(shape_renderer);
        shape_renderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
