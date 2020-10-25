package com.ticticboooom.twerkitmeal.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommonConfig {
    public final ForgeConfigSpec.BooleanValue showParticles;
    public final ForgeConfigSpec.BooleanValue useWhitelist;
    public final ForgeConfigSpec.ConfigValue<ArrayList<String>> blackList;
    public final ForgeConfigSpec.ConfigValue<ArrayList<String>> whitelist;

    public CommonConfig(ForgeConfigSpec.Builder builder) {
        showParticles = builder.comment("Whether to show particles or not when crouching to grow things")
                .define("showParticles", true);
        useWhitelist = builder.comment("whether to enable the whitelist (overrides all default usages)")
                .define("useWhiteList", false);
        blackList = builder.comment("growables to disable crouching on")
                .define("blacklist",new ArrayList<>());
        whitelist = builder.comment("growables to enable crouching on (only works if 'useWhiteList' is true and will give exclisivity to those growables) ")
                .define("whitelist", new ArrayList<>());
    }
}
