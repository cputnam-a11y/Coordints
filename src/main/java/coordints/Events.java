package coordints;

import coordints.event.ModEvents;
import net.minecraft.entity.player.PlayerEntity;

public class Events {
    private static boolean inBlacklistedArea = false;
    public static void init() {
        ModEvents.ENTERING_SECTION.register(Events::onEnteringSection);
        ModEvents.PLAYER_LOGGED_IN.register((context -> onEnteringSection(new ModEvents.EntityEnteringSectionContext(context.entity(), 0L, 0L))));
    }
    static void onEnteringSection(final ModEvents.EntityEnteringSectionContext event) {
        final var entity = event.entity();
        if (event.didChunkChange() && entity instanceof PlayerEntity) {
            inBlacklistedArea = false;
            if (Config.USE_BLACKLIST) {
                final double playerX = entity.getX();
                final double playerZ = entity.getZ();
                for (int i = 0; i < Config.BLACKLISTED_COORDS.length; i += 4) {
                    if (playerX >= Config.BLACKLISTED_COORDS[i] && playerX <= Config.BLACKLISTED_COORDS[i + 1]
                            && playerZ >= Config.BLACKLISTED_COORDS[i + 2] && playerZ <= Config.BLACKLISTED_COORDS[i + 3]) {
                        inBlacklistedArea = true;
                        break;
                    }
                }
            }
        }
    }
    public static boolean isInBlacklistedArea() {
        return inBlacklistedArea;
    }
}
