package com.example.calcul.controller;
import com.example.calcul.plugin.Plugin;
//import org.sid.calculatorplugins.request.PluginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.multipart.MultipartFile;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/plugins")
public class PluginControllerr {

    private final String PLUGIN_DIR = "src/main/resources/uploads/";

    // Endpoint : Upload et compiler un fichier Java plugin
    @PostMapping("/upload")
    public ResponseEntity<String> uploadPlugin(@RequestParam("file") MultipartFile file) {
        try {
            // Créer le répertoire des plugins s'il n'existe pas
            File directory = new File(PLUGIN_DIR);
            if (!directory.exists()) directory.mkdirs();

            // Sauvegarder le fichier dans le répertoire cible
            Path targetPath = Paths.get(PLUGIN_DIR, file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
                fos.write(file.getBytes());
            }

            // Compiler le fichier Java
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int result = compiler.run(null, null, null, targetPath.toString());

            if (result == 0) {
                return ResponseEntity.ok("Plugin chargé et compilé avec succès : " + file.getOriginalFilename());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erreur de compilation pour le plugin : " + file.getOriginalFilename());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload ou de la compilation : " + e.getMessage());
        }
    }

    // Endpoint : Lister les plugins disponibles
    @GetMapping
    public ResponseEntity<List<String>> getAvailablePlugins() {
        List<String> pluginNames = new ArrayList<>();
        File pluginDir = new File(PLUGIN_DIR);

        if (pluginDir.exists() && pluginDir.isDirectory()) {
            File[] files = pluginDir.listFiles((dir, name) -> name.endsWith(".class"));
            if (files != null) {
                try {
                    URL[] urls = {pluginDir.toURI().toURL()};
                    URLClassLoader classLoader = new URLClassLoader(urls);

                    for (File file : files) {
                        String className = file.getName().replace(".class", "");
                        Class<?> clazz = classLoader.loadClass(className);
                        if (Plugin.class.isAssignableFrom(clazz)) {
                            Plugin plugin = (Plugin) clazz.getDeclaredConstructor().newInstance();
                            pluginNames.add(plugin.getName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseEntity.ok(pluginNames);
    }

    // Endpoint : Exécuter un plugin
    @PostMapping("/execute")
//    public ResponseEntity<?> executePlugin(@RequestBody PluginRequest request) {
//        try {
//            Plugin plugin = loadPluginByName(request.getName());
//            if (plugin != null) {
//                Double result = plugin.execute(request.getValue());
//                return ResponseEntity.ok(result);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body("Plugin non trouvé : " + request.getName());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Erreur lors de l'exécution du plugin : " + e.getMessage());
//        }
//    }

    // Méthode pour charger un plugin par son nom
    private Plugin loadPluginByName(String name) {
        try {
            File pluginDir = new File(PLUGIN_DIR);
            if (!pluginDir.exists() || !pluginDir.isDirectory()) return null;

            File pluginClassFile = new File(pluginDir, name + ".class");
            if (!pluginClassFile.exists()) return null;

            URL[] urls = {pluginDir.toURI().toURL()};
            URLClassLoader classLoader = new URLClassLoader(urls);

            Class<?> clazz = classLoader.loadClass(name);
            if (Plugin.class.isAssignableFrom(clazz)) {
                return (Plugin) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Endpoint : Supprimer un plugin
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deletePlugin(@PathVariable String name) {
        try {
            File pluginClassFile = new File(PLUGIN_DIR, name + ".class");
            if (pluginClassFile.exists()) {
                pluginClassFile.delete();
                return ResponseEntity.ok("Plugin supprimé : " + name);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Plugin non trouvé : " + name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du plugin : " + e.getMessage());
        }
    }
}
