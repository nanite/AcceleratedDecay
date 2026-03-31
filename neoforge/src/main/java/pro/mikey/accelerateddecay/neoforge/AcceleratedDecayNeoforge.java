package pro.mikey.accelerateddecay.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import pro.mikey.accelerateddecay.AcceleratedDecay;

@Mod(AcceleratedDecay.MOD_ID)
public class AcceleratedDecayNeoforge {
    public AcceleratedDecayNeoforge() {
        AcceleratedDecay.init();
        NeoForge.EVENT_BUS.addListener(this::serverTick);
        NeoForge.EVENT_BUS.addListener(this::blockBreak);
    }

    private void blockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        AcceleratedDecay.breakHandler(serverLevel, event.getPos(), event.getState(), (ServerPlayer) event.getPlayer());
    }

    private void serverTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        AcceleratedDecay.levelTick(serverLevel, this::fireBlockBreakEvent);
    }

    private boolean fireBlockBreakEvent(ServerLevel serverLevel, BlockPos pos, BlockState blockState, ServerPlayer player) {
        BlockEvent.BreakEvent post = NeoForge.EVENT_BUS.post(new BlockEvent.BreakEvent(serverLevel, pos, blockState, player));
        return !post.isCanceled(); // True if the event was not canceled, meaning the block break should proceed
    }
}
