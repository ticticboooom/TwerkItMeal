package com.ticticboooom.twerkitmeal;

import com.ticticboooom.twerkitmeal.config.CommonConfig;
import com.ticticboooom.twerkitmeal.config.TwerkConfig;
import com.ticticboooom.twerkitmeal.dynamictrees.DTProxy;
import com.ticticboooom.twerkitmeal.helper.FilterListHelper;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;


@Mod(TwerkItMeal.MOD_ID)
public class TwerkItMeal {
    public static final String MOD_ID = "twerkitmeal";

    static final ForgeConfigSpec commonSpec;
    public static final CommonConfig COMMON_CONFIG;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        commonSpec = specPair.getRight();
        COMMON_CONFIG = specPair.getLeft();
    }

    public TwerkItMeal() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec, "twerk-config.toml");
        MinecraftForge.EVENT_BUS.register(new RegistryEvents());
    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        private final Map<UUID, Integer> crouchCount = new HashMap<>();
        private final Map<UUID, Boolean> prevSneaking = new HashMap<>();
        private final Map<UUID, Integer> playerDistance = new HashMap<>();
        private final Map<UUID, Integer> playerHoldTimer = new HashMap<>();

        @SubscribeEvent
        public void onTwerk(TickEvent.PlayerTickEvent event) {
            if (event.player.world.isRemote) {
                return;
            }
            UUID uuid = PlayerEntity.getUUID(event.player.getGameProfile());
            if (!crouchCount.containsKey(uuid)){
                crouchCount.put(uuid, 0);
                prevSneaking.put(uuid, event.player.isSneaking());
                playerDistance.put(uuid, 0);
            }

            ServerWorld world = (ServerWorld) event.player.world;

            if (event.player.isSprinting() && world.getRandom().nextDouble() <= TwerkConfig.sprintGrowChance){
                triggerGrowth(event, uuid);
            }

            if (event.player.isSneaking()){
                triggerGrowth(event, uuid);
            }

            boolean wasPlayerSneaking = prevSneaking.get(uuid);
            int playerCrouchCount = crouchCount.get(uuid);
            if (!event.player.isSneaking()) {
                prevSneaking.put(uuid, false);
                return;
            }
            if (wasPlayerSneaking && event.player.isSneaking()) {
                return;
            } else if (!wasPlayerSneaking && event.player.isSneaking()) {
                prevSneaking.put(uuid, true);
                crouchCount.put(uuid, ++playerCrouchCount);
            }

            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            if (playerCrouchCount >= TwerkConfig.minCrouchesToApplyBonemeal && world.getRandom().nextDouble() <= TwerkConfig.crouchGrowChance) {
                triggerGrowth(event, uuid);
            }
        }

        private void triggerGrowth(TickEvent.PlayerTickEvent event, UUID uuid) {
            crouchCount.put(uuid, 0);
            List<BlockPos> growables = getNearestBlocks(event.player.world, event.player.getPosition());
            Set<BlockPos> grownDT = new HashSet<>();
            for (BlockPos growablePos : growables) {
                BlockState blockState = event.player.world.getBlockState(growablePos);
                if (!FilterListHelper.shouldAllow(blockState.getBlock().getRegistryName().toString())) {
                    continue;
                }

                if (TwerkConfig.saplingsOnly){
                    if (!BlockTags.SAPLINGS.contains(blockState.getBlock())) {
                        continue;
                    }
                }
                if (blockState.hasProperty(CropsBlock.AGE)) {
                    int growth = blockState.get(CropsBlock.AGE);
                    event.player.world.setBlockState(growablePos, blockState.with(CropsBlock.AGE, growth < 7 ? growth + 1 : 7));
                } else if (blockState.getBlock() instanceof IGrowable) {
                    BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), event.player.world, growablePos, event.player);
                } else if (DTProxy.getProxy().blockAllowed(blockState.getBlock())) {
                    DTProxy.getProxy().grow(event.player.world, growablePos, grownDT);
                }
                ((ServerWorld)event.player.world).spawnParticle((ServerPlayerEntity) event.player, ParticleTypes.HAPPY_VILLAGER, false, growablePos.getX() + event.player.world.rand.nextDouble(), growablePos.getY() + event.player.world.rand.nextDouble(), growablePos.getZ() + event.player.world.rand.nextDouble(), 10, 0, 0, 0, 3);
            }
        }

        private List<BlockPos> getNearestBlocks(World world, BlockPos pos) {
            List<BlockPos> list = new ArrayList<>();
            for (int x = -TwerkConfig.effectRadius; x <= TwerkConfig.effectRadius; x++)
                for (int y = -2; y <= 2; y++)
                    for (int z = -TwerkConfig.effectRadius; z <= TwerkConfig.effectRadius; z++) {
                        Block block = world.getBlockState(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ())).getBlock();
                        if (block instanceof IGrowable || DTProxy.getProxy().blockAllowed(block)) {
                            if (FilterListHelper.shouldAllow(block.getRegistryName().toString())) {
                                list.add(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ()));
                            }
                        }
                    }
            return list;
        }

        private CompoundNBT createCompoundTag(BlockPos pos) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());
            return nbt;
        }
    }
}
