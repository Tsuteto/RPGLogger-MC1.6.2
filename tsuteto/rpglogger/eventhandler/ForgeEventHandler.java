package tsuteto.rpglogger.eventhandler;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import tsuteto.rpglogger.RPGLoggerLoader;
import tsuteto.rpglogger.RpgLogger;
import tsuteto.rpglogger.accessor.EntityLivingAccessor;

/**
 * Handles game events with Forge
 *
 * @author Tsuteto
 *
 */
public class ForgeEventHandler
{
    private RpgLogger rpgLogger;

    public ForgeEventHandler(RpgLogger rpgLogger)
    {
        this.rpgLogger = rpgLogger;
    }

    @ForgeSubscribe
    public void onEntityLivingBaseHurt(LivingHurtEvent event)
    {
        if (!RPGLoggerLoader.enabled || !rpgLogger.loaded)
        {
            return;
        }

        EntityLivingBase entity = event.entityLiving;
        DamageSource damagesource = event.source;
        float amount = event.ammount;

        EntityLivingAccessor bridgedEntity = new EntityLivingAccessor(entity);
        amount = bridgedEntity.applyArmorCalculations(damagesource, amount);
        amount = bridgedEntity.applyPotionDamageCalculations(damagesource, amount);

        rpgLogger.onAttack(entity, damagesource, amount);
    }

    @ForgeSubscribe
    public void onEntityLivingBaseDeath(LivingDeathEvent event)
    {
        if (RPGLoggerLoader.enabled && rpgLogger.loaded)
        {
            rpgLogger.onDeath(event.entityLiving, event.source);
        }
    }

    @ForgeSubscribe
    public void onEntityLivingBaseFall(LivingFallEvent event)
    {
        if (RPGLoggerLoader.enabled && rpgLogger.loaded)
        {
            rpgLogger.onFall(event.entityLiving, event.distance);
        }
    }

    @ForgeSubscribe
    public void onItemPickup(EntityItemPickupEvent event)
    {
        if (RPGLoggerLoader.enabled && rpgLogger.loaded && event.item.getEntityItem() != null)
        {
            rpgLogger.onItemPickup(event.entityPlayer, event.item.getEntityItem());
        }
    }
}
