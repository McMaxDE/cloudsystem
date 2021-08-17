package de.polocloud.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.updater.UpdateClient;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class Launcher {

    public static String PREFIX = "PoloCloud » ";

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println(PREFIX + "Please specify what you want to start. (Master, Wrapper)");
            return;
        }

        if (!(args[0].equalsIgnoreCase("Master") || args[0].equalsIgnoreCase("Wrapper"))) {
            System.out.println(PREFIX + args[0] + " is wrong argument!");
            return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File configFile = new File("launcher.json");
        Map<String, Object> configObject = null;
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                configObject = new HashMap<>();
                configObject.put("version", "0.Alpha");
                configObject.put("forceUpdate", true);

                FileWriter writer = new FileWriter(configFile);
                gson.toJson(configObject, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String currentVersion = "-1";
        boolean forceUpdate = true;
        try {
            FileReader reader = new FileReader(configFile);
            configObject = gson.fromJson(reader, HashMap.class);
            currentVersion = (String) configObject.get("version");
            forceUpdate = (boolean) configObject.get("forceUpdate");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        File bootstrapFile = new File("bootstrap.jar");

        if (!bootstrapFile.exists()) {
            forceUpdate = true;
        }

        String baseUrl = "http://37.114.60.129:8870";
        String downloadUrl = baseUrl + "/updater/download/bootstrap";
        String versionUrl = baseUrl + "/updater/version/bootstrap";
        UpdateClient updateClient = new UpdateClient(downloadUrl, bootstrapFile, versionUrl, currentVersion);

        System.out.println(PREFIX + "#Checking for bootstrap updates... (" + updateClient.getClientVersion() + ")");
        if (updateClient.download(forceUpdate)) {
            configObject.remove("version");
            configObject.put("version", updateClient.getFetchedVersion());

            try {
                FileWriter writer = new FileWriter(configFile);
                gson.toJson(configObject, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(PREFIX + "#updated");
        } else {
            System.out.println(PREFIX + "#no update found!");
        }

        //launch bootstrap.jar
        try (JarFile jarFile = new JarFile(bootstrapFile)) {

            URLClassLoader classLoader = new URLClassLoader(new URL[]{bootstrapFile.toURI().toURL()});
            Thread.currentThread().setContextClassLoader(classLoader);

            String mainClass = "de.polocloud.bootstrap.Bootstrap";
            Class<?> loadedClass = classLoader.loadClass(mainClass);
            Method mainMethod = loadedClass.getMethod("main", String[].class);

            final Object[] params = new Object[1];
            params[0] = args;

            mainMethod.invoke(null, params);

        } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
