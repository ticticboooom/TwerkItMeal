package com.ticticboooom.twerkitmeal.config;

import com.ticticboooom.twerkitmeal.TwerkItMeal;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

public class TwerkConfig {
    public static boolean showParticles;
    public static boolean useWhitelist;
    public static List<String> blackList;
    public static List<String> whitelist;
    public static int minCrouchesToApplyBonemeal;

    public static void bake(ModConfig config) {
        showParticles = TwerkItMeal.COMMON_CONFIG.showParticles.get();
        useWhitelist = TwerkItMeal.COMMON_CONFIG.useWhitelist.get();
        blackList = TwerkItMeal.COMMON_CONFIG.blackList.get();
        whitelist = TwerkItMeal.COMMON_CONFIG.whitelist.get();
        minCrouchesToApplyBonemeal = TwerkItMeal.COMMON_CONFIG.minCrouchesToApplyBonemeal.get();
    }
}
