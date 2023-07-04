package de.damcraft.serverseeker.mixin;

import de.damcraft.serverseeker.gui.GetInfoScreen;
import de.damcraft.serverseeker.gui.ServerSeekerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {
    @Shadow
    protected MultiplayerServerListWidget serverListWidget;
    private ButtonWidget getInfoButton;

    protected MultiplayerScreenMixin() {
        super(null);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerScreen;updateButtonActivationStates()V"))
    private void onInit(CallbackInfo info) {
        // Add a button which sets the current screen to the ServerSeekerScreen
        this.addDrawableChild(
            new ButtonWidget.Builder(
                Text.literal("ServerSeeker"),
                onPress -> {
                    if (this.client == null) return;
                    this.client.setScreen(new ServerSeekerScreen((MultiplayerScreen) (Object) this));
                }
            )
                .position(150, 3)
                .width(80)
                .build()
        );

        // Add a button to get the info of the selected server
        this.getInfoButton = this.addDrawableChild(
            new ButtonWidget.Builder(
                Text.literal("Get players"),
                onPress -> {
                    if (this.client == null) return;
                    MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
                    if (entry != null) {
                        if (this.client == null) return;
                        this.client.setScreen(new GetInfoScreen((MultiplayerScreen) (Object) this, entry));
                    }
                }
            )
                .position(150 + 80 + 5, 3)
                .width(80)
                .build()
        );
    }

    @Inject(method = "updateButtonActivationStates", at = @At("TAIL"))
    private void onUpdateButtonActivationStates(CallbackInfo info) {
        // Enable the button if a server is selected
        MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
        this.getInfoButton.active = entry != null && !(entry instanceof MultiplayerServerListWidget.ScanningEntry);
    }
}
