package com.example.calcul.plugins;

import com.example.calcul.plugin.Plugin;

public class SumPlugin implements Plugin {
    @Override
    public String getName() {
        return "Somme";
    }

    @Override
    public double calculate(double a, double b) {
        return a + b;
    }
}
