package pro.mikey.accelerateddecay.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.level.ServerPlayer;
import pro.mikey.accelerateddecay.AcceleratedDecay;

public class AcceleratedDecayFabric {
    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(AcceleratedDecay::levelTickPostEvent);
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> AcceleratedDecay.breakEvent(world, state, pos, (ServerPlayer) player));
    }
}
