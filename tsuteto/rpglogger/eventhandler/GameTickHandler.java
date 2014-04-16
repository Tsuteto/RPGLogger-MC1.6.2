package tsuteto.rpglogger.eventhandler;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiSleepMP;
import tsuteto.rpglogger.RpgLogger;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GameTickHandler implements ITickHandler
{
    private RpgLogger rpgLogger;
    private long clock = 0;

    public GameTickHandler(RpgLogger rpgLogger)
    {
        this.rpgLogger = rpgLogger;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        this.onTickInGame((Float) tickData[0], FMLClientHandler.instance().getClient());
    }

    public boolean onTickInGame(float f, Minecraft mc)
    {
        if (!mc.isIntegratedServerRunning() || mc.theWorld == null) return true;

        long tick = mc.theWorld.getWorldTime();

        if (clock != tick)
        {
            if (rpgLogger.setupTick > 0)
            {
                rpgLogger.setupTick--;
            }
            else
            {
                // Logging in sync with the game tick
                rpgLogger.onTick(f, mc);
                rpgLogger.windowRenderer.updateTick();
            }
        }

        // Render the log window when in-game or some screen
        if (rpgLogger.isWindowEnabled
                && (mc.inGameHasFocus || mc.currentScreen instanceof GuiGameOver || mc.currentScreen instanceof GuiSleepMP))
        {
            rpgLogger.windowRenderer.renderGameOverlay(mc);
        }

        clock = tick;

        return true;
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.RENDER);
    }

    @Override
    public String getLabel()
    {
        return null;
    }

}
