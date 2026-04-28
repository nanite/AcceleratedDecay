package pro.mikey.accelerateddecay.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pro.mikey.accelerateddecay.AcceleratedDecay;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    public void acceleratedDecay$onBlockStateUpdate(BlockPos pos, BlockState blockState, int updateFlags, int updateLimit, CallbackInfoReturnable<Boolean> cir, @Local(name = "newState") BlockState newState) {
        if (!blockState.is(BlockTags.LEAVES) && !newState.is(BlockTags.LEAVES)) {
            return;
        }

        AcceleratedDecay.tryAddLeaveToQueue(((Level) (Object) this), pos, newState);
    }
}
