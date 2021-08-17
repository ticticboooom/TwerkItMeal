package com.ticticboooom.twerkitmeal.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    public final ForgeConfigSpec.BooleanValue showParticles;
    public final ForgeConfigSpec.BooleanValue useWhitelist;
    public final ForgeConfigSpec.ConfigValue<List<String>> blackList;
    public final ForgeConfigSpec.ConfigValue<List<String>> whitelist;
    public final ForgeConfigSpec.IntValue minCrouchesToApplyBonemeal;
    public final ForgeConfigSpec.IntValue effectRadius;
    public final ForgeConfigSpec.BooleanValue saplingsOnly;
    public final ForgeConfigSpec.DoubleValue sprintGrowChance;
    public final ForgeConfigSpec.DoubleValue crouchGrowChance;

    public CommonConfig(ForgeConfigSpec.Builder builder) {
        List<String> defaultBlackList = new ArrayList<>();
        defaultBlackList.add("minecraft:netherrack");
        defaultBlackList.add("minecraft:grass_block");
        defaultBlackList.add("minecraft:warped_nylium");
        defaultBlackList.add("minecraft:crimson_nylium");
        defaultBlackList.add("minecraft:tall_grass");
        defaultBlackList.add("minecraft:grass");
        defaultBlackList.add("botanypots");
        defaultBlackList.add("gaiadimension");
        showParticles = builder.comment("Whether to show particles or not when crouching to grow things")
                .define("showParticles", true);
        useWhitelist = builder.comment("whether to enable the whitelist this does not disable the blacklist")
                .define("useWhiteList", false);
        blackList = builder.comment("growables to disable crouching on")
                .define("blacklist", defaultBlackList);
        whitelist = builder.comment("growables to enable crouching on (only works if 'useWhiteList' is true and will give exclisivity to those growables) ")
                .define("whitelist", new ArrayList<>());
        minCrouchesToApplyBonemeal = builder.comment("the minimum number of crouches before the bonemeal is applied (bonemeal is applied randomly so this will not be exact)")
                .defineInRange("minCrouchesToApplyBonemeal", 5, 0, Integer.MAX_VALUE);
        effectRadius = builder.comment("The radius of effect in blocks of applying the growth effect. Not recommended to change due to performance.")
                .defineInRange("effectRadius", 5, 0, 20);
        saplingsOnly = builder.comment("When true only saplings are allowed to grow with twerking")
                .define("saplingsOnly", false);
        sprintGrowChance = builder.comment("The chance of growth effect being applied from sprinting")
                .defineInRange("sprintGrowChance", 0.15, 0, 1);
        crouchGrowChance = builder.comment("The chance of growth effect being applied from any source")
                .defineInRange("crouchGrowChance", 0.5, 0, 1);
    }
}
