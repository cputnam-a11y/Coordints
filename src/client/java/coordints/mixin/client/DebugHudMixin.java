package coordints.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import coordints.event.client.ModClientEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
    @Shadow @Final private MinecraftClient client;

    @WrapOperation(method = "drawLeftText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V"))
    private void onDrawLeftText(DebugHud instance, DrawContext context, List<String> text, boolean left, Operation<Void> original) {
        ModClientEvents.CUSTOMIZE_DEBUG_HUD.invoker().onCustomizeDebugHud(new ModClientEvents.CustomizeDebugHudContext(client.getWindow(), context,client.getRenderTickCounter().getLastDuration(), (ArrayList<String>) text, ModClientEvents.CustomizeDebugHudContext.Side.Left));
        original.call(instance, context, text, left);
    }
    @WrapOperation(method = "drawRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;drawText(Lnet/minecraft/client/gui/DrawContext;Ljava/util/List;Z)V"))
    private void onDrawRightText(DebugHud instance, DrawContext context, List<String> text, boolean right, Operation<Void> original) {
        ModClientEvents.CUSTOMIZE_DEBUG_HUD.invoker().onCustomizeDebugHud(new ModClientEvents.CustomizeDebugHudContext(client.getWindow(), context,client.getRenderTickCounter().getLastDuration(), (ArrayList<String>) text, ModClientEvents.CustomizeDebugHudContext.Side.Right));
        original.call(instance, context, text, right);
    }
}
