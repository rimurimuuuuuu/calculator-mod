package com.example.calculatormod.client;

import com.example.calculatormod.CalculatorMod;
import com.example.calculatormod.screen.CalculatorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CalculatorModClient implements ClientModInitializer {

    private boolean wasPressed = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (client.currentScreen != null) return;

            boolean isPressed = InputUtil.isKeyPressed(
                client.getWindow().getHandle(),
                GLFW.GLFW_KEY_C
            );

            if (isPressed && !wasPressed) {
                client.setScreen(new CalculatorScreen());
            }
            wasPressed = isPressed;
        });

        CalculatorMod.LOGGER.info("Calculator Mod initialized!");
    }
}
