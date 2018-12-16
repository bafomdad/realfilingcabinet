package com.bafomdad.realfilingcabinet;

import com.bafomdad.realfilingcabinet.init.client.RFCRenders;
import net.fabricmc.api.ClientModInitializer;

/**
 * Created by bafomdad on 12/11/2018.
 */
public class RealFilingCabinetClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        RFCRenders.init();
    }
}
