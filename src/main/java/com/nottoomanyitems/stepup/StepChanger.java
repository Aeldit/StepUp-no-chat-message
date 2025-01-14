package com.nottoomanyitems.stepup;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;

public final class StepChanger implements EndTick
{
    public KeyBinding myKey;
    public static int autoJumpState = 0; // 0 StepUp, 1 None
    public static boolean firstRun = true;
    public static String serverName;

    private MinecraftClient mc;

    public void setKeyBindings()
    {
        KeyBindingHelper.registerKeyBinding(myKey = new KeyBinding("key.stepup.toggle",
                InputUtil.Type.KEYSYM, GLFW_KEY_Y, "key.categories.stepup")
        );
    }

    @Override
    public void onEndTick(@NotNull MinecraftClient client)
    {
        ClientPlayerEntity player;
        mc = client;      // can't do this in onInit because mixins need to be applied first
        player = client.player;
        if (player == null)
        {
            return;
        }
        processKeyBinds();
        if (player.isSneaking())
        {
            player.setStepHeight(.6f);
        }
        else if (autoJumpState == 0 && player.getStepHeight() < 1.0f)
        {
            player.setStepHeight(1.25f);
        }
        else if (autoJumpState == 1 && player.getStepHeight() >= 1.0f)
        {
            player.setStepHeight(.6f);
        }
        autoJump();

        if (firstRun && autoJumpState != -1)
        {
            message();
            firstRun = false;
        }
    }

    public void processKeyBinds()
    {
        if (myKey.wasPressed())
        {
            autoJumpState = (autoJumpState + 1) % 2;
            message();
        }
    }

    private void autoJump()
    {
        boolean b = mc.options.getAutoJump().getValue();
        if (autoJumpState < 2 && b)
        {
            mc.options.getAutoJump().setValue(false);
        }
    }

    private void message()
    {
        String m = Formatting.DARK_AQUA + "[" + Formatting.YELLOW + "StepUp" + Formatting.DARK_AQUA + "]" + " ";
        if (autoJumpState == 0)
        {
            m = m + Formatting.GREEN + I18n.translate("mod.stepup.enabled");
        }
        else if (autoJumpState == 1)
        {
            m = m + Formatting.RED + I18n.translate("mod.stepup.disabled");
        }
        if (mc.player != null)
        {
            mc.player.sendMessage(Text.of(m), true);
        }
    }
}