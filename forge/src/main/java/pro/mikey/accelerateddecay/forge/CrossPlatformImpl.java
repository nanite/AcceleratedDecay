package pro.mikey.accelerateddecay.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public class CrossPlatformImpl {
    public static boolean emitBlockBrokeEvent(ServerLevel serverLevel, BlockPos pos, BlockState blockState, ServerPlayer player) {
        return !MinecraftForge.EVENT_BUS.post(new BlockEvent.BreakEvent(serverLevel, pos, blockState, player));
    }
}
