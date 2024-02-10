package com.nottoomanyitems.stepup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Main implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        StepChanger stepChanger = new StepChanger();
        stepChanger.setKeyBindings();
        ClientTickEvents.END_CLIENT_TICK.register(stepChanger);
    }
}
