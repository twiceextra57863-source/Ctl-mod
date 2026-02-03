package com.example.renamemod;

import com.example.renamemod.gui.RenameScreen;
import com.example.renamemod.network.RenamePacketHandler;
import com.example.renamemod.renderer.CustomItemRenderer;
import net.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabric.api.client.input.keys.KeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class RenameModClient implements net.fabric.api.mod.Mod {

    // ─── Keybind: default "R" key ─────────────────────────────────
    public static KeyBinding RENAME_KEY = new KeyBinding(
            "key.renamemod.rename",       // translation key
            GLFW.GLFWKEY_R,              // default key
            "renamemod"                   // category
    );

    @Override
    public void onInitialize() {
        System.out.println("[RenameMod Client] Client mod loaded!");

        // ── 1. Keybind register ───────────────────────────────────
        KeyBindings.registerKeyBinding(RENAME_KEY);

        // ── 2. Network packet handler register (client side) ──────
        RenamePacketHandler.registerClientHandler();

        // ── 3. Custom Item Renderer register ──────────────────────
        // Ye renderer har frame mein run hota hai aur check karta hai
        // ki kisi item pe custom resource name hai ya nahi
        CustomItemRenderer.register();

        // ── 4. Every tick check karo ki R key pressed hai ya nahi ──
        ClientTickEvents.END_CLIENT_TICK.register((MinecraftClient client) -> {
            // justPressed() sirf ek baar true hota hai jab key dabba hota hai
            while (RENAME_KEY.consumePress()) {
                // Player ke hand mein item hai?
                var heldItem = client.player != null ? client.player.getMainHandStack() : null;

                if (heldItem != null && !heldItem.isEmpty()) {
                    // Rename GUI kholio
                    client.setScreen(new RenameScreen(heldItem));
                } else {
                    // No item in hand — message dikhao
                    client.player.sendMessage(
                            net.minecraft.text.Text.translatable("renamemod.gui.error_no_item"),
                            true // true = actionbar mein dikhega (top pe nahi)
                    );
                }
            }
        });
    }
}
