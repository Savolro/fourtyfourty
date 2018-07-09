package com.fourtyfourty.mod;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


import static com.fourtyfourty.mod.Reference.Reference.MODID;

@Mod.EventBusSubscriber(modid = MODID)
@Config(modid = MODID)
public class ModConfig {
    @Config.Comment("")
    public static float pullUpAngle = -40f;

    @Config.Comment("")
    public static float pullDownAngle = 40f;

    @Config.Comment("")
    public static float pullUpMinVelocity = 1.5f;

    @Config.Comment("")
    public static float pullDownMaxVelocity =  2.5f;

    @Config.Comment("")
    public static float pullUpSpeed = 3.5f;

    @Config.Comment("")
    public static float pullDownSpeed = 3f;

    @Config.Comment("")
    public static KeyBinding kb;

    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(MODID, Config.Type.INSTANCE);
        }
    }
}
