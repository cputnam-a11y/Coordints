package coordints.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkSectionPos;

public class ModEvents {
    public static final Event<EnteringSection> ENTERING_SECTION = EventFactory.createArrayBacked(EnteringSection.class, listeners -> context -> {
        for (EnteringSection listener : listeners) {
            listener.onEntityEnteringSection(context);
        }
    });

    public static final Event<PlayerLoggedIn> PLAYER_LOGGED_IN = EventFactory.createArrayBacked(PlayerLoggedIn.class, listeners -> context -> {
        for (PlayerLoggedIn listener : listeners) {
            listener.onPlayerLoggedIn(context);
        }
    });

    @FunctionalInterface
    public interface EnteringSection {
        void onEntityEnteringSection(EntityEnteringSectionContext context);
    }

    @FunctionalInterface
    public interface PlayerLoggedIn {
        void onPlayerLoggedIn(PlayerLoggedInContext context);
    }

    @SuppressWarnings("unused")
    public record EntityEnteringSectionContext(Entity entity, long packedOldPos, long packedNewPos) {
        public boolean didChunkChange() {
            return ChunkSectionPos.unpackX(packedOldPos) != ChunkSectionPos.unpackX(packedNewPos) || ChunkSectionPos.unpackZ(packedOldPos) != ChunkSectionPos.unpackZ(packedNewPos);
        }

        public ChunkSectionPos newPos() {
            return ChunkSectionPos.from(packedNewPos);
        }

        public ChunkSectionPos oldPos() {
            return ChunkSectionPos.from(packedOldPos);
        }
    }

    public record PlayerLoggedInContext(PlayerEntity entity) {
    }

}
