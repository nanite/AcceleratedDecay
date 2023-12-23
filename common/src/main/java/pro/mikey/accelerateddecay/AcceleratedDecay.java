package pro.mikey.accelerateddecay;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class AcceleratedDecay {
    public static final String MOD_ID = "accelerateddecay";

    private static final Set<TimedDimBlockPos> timeBasedScanLocations = new ConcurrentSet<>();

    public static void levelTickPostEvent(ServerLevel serverLevel) {
        if (timeBasedScanLocations.isEmpty()) {
            return;
        }

        Instant now = Instant.now();

        for (TimedDimBlockPos location : timeBasedScanLocations) {
            if (!location.checkAfter.isAfter(now) && serverLevel.dimension().equals(location.dim)) {
                if (location.player == null || !location.player.isAlive()) {
                    continue;
                }

                Set<BlockPos> yeetLeaves = seekLeaves(serverLevel, location.pos);

                boolean isFirst = true;
                for (BlockPos yeetLeaf : yeetLeaves) {
                    BlockState blockState = serverLevel.getBlockState(yeetLeaf);
                    if (!blockState.is(BlockTags.LEAVES)) {
                        continue;
                    }

                    // Allow events to block us
                    if (!CrossPlatform.emitBlockBrokeEvent(serverLevel, yeetLeaf, blockState, location.player)) {
                        continue;
                    }

                    destroyBlockWithOptionalSoundAndParticles(serverLevel, yeetLeaf, true, 512, location.player, isFirst);
                    isFirst = false;
                }

                timeBasedScanLocations.remove(location);
            }
        }
    }

    public static void breakEvent(Level level, BlockState state, BlockPos blockPos, ServerPlayer player) {
        if (!state.is(BlockTags.LOGS)) {
            return;
        }

        timeBasedScanLocations.add(new TimedDimBlockPos(Instant.now().plus(1, ChronoUnit.SECONDS), blockPos, level.dimension(), player));
    }


    private static final BlockPos[] SCAN_LOCATIONS;
    static {
        BoundingBox box = new BoundingBox(-1, -1, -1, 1, 1, 1);
        SCAN_LOCATIONS = BlockPos.betweenClosedStream(box).map(BlockPos::immutable).filter(e -> !e.equals(BlockPos.ZERO)).distinct().toArray(BlockPos[]::new);
    }

    private static Set<BlockPos> seekLeaves(Level level, BlockPos pos) {
        Set<BlockPos> validLocations = new HashSet<>();
        Set<BlockPos> walked = new HashSet<>();
        Deque<BlockPos> nextToScan = new ArrayDeque<>();
        nextToScan.add(pos);

        while (!nextToScan.isEmpty()) {
            BlockPos currentLocation = nextToScan.pop();
            for (BlockPos offset : SCAN_LOCATIONS) {
                BlockPos nextLocation = currentLocation.offset(offset);
                BlockState state = level.getBlockState(nextLocation);

                if (state.getBlock() instanceof LeavesBlock && !state.getValue(BlockStateProperties.PERSISTENT) && state.getValue(BlockStateProperties.DISTANCE) == 7 && validLocations.add(nextLocation)) {
                    if (walked.add(nextLocation)) {
                        nextToScan.add(nextLocation);
                    }
                }
            }
        }

        return validLocations;
    }

    public static void destroyBlockWithOptionalSoundAndParticles(Level level, BlockPos blockPos, boolean bl, int i, ServerPlayer player, boolean soundAndParticles) {
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isAir()) {
            return;
        }

        FluidState fluidState = level.getFluidState(blockPos);
        if (!(blockState.getBlock() instanceof BaseFireBlock) && soundAndParticles) {
            level.levelEvent(2001, blockPos, Block.getId(blockState));
        }

        if (bl) {
            BlockEntity blockEntity = blockState.getBlock().isEntityBlock() ? level.getBlockEntity(blockPos) : null;
            Block.dropResources(blockState, level, blockPos, blockEntity, player, ItemStack.EMPTY);
        }

        boolean bl2 = level.setBlock(blockPos, fluidState.createLegacyBlock(), 3, i);
        if (bl2) {
            level.levelEvent(player, 2001, blockPos, Block.getId(blockState));
        }
    }

    static final class TimedDimBlockPos {
        private final Instant checkAfter;
        private final BlockPos pos;
        private final ResourceKey<Level> dim;
        private final ServerPlayer player;

        public TimedDimBlockPos(Instant checkAfter, BlockPos pos, ResourceKey<Level> dim, ServerPlayer player) {
            this.checkAfter = checkAfter;
            this.pos = pos;
            this.dim = dim;
            this.player = player;
        }
    }
}
