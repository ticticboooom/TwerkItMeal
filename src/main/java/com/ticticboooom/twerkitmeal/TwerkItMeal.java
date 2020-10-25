package com.ticticboooom.twerkitmeal;

import com.ticticboooom.twerkitmeal.config.CommonConfig;
import com.ticticboooom.twerkitmeal.net.PacketHandler;
import com.ticticboooom.twerkitmeal.net.packet.BonemealPacket;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;


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
        PacketHandler.register();
        MinecraftForge.EVENT_BUS.register(new RegistryEvents());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
    }


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        public int ticksSinceLastCheck = 0;
        private boolean allowNextBonemeal = true;

        @SubscribeEvent
        public void onTwerk(TickEvent.PlayerTickEvent event) {
            if (!event.player.isSneaking()) {
                allowNextBonemeal = true;
                return;
            }
            if (ticksSinceLastCheck >= 10 && allowNextBonemeal) {
                List<BlockPos> saplings = getNearestBlocks(event.player.world, event.player.getPosition());
                for (BlockPos sapling : saplings) {
                    BlockPos pos = new BlockPos(sapling.getX(), sapling.getY(), sapling.getZ());


                    PacketHandler.sendToServer(new BonemealPacket(createCompoundTag(pos)));
                }
                ticksSinceLastCheck = 0;
                allowNextBonemeal = false;
            }
            ticksSinceLastCheck++;
        }

        private List<BlockPos> getNearestBlocks(World world, BlockPos pos) {
            List<BlockPos> list = new ArrayList<>();
            for (int x = -5; x <= 5; x++)
                for (int y = -2; y <= 2; y++)
                    for (int z = -5; z <= 5; z++) {
                        Block block = world.getBlockState(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ())).getBlock();
                        if (block instanceof IGrowable && !(block instanceof GrassBlock || block instanceof TallGrassBlock)) {
                            if (!COMMON_CONFIG.blackList.get().contains(block.getRegistryName())) {
                                if (COMMON_CONFIG.useWhitelist.get() && !COMMON_CONFIG.whitelist.get().contains(block.getRegistryName())) {
                                    continue;
                                }
                                if (block.getRegistryName().toString().contains("botanypots")) {
                                    continue;
                                }
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
