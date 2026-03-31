package pro.mikey.accelerateddecay.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import pro.mikey.accelerateddecay.AcceleratedDecay;

public class AcceleratedDecayFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlayerBlockBreakEvents.AFTER.register(((level, player, pos, state, blockEntity) -> {
            if (level.isClientSide()) {
                return;
            }

            AcceleratedDecay.breakHandler(level, pos, state, (ServerPlayer) player);
        }));

        ServerTickEvents.END_LEVEL_TICK.register((level) -> {
            AcceleratedDecay.levelTick(level, this::fireBlockBreakEvent);
        });
    }

    private boolean fireBlockBreakEvent(ServerLevel level, BlockPos pos, BlockState blockState, ServerPlayer player) {
        return PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(level, player, pos, blockState, level.getBlockEntity(pos));
    }
}
