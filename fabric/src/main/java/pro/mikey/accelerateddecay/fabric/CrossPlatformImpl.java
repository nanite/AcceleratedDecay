package pro.mikey.accelerateddecay.fabric;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class CrossPlatformImpl {
    public static boolean emitBlockBrokeEvent(ServerLevel serverLevel, BlockPos pos, BlockState blockState, ServerPlayer player) {
        return PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(serverLevel, player, pos, blockState, serverLevel.getBlockEntity(pos));
    }
}
