package coordints.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import coordints.event.ModEvents;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEntityManager.Listener.class)
public class ClientEntityManagerMixin {
    @Shadow private long lastSectionPos;
    @Unique
    private Entity realEntity;
    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/ClientEntityManager$Listener;entity:Lnet/minecraft/world/entity/EntityLike;", shift = At.Shift.AFTER))
    private <T extends EntityLike> void initRealEntity(ClientEntityManager<T> clientEntityManager, EntityLike entity, long pos, EntityTrackingSection<T> section, CallbackInfo ci) {
        realEntity = (entity instanceof Entity) ? (Entity) entity : null;
    }
    @SuppressWarnings("MixinExtrasOperationParameters")
    @WrapOperation(method = "updateEntityPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityTrackingSection;add(Lnet/minecraft/world/entity/EntityLike;)V"))
    private <T extends EntityLike> void onEntityEnteringSection(EntityTrackingSection<T> instance, T entity, Operation<Void> original, @Share("oldPos") LocalLongRef oldPosRef) {
        original.call(instance, entity);
        oldPosRef.set(lastSectionPos);
    }
    @ModifyVariable(method = "updateEntityPosition", at = @At(value = "STORE"))
    private long getCompareResult(long value, @Share("hasChanged") LocalBooleanRef hasChangedRef) {
        hasChangedRef.set(value != this.lastSectionPos);
        return value;
    }
    @Inject(method = "updateEntityPosition", at = @At(value = "TAIL"))
    private void onEntityEnteringSection(CallbackInfo ci, @Share("oldPos") LocalLongRef oldPosRef, @Share("hasChanged") LocalBooleanRef hasChangedRef) {
        if (realEntity != null && hasChangedRef.get()) {
            ModEvents.ENTERING_SECTION.invoker().onEntityEnteringSection(new ModEvents.EntityEnteringSectionContext(realEntity, oldPosRef.get(), lastSectionPos));
        }
    }
}
