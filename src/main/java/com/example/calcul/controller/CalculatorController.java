package com.example.calcul.controller;

import com.example.calcul.service.PluginService;
import com.example.calcul.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/calculator")
public class CalculatorController {

    @Autowired
    private PluginService pluginService;

    @GetMapping("/")
    public String getCalculator(Model model) {
        model.addAttribute("plugins", pluginService.getPlugins());
        return "calculator";
    }

    @PostMapping("/calculate")
    @ResponseBody
    public ResponseEntity<?> calculate(@RequestParam("number1") double number1,
                                       @RequestParam("number2") double number2,
                                       @RequestParam("operation") String operation) {
        try {
            double result = 0;

            // Vérifie si l'opération est basique (+, -, *, /)
            switch (operation) {
                case "+":
                    result = number1 + number2;
                    break;
                case "-":
                    result = number1 - number2;
                    break;
                case "*":
                    result = number1 * number2;
                    break;
                case "/":
                    if (number2 == 0) throw new ArithmeticException("Division par zéro !");
                    result = number1 / number2;
                    break;
                default:
                    throw new IllegalArgumentException("Opération ou plugin non supporté !");
            }

            // Renvoyer le résultat sous forme de JSON
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            // Renvoyer l'erreur sous forme de JSON
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/remove-plugin")
    public String removePlugin(@RequestParam("name") String name, Model model) {
        try {
            pluginService.removePlugin(name);
            model.addAttribute("message", "Plugin supprimé avec succès !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la suppression du plugin : " + e.getMessage());
        }
        model.addAttribute("plugins", pluginService.getPlugins());
        return "calculator";
    }
}
