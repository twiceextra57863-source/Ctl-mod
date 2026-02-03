package com.example.renamemod.network;

import net.fabric.api.networking.v1.PacketByteBufs;
import net.fabric.api.networking.v1.client.ClientPlayNetworking;
import net.fabric.api.networking.v1.server.ServerPlayNetworking;
import net.minecraft.io.UnsuccessfulReadException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class RenamePacketHandler {

    // ─── Packet ID — unique identifier for our rename packet ─────
    // Format: mod_id:packet_name
    public static final Identifier RENAME_ITEM_PACKET = Identifier.of("renamemod", "rename_item");

    /**
     * Server side handler register karta hai.
     * Jab client se rename packet aayega, ye handle karega.
     */
    public static void registerServerHandler() {
        ServerPlayNetworking.registerReceiver(RENAME_ITEM_PACKET, (server, player, handler, buf, sender) -> {
            // Client se aaya data read karo
            String newName = buf.readString();

            // Server thread pe run karo (important for thread safety)
            server.execute(() -> {
                handleRenameOnServer(player, newName);
            });
        });
    }

    /**
     * Client side handler register karta hai.
     * Server se koi confirmation aayegi toh ye handle karega.
     * (Abhi directly use nahi ho raha but future mein confirmation loop ke liye)
     */
    public static void registerClientHandler() {
        // Future: server se success/failure confirmation receive karna
        System.out.println("[RenameMod] Client network handler registered.");
    }

    /**
     * Client se server pe rename request bhejta hai.
     * @param newName — wo name jo user ne type kiya hai GUI mein
     */
    public static void sendRenameRequest(String newName) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(newName);                          // name write karo buffer mein
        ClientPlayNetworking.sendToServer(RENAME_ITEM_PACKET, buf);  // server pe bhejo
    }

    /**
     * Server pe actual rename logic.
     * Player ke haath mein jo item hai, uska custom component update karta hai.
     */
    private static void handleRenameOnServer(ServerPlayerEntity player, String newName) {
        // Validate name — sirf lowercase letters, numbers, underscores allowed
        if (!newName.matches("^[a-z0-9_]+$")) {
            System.out.println("[RenameMod] Invalid rename request from " + player.getName() + ": " + newName);
            return;
        }

        // Player ke main hand se item lo
        ItemStack item = player.getMainHandStack();

        if (item.isEmpty()) {
            System.out.println("[RenameMod] Player " + player.getName() + " tried to rename empty hand.");
            return;
        }

        // ─── Item pe custom NBT data set karo ─────────────────────
        // Ye data har player ko same milega — server pe stored hai
        // Hum item ki display name change karenge aur ek custom tag lagayenge
        item.set(
                net.minecraft.component.DataComponentType.of(
                        net.minecraft.component.DataComponentTypes.CUSTOM_NAME
                ),
                // Display name set karna (chat mein dikhe wala naam)
                net.minecraft.text.Text.literal(newName)
        );

        // ─── Custom NBT tag store karo resource name ke liye ──────
        // Ye tag client side renderer use karega texture load karne ke liye
        // Hum item.getNbt() use karke ek custom tag "renamemod:resource_name" lagayenge
        item.getOrDefault(
                net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
                net.minecraft.item.common.CustomData.EMPTY
        );

        // Simpler approach: NBT compound mein directly write karo
        var nbt = item.getOrDefault(
                net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
                net.minecraft.item.common.CustomData.EMPTY
        ).copyNbt();

        nbt.putString("renamemod_resource", newName);  // ← ye tag client pe read karega

        item.put(
                net.minecraft.component.DataComponentTypes.CUSTOM_DATA,
                net.minecraft.item.common.CustomData.of(nbt)
        );

        // Item update karo player ke inventory mein
        player.getInventory().markDirty();

        System.out.println("[RenameMod] '" + player.getName() + "' renamed item to: " + newName);
    }
}
