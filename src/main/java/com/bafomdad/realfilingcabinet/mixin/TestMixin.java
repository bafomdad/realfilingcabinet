package com.bafomdad.realfilingcabinet.mixin;

import com.bafomdad.realfilingcabinet.RealFilingCabinet;
import com.bafomdad.realfilingcabinet.api.IModAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Created by bafomdad on 12/13/2018.
 */
@Mixin(RealFilingCabinet.class)
public class TestMixin {

    @Shadow
    public static List<IModAddon> addons;

    @Inject(at = @At("RETURN"), method = "setAddons")
    public void setAddons(CallbackInfo ci) {

        if (addons != null)
            addons.add(new TestAddon());
    }

    private class TestAddon implements IModAddon {

        @Override
        public void register() {

            System.out.println("it is I who added the nuts to this code!");
        }
    }
}
