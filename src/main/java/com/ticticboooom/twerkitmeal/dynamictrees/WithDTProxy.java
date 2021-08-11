package com.ticticboooom.twerkitmeal.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public class WithDTProxy extends DTProxy {
    @Override
    public boolean blockAllowed(Block block) {
        return block instanceof ITreePart;
    }
    
    @Override
    public void grow(World world, BlockPos growablePos, Set<BlockPos> grown) {
        BlockPos root = TreeHelper.findRootNode(world, growablePos);
        
        if (grown.add(root)) {
            TreeHelper.growPulse(world, root);
        }
    }
}
