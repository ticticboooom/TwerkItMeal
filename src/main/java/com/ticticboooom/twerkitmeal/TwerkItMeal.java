package com.ticticboooom.twerkitmeal;

import com.ticticboooom.twerkitmeal.config.CommonConfig;
import com.ticticboooom.twerkitmeal.config.TwerkConfig;
import com.ticticboooom.twerkitmeal.helper.FilterListHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


public class TwerkItMeal implements ModInitializer {
    public static final String MOD_ID = "twerkitmeal";

    static final ForgeConfigSpec commonSpec;
    public static final CommonConfig COMMON_CONFIG;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        commonSpec = specPair.getRight();
        COMMON_CONFIG = specPair.getLeft();
    }

    @Override
    public void onInitialize() {
        ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.COMMON, commonSpec, "twerk-config.toml");
        ModConfigEvent.LOADING.register(TwerkConfig::bake);
        ModConfigEvent.RELOADING.register(TwerkConfig::bake);
        new RegistryEvents();
    }


    public static class RegistryEvents {

        private final Map<UUID, Integer> crouchCount = new HashMap<>();
        private final Map<UUID, Boolean> prevSneaking = new HashMap<>();
        private final Map<UUID, Integer> playerDistance = new HashMap<>();

        public RegistryEvents() {
            ServerTickEvents.START_WORLD_TICK.register(world -> PlayerLookup.world(world).forEach(this::onTwerk));
        }

        public void onTwerk(ServerPlayer player) {
            UUID uuid = player.getUUID();
            if (!crouchCount.containsKey(uuid)){
                crouchCount.put(uuid, 0);
                prevSneaking.put(uuid, player.isCrouching());
                playerDistance.put(uuid, 0);
            }

            ServerLevel world = (ServerLevel) player.level;

            if (player.isSprinting() && world.getRandom().nextDouble() <= TwerkConfig.sprintGrowChance){
                triggerGrowth(player, uuid);
            }

            boolean wasPlayerSneaking = prevSneaking.get(uuid);
            int playerCrouchCount = crouchCount.get(uuid);
            if (!player.isCrouching()) {
                prevSneaking.put(uuid, false);
                return;
            }
            if (wasPlayerSneaking && player.isCrouching()) {
                return;
            } else if (!wasPlayerSneaking && player.isCrouching()) {
                prevSneaking.put(uuid, true);
                crouchCount.put(uuid, ++playerCrouchCount);
            }

            if (playerCrouchCount >= TwerkConfig.minCrouchesToApplyBonemeal && world.random.nextDouble() <= TwerkConfig.crouchGrowChance) {
                triggerGrowth(player, uuid);
            }
        }

        private void triggerGrowth(ServerPlayer player, UUID uuid) {
            crouchCount.put(uuid, 0);
            List<BlockPos> growables = getNearestBlocks(player.level, player.blockPosition());
            Set<BlockPos> grownDT = new HashSet<>();
            for (BlockPos growablePos : growables) {
                BlockState blockState = player.level.getBlockState(growablePos);
                if (!FilterListHelper.shouldAllow(Registry.BLOCK.getKey(blockState.getBlock()).toString())) {
                    continue;
                }

                if (TwerkConfig.saplingsOnly){
                    if (!blockState.getBlock().builtInRegistryHolder().is(BlockTags.SAPLINGS)) {
                        continue;
                    }
                }
                if (blockState.hasProperty(CropBlock.AGE)) {
                    int growth = blockState.getValue(CropBlock.AGE);
                    player.level.setBlockAndUpdate(growablePos, blockState.setValue(CropBlock.AGE, growth < 7 ? growth + 1 : 7));
                } else if (blockState.getBlock() instanceof BonemealableBlock) {
                    BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), player.level, growablePos);
                }
                ((ServerLevel)player.level).sendParticles(player, ParticleTypes.HAPPY_VILLAGER, false, growablePos.getX() + player.level.random.nextDouble(), growablePos.getY() + player.level.random.nextDouble(), growablePos.getZ() + player.level.random.nextDouble(), 10, 0, 0, 0, 3);
            }
        }

        private List<BlockPos> getNearestBlocks(Level level, BlockPos pos) {
            List<BlockPos> list = new ArrayList<>();
            for (int x = -TwerkConfig.effectRadius; x <= TwerkConfig.effectRadius; x++)
                for (int y = -2; y <= 2; y++)
                    for (int z = -TwerkConfig.effectRadius; z <= TwerkConfig.effectRadius; z++) {
                        Block block = level.getBlockState(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ())).getBlock();
                        if (block instanceof BonemealableBlock) {
                            if (FilterListHelper.shouldAllow(Registry.BLOCK.getKey(block).toString())) {
                                list.add(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ()));
                            }
                        }
                    }
            return list;
        }

        private CompoundTag createCompoundTag(BlockPos pos) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());
            return nbt;
        }
    }
}
