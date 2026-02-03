package com.example.renamemod.mixin;

import com.example.renamemod.renderer.CustomItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.mixin.injection.Inject;
import org.spongepowered.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.mixin.Mixin;

/**
 * ItemRendererMixin
 *
 * Ye Minecraft ke ItemRenderer class ko mixin karti hai.
 * Jab bhi game kisi item ka model load karta hai rendering ke liye,
 * hum intercepted karte hain aur check karte hain ki
 * us item pe "renamemod_resource" custom tag hai ya nahi.
 *
 * Flow:
 * 1. Game "diamond_sword" ka model load karna chahta hai
 * 2. Hamara mixin intercept karta hai
 * 3. Hum item ke NBT mein dekh ke "fire_sword" milta hai
 * 4. Hum "fire_sword" ka model Resource Pack se load karte hain
 * 5. Agar mila → woh return karte hain (custom texture dikha)
 * 6. Agar nahi mila → original model return hota hai (fallback)
 *
 * Target: net.minecraft.client.render.item.ItemRenderer
 * Method: getModel(ItemStack, int, @Nullable LivingEntity)
 */
@Mixin(net.minecraft.client.render.item.ItemRenderer.class)
public class ItemRendererMixin {

    /**
     * @author renamemod
     * @reason Custom item model loading for renamed items
     *
     * Ye method tab call hota hai jab game kisi item ka BakedModel load karna chahta hai.
     * Hum yahan intercept karte hain aur custom model return karte hain agar available ho.
     */
    @Inject(
            method = "getModel(Lnet/minecraft/item/ItemStack;I Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/client/render/model/BakedModel;",
            at = @org.spongepowered.mixin.injection.At("HEAD"),
            cancellable = true
    )
    private void onGetModel(
            ItemStack stack,
            int damage,
            net.minecraft.entity.LivingEntity entity,
            CallbackInfoReturnable<BakedModel> cir
    ) {
        // ─── Step 1: Check karo ki item pe custom resource name hai ──
        String customName = CustomItemRenderer.getCustomResourceName(stack);

        // Agar koi custom name nahi hai, toh normal flow continue karo
        if (customName == null) return;

        // ─── Step 2: Resource Pack mein woh model hai? ──────────────
        if (!CustomItemRenderer.resourceExists(customName)) {
            // Model nahi mila — fallback: original item ka model dikhega
            System.out.println("[RenameMod] No resource found for '" + customName
                    + "' — falling back to original.");
            return; // return na karo — normal flow chalega
        }

        // ─── Step 3: Custom model load karo ──────────────────────────
        BakedModel customModel = CustomItemRenderer.getCustomModel(customName);

        if (customModel != null) {
            // Custom model mila! Ye return karo — original model nahi chalaega
            cir.setReturnValue(customModel);
            System.out.println("[RenameMod] Rendering custom model: " + customName);
        }
        // Agar customModel null hai toh normal flow chalega (fallback)
    }
}
