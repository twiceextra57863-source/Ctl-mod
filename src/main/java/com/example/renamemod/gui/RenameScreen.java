package com.example.renamemod.gui;

import com.example.renamemod.network.RenamePacketHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TextInputScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.TextLayerRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class RenameScreen extends Screen {

    private final ItemStack originalItem;   // wo item jo rename ho raha hai
    private TextFieldWidget textField;       // input box jahan user type karega
    private String errorMessage = null;      // agar koi error ho toh ye dikhega

    public RenameScreen(ItemStack item) {
        super(Text.translatable("renamemod.gui.title"));
        this.originalItem = item;
    }

    /**
     * Screen init — widgets (buttons, text fields) yahan banate hain
     */
    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // ─── Text Field (input box) ───────────────────────────────
        // Yahan user naya naam type karega
        this.textField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,          // x position
                centerY - 20,           // y position
                200,                     // width
                20,                      // height
                Text.translatable("renamemod.gui.new_name")
        );
        this.textField.setMaxLength(40);  // max 40 characters
        this.addDrawable(this.textField);
        this.addSelectable(this.textField);
        this.setFocusedElement(this.textField);  // automatically focus karo jab screen khole

        // ─── Rename Button ────────────────────────────────────────
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("renamemod.gui.rename_button"),
                (button) -> onRenameClicked()   // click pe ye method chalega
        ).dimensions(
                centerX - 60,       // x
                centerY + 20,       // y
                120,                // width
                20                  // height
        ).build());

        // ─── Cancel Button ────────────────────────────────────────
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("renamemod.gui.cancel_button"),
                (button) -> this.client.setScreen(null)  // screen band karo
        ).dimensions(
                centerX - 60,
                centerY + 50,
                120,
                20
        ).build());
    }

    /**
     * Har frame render hota hai — screen draw karte hain yahan
     */
    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        // Background blur/overlay
        this.renderWithZTranslation(context, () -> {
            super.render(context, mouseX, mouseY, delta);

            int centerX = this.width / 2;
            int centerY = this.height / 2;

            // ─── Title ──────────────────────────────────────────
            context.drawCenteredText(
                    this.textRenderer,
                    Text.translatable("renamemod.gui.title"),
                    centerX,
                    centerY - 60,
                    0xFFFFFF   // white color
            );

            // ─── "Current Name:" label ──────────────────────────
            context.drawCenteredText(
                    this.textRenderer,
                    Text.translatable("renamemod.gui.current_name")
                            .append(" ")
                            .append(originalItem.getDisplayName()),
                    centerX,
                    centerY - 45,
                    0xAAAAAA   // grey
            );

            // ─── "New Name:" label ──────────────────────────────
            context.drawCenteredText(
                    this.textRenderer,
                    Text.translatable("renamemod.gui.new_name"),
                    centerX,
                    centerY - 5,
                    0xFFFFFF
            );

            // ─── Hint text ──────────────────────────────────────
            context.drawCenteredText(
                    this.textRenderer,
                    Text.translatable("renamemod.gui.hint"),
                    centerX,
                    centerY + 45,
                    0x888888   // dark grey
            );

            // ─── Error message (agar hai) ───────────────────────
            if (errorMessage != null) {
                context.drawCenteredText(
                        this.textRenderer,
                        Text.literal(errorMessage),
                        centerX,
                        centerY + 65,
                        0xFF4444   // red
                );
            }

            // ─── Item preview (small) ───────────────────────────
            // Original item ka icon dikhao left side pe
            context.drawItem(originalItem, centerX - 115, centerY - 25);
        });
    }

    /**
     * Rename button click hone pe ye chalega
     */
    private void onRenameClicked() {
        String input = this.textField.getText().trim();

        // ─── Validation ───────────────────────────────────────────
        if (input.isEmpty()) {
            errorMessage = "Name cannot be empty!";
            return;
        }

        // Sirf lowercase letters, numbers, underscores allowed
        if (!input.matches("^[a-z0-9_]+$")) {
            errorMessage = "Invalid! Only lowercase, numbers & underscores allowed.";
            return;
        }

        // ─── Packet bhejdo server pe ──────────────────────────────
        RenamePacketHandler.sendRenameRequest(input);

        // ─── Screen band karo ─────────────────────────────────────
        this.client.setScreen(null);

        // ─── Player ko message dikhao ─────────────────────────────
        this.client.player.sendMessage(
                Text.translatable("renamemod.chat.renamed", input),
                true  // actionbar
        );
    }

    /**
     * Agar player Escape dabega toh screen band ho jaayegi
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Ye screen pause screen nahi hai — game pause nahi hoga
     */
    @Override
    public boolean doesNotPauseGame() {
        return true;
    }
}
