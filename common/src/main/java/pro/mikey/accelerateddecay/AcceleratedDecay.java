package pro.mikey.accelerateddecay;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.utils.value.IntValue;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class AcceleratedDecay {
    public static final String MOD_ID = "accelerateddecay";

    private static final List<TimedDimBlockPos> timeBasedScanLocations = new ArrayList<>();

    public static void init() {
        BlockEvent.BREAK.register(AcceleratedDecay::breakHandler);
        TickEvent.SERVER_LEVEL_POST.register(AcceleratedDecay::levelTick);
    }

    private static void levelTick(ServerLevel serverLevel) {
        if (timeBasedScanLocations.isEmpty()) {
            return;
        }

        Instant now = Instant.now();
        List<TimedDimBlockPos> locations = timeBasedScanLocations.stream()
                .filter(e -> !e.checkAfter.isAfter(now) && serverLevel.dimension().equals(e.dim))
                .toList();

        if (locations.isEmpty()) {
            return;
        }

        for (TimedDimBlockPos location : locations) {
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
                EventResult eventResult = BlockEvent.BREAK.invoker().breakBlock(serverLevel, yeetLeaf, blockState, location.player, null);
                if (eventResult.isFalse()) {
                    continue;
                }

                destroyBlockWithOptionalSoundAndParticles(serverLevel, yeetLeaf, true, 512, location.player, isFirst);
                isFirst = false;
            }

            timeBasedScanLocations.remove(location);
        }
    }

    private static EventResult breakHandler(Level level, BlockPos blockPos, BlockState state, ServerPlayer player, @Nullable IntValue intValue) {
        if (!state.is(BlockTags.LOGS)) {
            return EventResult.pass();
        }

        timeBasedScanLocations.add(new TimedDimBlockPos(Instant.now().plus(1, ChronoUnit.SECONDS), blockPos, level.dimension(), player));
        return EventResult.pass();
    }

    private static final BlockPos[] SCAN_LOCATIONS;
    static {
        var box = new BoundingBox(BlockPos.ZERO).inflatedBy(1);
        SCAN_LOCATIONS = BlockPos.betweenClosedStream(box).map(BlockPos::immutable).filter(e -> !e.equals(BlockPos.ZERO)).distinct().toArray(BlockPos[]::new);
    }

    private static Set<BlockPos> seekLeaves(Level level, BlockPos pos) {
        Set<BlockPos> validLocations = new HashSet<>();
        Set<BlockPos> walked = new HashSet<>();
        Deque<BlockPos> nextToScan = new ArrayDeque<>(List.of(pos));

        while (!nextToScan.isEmpty()) {
            var currentLocation = nextToScan.pop();
            for (BlockPos offset : SCAN_LOCATIONS) {
                BlockPos nextLocation = currentLocation.offset(offset);
                var state = level.getBlockState(nextLocation);

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
            BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            Block.dropResources(blockState, level, blockPos, blockEntity, player, ItemStack.EMPTY);
        }

        boolean bl2 = level.setBlock(blockPos, fluidState.createLegacyBlock(), 3, i);
        if (bl2) {
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, blockPos);
        }
    }

    record TimedDimBlockPos(
            Instant checkAfter,
            BlockPos pos,
            ResourceKey<Level> dim,
            ServerPlayer player
    ) {}
}
