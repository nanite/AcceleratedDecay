package pro.mikey.accelerateddecay.neoforge;

import net.neoforged.fml.common.Mod;
import pro.mikey.accelerateddecay.AcceleratedDecay;

@Mod(AcceleratedDecay.MOD_ID)
public class AcceleratedDecayNeoforge {
    public AcceleratedDecayNeoforge() {
//        EventBuses.registerModEventBus(AcceleratedDecay.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        AcceleratedDecay.init();
    }
}
