package de.damcraft.serverseeker.mixin;

import de.damcraft.serverseeker.gui.InstallMeteorScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(at = @At("HEAD"), method = "init()V", cancellable = true)
    private void init(CallbackInfo info) {
        // Check if meteor-client is installed
        if (!FabricLoader.getInstance().isModLoaded("meteor-client")) {
            info.cancel();
            MinecraftClient.getInstance().setScreen(new InstallMeteorScreen());
        }
    }
}
