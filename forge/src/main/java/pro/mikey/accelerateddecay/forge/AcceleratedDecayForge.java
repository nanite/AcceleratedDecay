package pro.mikey.accelerateddecay.forge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import pro.mikey.accelerateddecay.AcceleratedDecay;
import net.minecraftforge.fml.common.Mod;
@Mod(AcceleratedDecay.MOD_ID)
public class AcceleratedDecayForge {
    public AcceleratedDecayForge() {
        AcceleratedDecay.init();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    void onBreakEvent(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            AcceleratedDecay.breakEvent((Level) event.getWorld(), event.getState(), event.getPos(), player);
        }
    }

    @SubscribeEvent
    void onServerLevelTick(TickEvent.WorldTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            AcceleratedDecay.levelTickPostEvent((ServerLevel) event.world);
        }
    }
}
