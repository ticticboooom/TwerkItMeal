package com.ticticboooom.twerkitmeal;

import com.ticticboooom.twerkitmeal.net.PacketHandler;
import com.ticticboooom.twerkitmeal.net.packet.BonemealPacket;
import net.minecraft.block.*;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


@Mod(TwerkItMeal.MOD_ID)
public class TwerkItMeal {
    public static final String MOD_ID = "twerkitmeal";

    public TwerkItMeal() {
        PacketHandler.register();
        MinecraftForge.EVENT_BUS.register(new RegistryEvents());
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
                        if (block instanceof IGrowable && !(block instanceof GrassBlock || block instanceof TallGrassBlock))
                            list.add(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ()));
                    }
            return list;
        }
        private CompoundNBT createCompoundTag(BlockPos pos) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("x",  pos.getX());
            nbt.putInt("y",  pos.getY());
            nbt.putInt("z",  pos.getZ());
            return nbt;
        }
    }
}
