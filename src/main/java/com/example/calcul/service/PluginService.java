package com.example.calcul.service;


import com.example.calcul.plugin.Plugin;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class PluginService {




    private Map<String, Plugin> plugins = new HashMap<>();

    // Ajouter un plugin à la liste
    public void addPlugin(Plugin plugin) {
        plugins.put(plugin.getName(), plugin);
    }

    // Récupérer un plugin par son nom
    public Plugin getPlugin(String name) {
        return plugins.get(name); // Renvoie null si le plugin n'existe pas
    }

    // Supprimer un plugin par son nom
    public void removePlugin(String name) {
        plugins.remove(name);
    }

    // Retourne la liste des plugins
    public Map<String, Plugin> getPlugins() {
        return plugins;
    }
}
