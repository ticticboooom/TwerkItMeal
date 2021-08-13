package com.ticticboooom.twerkitmeal.dynamictrees;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import java.util.Set;

public class DTProxy {
    public boolean blockAllowed(Block block) {
        return false;
    }
    
    public void grow(World world, BlockPos growablePos, Set<BlockPos> grown) {
    }
    
    
    private static DTProxy proxy;
    
    public static DTProxy getProxy() {
        if (proxy == null) {
            if (ModList.get().isLoaded("dynamictrees")) {
                proxy = new WithDTProxy();
            } else {
                proxy = new DTProxy();
            }
        }
        
        return proxy;
    }
}
