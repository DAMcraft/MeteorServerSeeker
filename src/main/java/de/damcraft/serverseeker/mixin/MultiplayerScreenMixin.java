package de.damcraft.serverseeker.mixin;

import de.damcraft.serverseeker.gui.ServerSeekerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    protected MultiplayerScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        // Add a button which sets the current screen to the ServerSeekerScreen
        this.addDrawableChild(
            new ButtonWidget.Builder(
                Text.literal("ServerSeeker"),
                onPress -> {
                    if (this.client == null) return;
                    this.client.setScreen(new ServerSeekerScreen((MultiplayerScreen)(Object)this));
                }
            )
            .position(150, 3)
            .width(100)
            .build()
        );
    }

}
