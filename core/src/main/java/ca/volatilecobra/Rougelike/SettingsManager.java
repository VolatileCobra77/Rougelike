package ca.volatilecobra;

import com.badlogic.gdx.Input.Keys;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsManager {

    private static final String FILE_PATH = "settings.json";
    private static SettingsManager instance;

    public WindowSettings window = new WindowSettings();
    public ControlSettings controls = new ControlSettings();

    public static class WindowSettings {
        public int width = 800;
        public int height = 600;
        public boolean vsync = false;
        public int max_fps = 60;
        public boolean fullscreen = false;
    }

    public static class ControlSettings {
        public List<Integer> forward = Arrays.asList(Keys.W, Keys.UP);
        public List<Integer> backward = Arrays.asList(Keys.S, Keys.DOWN);
        public List<Integer> left = Arrays.asList(Keys.A, Keys.LEFT);
        public List<Integer> right = Arrays.asList(Keys.D, Keys.RIGHT);
    }

    private SettingsManager() {}

    public static SettingsManager get() {
        if (instance == null) {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                instance = new SettingsManager(); // Use default values
                try {
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, instance);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create default settings file", e);
                }
            } else {
                try {
                    instance = mapper.readValue(file, SettingsManager.class);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load settings", e);
                }
            }
        }
        return instance;
    }

    public void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save settings", e);
        }
    }
}
