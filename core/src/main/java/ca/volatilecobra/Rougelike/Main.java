package ca.volatilecobra.Rougelike;

import ca.volatilecobra.Rougelike.Entities.Enemies.Enemy;
import ca.volatilecobra.Rougelike.Entities.Entity;
import ca.volatilecobra.Rougelike.Entities.Player;
import ca.volatilecobra.Rougelike.Mods.DefaultTextures;
import ca.volatilecobra.Rougelike.Mods.Modloader;
import ca.volatilecobra.Rougelike.World.WorldManager;
import ca.volatilecobra.SettingsManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import static ca.volatilecobra.Rougelike.Entities.Entity.ENTITIES;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shape_renderer;
    private Texture image;
    public Player localPlayer;
    public WorldManager world;
    public BitmapFont font;
    public OrthographicCamera cam = new OrthographicCamera();



    @Override
    public void create() {
        Modloader.SearchForMods(System.getProperty("user.dir") + "/mods");
        Modloader.LoadSingleMod(new DefaultTextures());
        Modloader.loadMods();
        batch = new SpriteBatch();
        shape_renderer = new ShapeRenderer();
        font = new BitmapFont();
        localPlayer = new Player(new Vector2(0,0));
        System.out.println("Entity count: " + ENTITIES.size());
        world = new WorldManager(10);
        GlobalVariables.CAMERA = cam;
        cam.setToOrtho(false, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
        for (int i = 0; i < 5; i++) {
            new Enemy("enemy_" + i, new Vector2(-10 - i*8,-10)).brain.update_target(localPlayer);
        }

    }

    private void _process_keyboard_inputs(float delta){
        Vector2 inputDir = new Vector2();

        if (SettingsManager.get().controls.forward.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.y += 1;
        if (SettingsManager.get().controls.backward.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.y -= 1;
        if (SettingsManager.get().controls.left.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.x -= 1;
        if (SettingsManager.get().controls.right.stream().anyMatch(Gdx.input::isKeyPressed)) inputDir.x += 1;
        if (SettingsManager.get().controls.zoom_in.stream().anyMatch(Gdx.input::isKeyPressed)) cam.zoom = Math.max(0.01f, cam.zoom-1f * delta);
        if (SettingsManager.get().controls.zoom_out.stream().anyMatch(Gdx.input::isKeyPressed)) cam.zoom = Math.min(1f, cam.zoom + 1f*delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            GlobalVariables.DEBUG_ENABLED = !GlobalVariables.DEBUG_ENABLED;
            System.out.println("Debug status: " + GlobalVariables.DEBUG_ENABLED);
        }

        localPlayer.setDesiredDirection(inputDir);

        if (GlobalVariables.DEBUG_ENABLED && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();

            // Convert from screen coordinates to world coordinates (optional, see below)
            Vector3 worldPos = cam.unproject(new Vector3(mouseX, mouseY, 0));

            localPlayer.set_pos(new Vector2(worldPos.x, worldPos.y));
        }
    }

    private void draw_debug(SpriteBatch batch){
        Modloader.RenderModsDebug(batch);
    }
    private void draw_debug(ShapeRenderer shapeRenderer){
        Modloader.RenderModsDebug(shapeRenderer);
        world.draw_debug(shapeRenderer);
        Entity.draw_debug_all(shapeRenderer, null);
    }
    private void draw_debug_ui(ShapeRenderer shapeRenderer){
        Modloader.RenderModsDebugUI(shapeRenderer);
    }
    private void draw_debug_ui(SpriteBatch batch){
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() , 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Frametime: " + Gdx.graphics.getDeltaTime() , 10, Gdx.graphics.getHeight() - 30);
        font.draw(batch, "World Pos: " + localPlayer.get_pos(), 10, Gdx.graphics.getHeight() - 50);
        Modloader.RenderModsDebugUI(batch);
    }



    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        Modloader.UpdateMods(delta);
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        _process_keyboard_inputs(delta);

        Entity.update_all(delta, world);
        cam.position.set(localPlayer.get_pos().x + localPlayer.get_size().x /2, localPlayer.get_pos().y + localPlayer.get_size().y /2, 0);
        cam.update();


        //main render pass
        batch.setProjectionMatrix(cam.combined);
        shape_renderer.setProjectionMatrix(cam.combined);
        batch.begin();


        world.render(batch);
        if (GlobalVariables.DEBUG_ENABLED) draw_debug(batch);
        Entity.Render_all(batch);
        Modloader.RenderMods(batch);

        batch.end();
        shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
        Entity.Render_all(shape_renderer);
        Modloader.RenderMods(shape_renderer);
        if (GlobalVariables.DEBUG_ENABLED) draw_debug(shape_renderer);
        shape_renderer.end();

        //ui render pass
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        batch.begin();
        if (GlobalVariables.DEBUG_ENABLED) draw_debug_ui(batch);
        Modloader.RenderModsUI(batch);
        batch.end();
        shape_renderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
        if (GlobalVariables.DEBUG_ENABLED) draw_debug_ui(shape_renderer);
        shape_renderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();

        shape_renderer.dispose();
        Entity.Dispose_all();
    }
}
