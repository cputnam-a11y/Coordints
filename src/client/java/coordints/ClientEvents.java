package coordints;


import coordints.event.client.ModClientEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ClientEvents {
    public static void init() {
        ModClientEvents.CUSTOMIZE_DEBUG_HUD.register(ClientEvents::onDebugScreen);
        ModClientEvents.CLIENT_CHAT.register(ClientEvents::onChatMessage);
    }
    static void onChatMessage(ModClientEvents.ChatClientContext event) {
        final String message = event.getMessage();
        if (Utils.messageContainsCoords(message)) {
            switch (Config.BLACKLIST_ACTION) {
                case REDACT -> event.setMessage(Utils.redactCoords(message));
                case RANDOMISE -> event.setMessage(Utils.redactCoords(message, s -> String.valueOf(Utils.RANDOM.nextInt())));
                case BLOCK -> {
                    Optional.ofNullable(MinecraftClient.getInstance().player).ifPresent(player -> {
                        player.sendMessage(Text.translatable("messages.coordints.blocked").formatted(Formatting.RED), true);
                    });
                    event.setCanceled(true);
                }
            }
        }
    }
    static void onDebugScreen(final ModClientEvents.CustomizeDebugHudContext event) {
        if (Events.isInBlacklistedArea())
            event.text().removeIf(line -> Stream.of("XYZ: ", "Block: ", "Chunk: ", "Chunks[").anyMatch(line::startsWith));
    }
}
