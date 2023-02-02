package pro.mikey.accelerateddecay;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class CrossPlatform {
    @ExpectPlatform
    public static boolean emitBlockBrokeEvent(ServerLevel serverLevel, BlockPos pos, BlockState blockState, ServerPlayer player) {
        throw new AssertionError("emitBlockBrokeEvent expected platform implementation");
    }
}
