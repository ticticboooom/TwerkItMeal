package com.ticticboooom.twerkitmeal.config;

import com.ticticboooom.twerkitmeal.TwerkItMeal;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = TwerkItMeal.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigSubscriber {
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        TwerkConfig.bake(event.getConfig());
    }
}
