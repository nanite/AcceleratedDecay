package pro.mikey.accelerateddecay.forge;

import dev.architectury.platform.forge.EventBuses;
import pro.mikey.accelerateddecay.AcceleratedDecay;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AcceleratedDecay.MOD_ID)
public class AcceleratedDecayForge {
    public AcceleratedDecayForge() {
        EventBuses.registerModEventBus(AcceleratedDecay.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AcceleratedDecay.init();
    }
}
