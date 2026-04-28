package pro.mikey.accelerateddecay;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AcceleratedDecay {
    public static final String MOD_ID = "accelerateddecay";

    public static void init() {
    }

    public static void levelTick(ServerLevel serverLevel, BlockBreakCallback blockBreakCallback) {
        Data data = Data.getOrCreate(serverLevel);
        Queue<PosSnapshot> posSnapshots = data.breakTargets;
        if (posSnapshots == null) {
            return;
        }

        if (posSnapshots.isEmpty()) {
            return;
        }

        for (int i = 0; i < 5 && !posSnapshots.isEmpty(); i++) {
            PosSnapshot snapshot = posSnapshots.poll();
            data.setDirty();
            if (snapshot == null) {
                continue;
            }

            BlockState blockState = serverLevel.getBlockState(snapshot.pos);
            if (!blockState.is(BlockTags.LEAVES)) {
                continue;
            }

            boolean eventResult = blockBreakCallback.onBreak(serverLevel, snapshot.pos, blockState, null);
            if (!eventResult) {
                continue;
            }

            destroyBlockWithOptionalSoundAndParticles(serverLevel, snapshot.pos, 512, i == 0);
        }
    }

    public static void tryAddLeaveToQueue(Level level, BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (state.getBlock() instanceof LeavesBlock && !state.getValue(BlockStateProperties.PERSISTENT) && state.getValue(BlockStateProperties.DISTANCE) == 7) {
            Data data = Data.getOrCreate(serverLevel);
            data.breakTargets.add(new PosSnapshot(pos, state));
            data.setDirty();
        }
    }

    // Mimic the vanilla breaking but an option to bypass the sound?
    public static void destroyBlockWithOptionalSoundAndParticles(Level level, BlockPos blockPos, int updateLimit, boolean soundAndParticles) {
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isAir()) {
            return;
        }

        FluidState fluidState = level.getFluidState(blockPos);
        if (!(blockState.getBlock() instanceof BaseFireBlock) && soundAndParticles) {
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, blockPos, Block.getId(blockState));
        }

        BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
        Block.dropResources(blockState, level, blockPos, blockEntity, null, ItemStack.EMPTY);

        boolean destroyed = level.setBlock(blockPos, fluidState.createLegacyBlock(), Block.UPDATE_ALL, updateLimit);
        if (destroyed) {
            level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(null, blockState));
        }
    }

    record PosSnapshot(BlockPos pos, BlockState state) {
        public static final Codec<PosSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter(PosSnapshot::pos),
                BlockState.CODEC.fieldOf("state").forGetter(PosSnapshot::state)
        ).apply(instance, PosSnapshot::new));
    }

    private static class Data extends SavedData {
        private static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PosSnapshot.CODEC.listOf().fieldOf("breakTargets").xmap(
                        ConcurrentLinkedDeque::new,
                        ArrayList::new
                ).forGetter(data -> data.breakTargets)
        ).apply(instance, Data::new));

        private static final SavedDataType<Data> TYPE = new SavedDataType<>(
                Identifier.fromNamespaceAndPath("accelerateddecay", "break_data"),
                Data::new,
                CODEC,
                null
        );

        private final ConcurrentLinkedDeque<PosSnapshot> breakTargets;

        private Data(ConcurrentLinkedDeque<PosSnapshot> breakTargets) {
            this.breakTargets = breakTargets;
        }

        private Data() {
            this.breakTargets = new ConcurrentLinkedDeque<>();
        }

        public static Data getOrCreate(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(TYPE);
        }
    }

    @FunctionalInterface
    public interface BlockBreakCallback {
        boolean onBreak(ServerLevel level, BlockPos pos, BlockState state, ServerPlayer player);
    }
}
