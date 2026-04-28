package pro.mikey.accelerateddecay.neoforge;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import pro.mikey.accelerateddecay.AcceleratedDecay;

@Mod(AcceleratedDecay.MOD_ID)
public class AcceleratedDecayNeoforge {
    public AcceleratedDecayNeoforge() {
        AcceleratedDecay.init();
        NeoForge.EVENT_BUS.addListener(this::serverTick);
    }

    private void serverTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        AcceleratedDecay.levelTick(serverLevel);
    }
}
