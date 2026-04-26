package com.example.calculatormod.client;

import com.example.calculatormod.CalculatorMod;
import com.example.calculatormod.screen.CalculatorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CalculatorModClient implements ClientModInitializer {

    public static KeyBinding openCalculatorKey;

    @Override
    public void onInitializeClient() {
        openCalculatorKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.calculatormod.open_calculator",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.calculatormod.calculator"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (client.currentScreen != null) return;
            if (InputUtil.isKeyPressed(
                    client.getWindow().getHandle(),
                    GLFW.GLFW_KEY_C)) {
                // Cキーが押されたらチャット入力中でなければ開く
            }
            while (openCalculatorKey.wasPressed()) {
                client.setScreen(new CalculatorScreen());
            }
        });

        CalculatorMod.LOGGER.info("Calculator Mod initialized!");
    }
}
