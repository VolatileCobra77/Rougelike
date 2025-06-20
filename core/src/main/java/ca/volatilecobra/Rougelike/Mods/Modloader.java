package ca.volatilecobra.Rougelike.Mods;



import ca.volatilecobra.Rougelike.Utils.Ziputils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Modloader {

    public static Map<String, Mod> LOADED_MODS = new HashMap<String, Mod>();
    private static String lastPath = "";
    public static void SearchForMods(String path){
        File dir = new File(path);
        lastPath = path;
        File extracted_dir = new File(path +"/extracted");
        if (!extracted_dir.exists()){
            System.out.println("Extracted directory not found, creating directory...");
            if (extracted_dir.mkdirs()){
                System.out.println("Successfully created directory");

            }else{
                System.err.println("Failed to create extracted directory, mods will not be loaded");
                return;
            };
        }else{
            System.out.println("Found extracted directory");
        }
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files){
                    if (file.getName().contains(".zip") && !new File(extracted_dir, file.getName().contains(".") ? file.getName().substring(0, file.getName().lastIndexOf('.')) : file.getName()).isDirectory()){
                        try{
                            Ziputils.unzip(Path.of(file.getAbsolutePath()), Path.of(extracted_dir.getAbsolutePath()));
                            File extracted_mod_dir = new File(extracted_dir.getAbsolutePath() + file.getName());
                            try {
                                Mod mod = findSingleModClass(Path.of(extracted_mod_dir.getAbsolutePath()));
                                mod.directory = extracted_mod_dir.getAbsolutePath();
                                LOADED_MODS.put(mod.getName(), mod);
                            }catch (Exception e){
                                System.err.println("Error loading mod " + file.getName() + "skipping...");
                                System.err.println("Stack Trace:");
                                e.printStackTrace(System.err);
                            }
                        } catch (IOException e) {
                            System.err.println("Error unzipping mod " + file.getName() +", Skipping...");
                            System.err.println("Stack trace: ");
                            e.printStackTrace(System.err);
                        }
                    }
                }
            }
        }
    }

    public static void loadMods(){
        for (Mod mod : LOADED_MODS.values()){
            mod.onLoad();
        }
    }

    public static void RenderMods(SpriteBatch spriteBatch){
        for (Mod mod : LOADED_MODS.values()){
            mod.Render_objects(spriteBatch);
        }
    }

    public static void RenderMods(ShapeRenderer shapeRenderer){
        for (Mod mod : LOADED_MODS.values()){
            mod.Render_shapes(shapeRenderer);
        }
    }

    public static void UpdateMods(float Delta_time){
        for (Mod mod : LOADED_MODS.values()){
            mod.Update(Delta_time);
        }
    }

    public static Mod findSingleModClass(Path rootDir) throws Exception {
        List<Mod> modInstances = new ArrayList<>();
        URL[] urls = { rootDir.toUri().toURL() };

        try (URLClassLoader classLoader = new URLClassLoader(urls)) {
            try (Stream<Path> paths = Files.walk(rootDir)) {
                paths.filter(path -> path.toString().endsWith(".class")).forEach(classFile -> {
                    try {
                        String fqn = getFullyQualifiedName(rootDir, classFile);
                        Class<?> clazz = classLoader.loadClass(fqn);

                        // Skip abstract or interface types
                        if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) return;

                        // Ensure it's a subclass of Mod
                        if (Mod.class.isAssignableFrom(clazz)) {
                            Mod instance = (Mod) clazz.getDeclaredConstructor().newInstance();
                            modInstances.add(instance);
                        }

                    } catch (Exception e) {
                        throw new RuntimeException("Error loading mod from " + classFile, e);
                    }
                });
            }
        }

        if (modInstances.isEmpty()) {
            throw new RuntimeException("No Mod classes found.");
        } else if (modInstances.size() > 1) {
            throw new RuntimeException("Multiple Mod classes found: " + modInstances);
        }

        return modInstances.get(0);
    }

    private static String getFullyQualifiedName(Path rootDir, Path classFile) {
        Path relativePath = rootDir.relativize(classFile);
        String pathStr = relativePath.toString()
            .replace(File.separatorChar, '.')
            .replaceAll("\\.class$", "");
        return pathStr;
    }

    private static Class<?> loadClassFromDirectory(Path dir, String className) throws Exception {
        URL[] urls = { dir.toUri().toURL() };
        try (URLClassLoader loader = new URLClassLoader(urls)) {
            return loader.loadClass(className);
        }
    }

    private static Class<?> loadClassFromJar(Path jarPath, String className) throws Exception {
        URL[] urls = { jarPath.toUri().toURL() };
        try (URLClassLoader loader = new URLClassLoader(urls)) {
            return loader.loadClass(className);
        }
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    public static Class<?> getClassReference(String fullyQualifiedName) throws ClassNotFoundException {
        return Class.forName(fullyQualifiedName);
    }

    public static Object invokeIfExists(Class<?> clazz, String methodName, Object instance, Object... args) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == args.length) {
                try {
                    method.setAccessible(true);
                    return method.invoke(instance, args);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        System.err.println("Method not found: " + methodName + " in class " + clazz.getName());
        return null;
    }

    public static void LoadSingleMod(Mod instance) {
        LOADED_MODS.put(instance.getName(), instance);
    }

    public static String GetModsDir() {
        return lastPath;
    }
}
