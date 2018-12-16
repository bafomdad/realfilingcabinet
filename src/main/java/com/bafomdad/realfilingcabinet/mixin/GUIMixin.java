package com.bafomdad.realfilingcabinet.mixin;

import com.bafomdad.realfilingcabinet.gui.HudRFC;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by bafomdad on 12/15/2018.
 */

@Mixin(InGameHud.class)
public abstract class GUIMixin {

    private HudRFC hud;

    @Inject(at = @At(value = "RETURN"), method = "draw(F)V")
    private void drawFolderList(CallbackInfo ci) {

        if (hud == null)
            hud = new HudRFC();

        hud.draw();
    }
}
