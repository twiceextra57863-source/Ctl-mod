package com.example.renamemod.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.resource.manager.ResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * CustomItemRenderer
 *
 * Ye class responsible hai custom renamed items ko render karne ke liye.
 * Ye Fabric ke item rendering event hook use karti hai taaki
 * jab bhi koi item render ho, hum check kar sakein ki
 * uske NBT mein "renamemod_resource" tag hai ya nahi.
 *
 * Agar hai, toh hum Resource Pack se woh model/texture load karte hain.
 * Agar nahi hai, toh original item ka model/texture dikhta hai (fallback).
 */
public class CustomItemRenderer {

    /**
     * Register karta hai — client init pe ek baar call hota hai
     */
    public static void register() {
        System.out.println("[RenameMod] CustomItemRenderer registered.");
        // Note: Actual rendering interception Mixin se hoga — dekho ItemRendererMixin.java
        // Ye class utility methods provide karti hai jo Mixin use karega
    }

    /**
     * Ek ItemStack ke liye custom resource name check karta hai.
     *
     * @param stack — wo ItemStack jisko hum check kar rahe hain
     * @return String (resource name) agar custom tag hai, null agar nahi hai
     */
    public static String getCustomResourceName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        // Item ke CustomData (NBT) se "renamemod_resource" tag read karo
        var customData = stack.get(net.minecraft.component.DataComponentTypes.CUSTOM_DATA);
        if (customData == null) return null;

        var nbt = customData.getNbt();
        if (nbt == null || !nbt.containsKey("renamemod_resource")) return null;

        String resourceName = nbt.getString("renamemod_resource");

        // Validate — sirf valid resource location characters
        if (resourceName == null || resourceName.isEmpty()) return null;
        if (!resourceName.matches("^[a-z0-9_]+$")) return null;

        return resourceName;
    }

    /**
     * Resource Pack mein wo model hai ya nahi check karta hai.
     *
     * @param resourceName — jaise "fire_sword"
     * @return true agar resource pack mein model/texture mila, false agar nahi
     */
    public static boolean resourceExists(String resourceName) {
        if (resourceName == null) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        ResourceManager resourceManager = client.getResourceManager();

        // Model check karo: assets/minecraft/models/item/{name}.json
        Identifier modelId = Identifier.of("minecraft", "item/" + resourceName);

        // Texture check karo: assets/minecraft/textures/item/{name}.png
        Identifier textureId = Identifier.of("minecraft", "textures/item/" + resourceName + ".png");

        // Dono check karo — agar koi bhi hai toh true
        boolean modelExists = resourceManager.hasResource(
                new net.minecraft.resource.ResourceReference(
                        net.minecraft.resource.ResourceType.CLIENT_RESOURCES,
                        modelId.withPath(modelId.getPath() + ".json")
                )
        );

        boolean textureExists = resourceManager.hasResource(
                new net.minecraft.resource.ResourceReference(
                        net.minecraft.resource.ResourceType.CLIENT_RESOURCES,
                        textureId
                )
        );

        return modelExists || textureExists;
    }

    /**
     * Custom resource ka BakedModel load karta hai.
     * Ye ModelManager se directly load karta hai.
     *
     * @param resourceName — jaise "fire_sword"
     * @return BakedModel agar mila, null agar nahi mila (fallback case)
     */
    public static BakedModel getCustomModel(String resourceName) {
        if (resourceName == null) return null;

        MinecraftClient client = MinecraftClient.getInstance();

        // Model identifier banao: minecraft:item/fire_sword
        Identifier modelId = Identifier.of("minecraft", "item/" + resourceName);

        // ModelManager se BakedModel lo
        BakedModel model = client.getModelLoader().getModel(modelId);

        // Agar model nahi mila toh null return karo (fallback haoga)
        if (model == null || model == client.getModelLoader().getModel(new Identifier("minecraft", "missing"))) {
            System.out.println("[RenameMod] Model not found for: " + resourceName + " — using fallback.");
            return null;
        }

        return model;
    }
}
