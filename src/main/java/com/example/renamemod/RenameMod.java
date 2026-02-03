package com.example.renamemod;

import net.fabric_api.discrete_event.DiscreteEvent;
import net.fabric.api.mod.Mod;
import net.minecraft.server.MinecraftServer;
import net.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import com.example.renamemod.network.RenamePacketHandler;

public class RenameMod implements net.fabric.api.mod.Mod {

    public static final String MOD_ID = "renamemod";

    @Override
    public void onInitialize() {
        System.out.println("[RenameMod] Mod loaded successfully!");

        // Server lifecycle event — server start hone pe network packets register karte hain
        ServerLifecycleEvents.SERVER_STARTING.register((MinecraftServer server) -> {
            System.out.println("[RenameMod] Server starting — registering network handlers...");
            RenamePacketHandler.registerServerHandler();
        });
    }
}
