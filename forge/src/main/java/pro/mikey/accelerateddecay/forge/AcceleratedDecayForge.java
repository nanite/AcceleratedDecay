package pro.mikey.accelerateddecay.forge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pro.mikey.accelerateddecay.AcceleratedDecay;
@Mod(AcceleratedDecay.MOD_ID)
public class AcceleratedDecayForge {
    public AcceleratedDecayForge() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    void onBreakEvent(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer) {
            AcceleratedDecay.breakEvent((Level) event.getWorld(), event.getState(), event.getPos(), (ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    void onServerLevelTick(TickEvent.WorldTickEvent event) {
        if (event.world instanceof ServerLevel && event.phase == TickEvent.Phase.END) {
            AcceleratedDecay.levelTickPostEvent((ServerLevel) event.world);
        }
    }
}
