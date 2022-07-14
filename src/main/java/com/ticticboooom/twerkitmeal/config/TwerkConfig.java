package com.ticticboooom.twerkitmeal.config;

import com.ticticboooom.twerkitmeal.TwerkItMeal;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

public class TwerkConfig {
    public static boolean showParticles;
    public static boolean useWhitelist;
    public static List<String> blackList;
    public static List<String> whitelist;
    public static int minCrouchesToApplyBonemeal;
    public static int effectRadius;
    public static boolean saplingsOnly;
    public static int distanceSprintedToGrow;
    public static double sprintGrowChance;
    public static double crouchGrowChance;
    public static boolean growBabies;

    public static void bake(ModConfig config) {
        showParticles = TwerkItMeal.COMMON_CONFIG.showParticles.get();
        useWhitelist = TwerkItMeal.COMMON_CONFIG.useWhitelist.get();
        blackList = TwerkItMeal.COMMON_CONFIG.blackList.get();
        whitelist = TwerkItMeal.COMMON_CONFIG.whitelist.get();
        minCrouchesToApplyBonemeal = TwerkItMeal.COMMON_CONFIG.minCrouchesToApplyBonemeal.get();
        effectRadius = TwerkItMeal.COMMON_CONFIG.effectRadius.get();
        saplingsOnly = TwerkItMeal.COMMON_CONFIG.saplingsOnly.get();
        sprintGrowChance = TwerkItMeal.COMMON_CONFIG.sprintGrowChance.get();
        crouchGrowChance = TwerkItMeal.COMMON_CONFIG.crouchGrowChance.get();
        growBabies = TwerkItMeal.COMMON_CONFIG.growBabies.get();
    }
}
