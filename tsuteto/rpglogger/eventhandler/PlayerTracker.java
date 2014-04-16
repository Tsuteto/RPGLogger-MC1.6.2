package tsuteto.rpglogger.eventhandler;

import net.minecraft.entity.player.EntityPlayer;
import tsuteto.rpglogger.RpgLogger;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{
    private RpgLogger rpgLogger;

    public PlayerTracker(RpgLogger rpgLogger)
    {
        this.rpgLogger = rpgLogger;
    }

    @Override
    public void onPlayerLogin(EntityPlayer player)
    {
        if (rpgLogger == null) return;
        rpgLogger.onPlayerLoginWorld(player);
    }

    @Override
    public void onPlayerLogout(EntityPlayer player)
    {
        if (rpgLogger == null) return;
        rpgLogger.releaseLogger();
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player)
    {
        if (rpgLogger == null) return;
        rpgLogger.onPlayerTraveledDimension(player);
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player)
    {
        // TODO Auto-generated method stub

    }

}
