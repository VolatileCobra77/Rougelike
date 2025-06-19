package ca.volatilecobra.Rougelike.World;

import ca.volatilecobra.Rougelike.Entities.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Tile {

    public Texture _tex;
    public Vector2 _world_pos;

    public List<Tile> allowed_up = new ArrayList<>();
    public List<Tile> allowed_down = new ArrayList<>();
    public List<Tile> allowed_left = new ArrayList<>();
    public List<Tile> allowed_right = new ArrayList<>();

    public List<Tile> allowed_up_left = new ArrayList<>();
    public List<Tile> allowed_up_right = new ArrayList<>();
    public List<Tile> allowed_down_left = new ArrayList<>();
    public List<Tile> allowed_down_right = new ArrayList<>();

    public String name;

    // Global tile registry
    public static final Map<String, Tile> TILES = new HashMap<>();

    public Tile(String name, Texture tex) {
        this.name = name;
        this._tex = tex;
        this._world_pos = new Vector2();

        if (TILES.containsKey(name)) {
            throw new IllegalArgumentException("Tile with name '" + name + "' already exists.");
        }

        TILES.put(name, this);
    }

    public static Tile getByName(String name) {
        return TILES.get(name);
    }

    public void setAllowedNeighbors(
        List<Tile> up,
        List<Tile> down,
        List<Tile> left,
        List<Tile> right,
        List<Tile> upLeft,
        List<Tile> upRight,
        List<Tile> downLeft,
        List<Tile> downRight
    ) {
        this.allowed_up = up;
        this.allowed_down = down;
        this.allowed_left = left;
        this.allowed_right = right;
        this.allowed_up_left = upLeft;
        this.allowed_up_right = upRight;
        this.allowed_down_left = downLeft;
        this.allowed_down_right = downRight;
    }
    public void render(SpriteBatch spriteBatch){
        spriteBatch.draw(_tex, _world_pos.x, _world_pos.y);
    }

    public static Tile GetFromId(String name){
        return TILES.get(name);
    }

    public static String GetIdFromInstance(Tile tile) {
        for (Map.Entry<String, Tile> entry : TILES.entrySet()) {
            if (entry.getValue().equals(tile)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static List<Tile> ImportFromModFile(String path){
        Path zip_path = Paths.get(path);
        String filename = zip_path.getFileName().toString();
        Path extract_dir = Paths.get("mods/extracted/" + Arrays.toString(new String[]{
            filename.substring(0, filename.lastIndexOf('.')),
            filename.substring(filename.lastIndexOf('.') + 1)
        }));


        return null;
    }

}
