package coordints.mixin.client;

import com.llamalad7.mixinextras.sugar.Cancellable;
import coordints.event.client.ModClientEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
 @ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), argsOnly = true)
    private String onChatMessage(String message, @Cancellable CallbackInfo ci) {
        var context = new ModClientEvents.ChatClientContext(message);
        ModClientEvents.CLIENT_CHAT.invoker().onChatMessage(context);
        if (message.isEmpty() || context.isCanceled())
            ci.cancel();
        return context.getMessage();
    }
}
