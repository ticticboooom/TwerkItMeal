package com.ticticboooom.twerkitmeal.net.packet;

import com.ticticboooom.twerkitmeal.config.TwerkConfig;
import com.ticticboooom.twerkitmeal.helper.FilterListHelper;
import com.ticticboooom.twerkitmeal.net.PacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BonemealPacket {
    private CompoundNBT tag;

    public BonemealPacket(CompoundNBT tag) {
        this.tag = tag;
    }

    public static void encode(BonemealPacket pkt, PacketBuffer buf) {
        buf.writeCompoundTag(pkt.tag);
    }

    public static BonemealPacket decode(PacketBuffer buf) {
        return new BonemealPacket(buf.readCompoundTag());
    }

    public static void handle(final BonemealPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            CompoundNBT nbt = pkt.tag;
            int x = nbt.getInt("x");
            int y = nbt.getInt("y");
            int z = nbt.getInt("z");
            BlockPos pos = new BlockPos(x, y, z);
            if (ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT) && TwerkConfig.showParticles) {
                BoneMealItem.spawnBonemealParticles(Minecraft.getInstance().world, pos, 5);
            } else if (ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
                BlockState blockstate = ctx.get().getSender().world.getBlockState(pos);
                if (!FilterListHelper.shouldAllow(blockstate.getBlock().getRegistryName().toString())) {
                    return;
                }
                if (blockstate.hasProperty(CropsBlock.AGE)) {
                    Integer growth = blockstate.get(CropsBlock.AGE);
                    ctx.get().getSender().world.setBlockState(pos, blockstate.with(CropsBlock.AGE, growth < 7 ? growth + 1 : 7));
                } else if (blockstate.getBlock() instanceof IGrowable) {
                    BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), ctx.get().getSender().world, pos, ctx.get().getSender());
                }
                for (PlayerEntity player : ctx.get().getSender().world.getPlayers()) {
                    PacketHandler.sendTo(pkt, (ServerPlayerEntity) player);
                }
            }

        });
    }
}
