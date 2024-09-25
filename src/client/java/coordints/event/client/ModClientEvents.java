package coordints.event.client;

import com.google.common.base.Strings;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.util.ArrayList;

public class ModClientEvents {
    public static final Event<ClientChat> CLIENT_CHAT = EventFactory.createArrayBacked(ClientChat.class, listeners -> context -> {
        for (ClientChat listener : listeners) {
            listener.onChatMessage(context);
        }
    });

    public static final Event<CustomizeDebugHud> CUSTOMIZE_DEBUG_HUD = EventFactory.createArrayBacked(CustomizeDebugHud.class, listeners -> context -> {
        for (CustomizeDebugHud listener : listeners) {
            listener.onCustomizeDebugHud(context);
        }
    });
    @FunctionalInterface
    public interface ClientChat {
        void onChatMessage(ChatClientContext context);
    }

    public interface CustomizeDebugHud {
        void onCustomizeDebugHud(CustomizeDebugHudContext context);
    }
    @SuppressWarnings("unused")
    public static class ChatClientContext {
        private String message;
        private final String originalMessage;
        private boolean isCanceled = false;

        public ChatClientContext(String message) {
            this.setMessage(message);
            this.originalMessage = Strings.nullToEmpty(message);
            this.message = this.originalMessage;
        }

        public String getOriginalMessage() {
            return originalMessage;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = Strings.nullToEmpty(message);
        }

        @SuppressWarnings("SameReturnValue")
        public boolean isCancelable() {
            return true;
        }

        public boolean isCanceled() {
            return this.isCanceled;
        }

        public void setCanceled(boolean cancel) {
            this.isCanceled = cancel;
        }
    }
    public record CustomizeDebugHudContext(Window window, DrawContext context, float partialTick, ArrayList<String> text, Side side) {
        public enum Side {
            Left,
            Right
        }
    }
}
