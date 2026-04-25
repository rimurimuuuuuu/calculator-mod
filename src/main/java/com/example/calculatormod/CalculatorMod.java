package com.example.calculatormod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatorMod implements ModInitializer {
    public static final String MOD_ID = "calculatormod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Calculator Mod initialized!");
    }
}
