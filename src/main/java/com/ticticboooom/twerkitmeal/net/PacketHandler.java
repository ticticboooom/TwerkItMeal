package com.ticticboooom.twerkitmeal.net;

import com.ticticboooom.twerkitmeal.TwerkItMeal;
import com.ticticboooom.twerkitmeal.net.packet.BonemealPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(TwerkItMeal.MOD_ID, "main_channel"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void register() {
        int disc = 0;

        HANDLER.registerMessage(disc++, BonemealPacket.class, BonemealPacket::encode, BonemealPacket::decode, BonemealPacket::handle);

    }
    public static void sendToServer(BonemealPacket pkt) {
        HANDLER.sendToServer(pkt);
    }
    public static void sendTo(BonemealPacket pkt, ServerPlayerEntity player) {
        HANDLER.sendTo(pkt, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }
}
