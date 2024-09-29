package coordints.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import coordints.event.ModEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityTrackingSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerEntityManager.Listener.class)
public abstract class ServerEntityManagerListenerMixin implements EntityChangeListener {
    @Shadow private long sectionPos;
    @Unique
    private Entity realEntity;
    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ServerEntityManager$Listener;entity:Lnet/minecraft/world/entity/EntityLike;", shift = At.Shift.AFTER))
    private <T extends EntityLike> void initRealEntity(ServerEntityManager<T> serverEntityManager, EntityLike entity, long sectionPos, EntityTrackingSection<T> section, CallbackInfo ci) {
        realEntity = (entity instanceof Entity) ? (Entity) entity : null;
    }
    @SuppressWarnings("MixinExtrasOperationParameters")
    @WrapOperation(method = "updateEntityPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityTrackingSection;add(Lnet/minecraft/world/entity/EntityLike;)V"))
    private <T extends EntityLike> void setOldPosRef(EntityTrackingSection<T> instance, T entity, Operation<Void> original, @Share("oldPos")LocalLongRef oldPosRef) {
        original.call(instance, entity);
        oldPosRef.set(sectionPos);
    }
    @ModifyVariable(method = "updateEntityPosition", at = @At(value = "STORE"), ordinal = 0)
    private long getCompareResult(long value, @Share("hasChanged") LocalBooleanRef hasChangedRef) {
        hasChangedRef.set(value != this.sectionPos);
        return value;
    }
    @Inject(method = "updateEntityPosition", at = @At("TAIL"))
    private <T extends EntityLike> void onEntityEnteringSection(CallbackInfo ci, @Share("oldPos") LocalLongRef oldPosRef, @Share("hasChanged") LocalBooleanRef hasChangedRef) {
        if (realEntity != null && hasChangedRef.get()) {
            ModEvents.ENTERING_SECTION.invoker().onEntityEnteringSection(new ModEvents.EntityEnteringSectionContext(realEntity, oldPosRef.get(), sectionPos));
        }
    }
}
