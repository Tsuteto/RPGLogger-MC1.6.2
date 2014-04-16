package tsuteto.rpglogger.eventhandler;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import tsuteto.rpglogger.RpgLogger;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class ModKeyHandler extends KeyHandler
{
    private RpgLogger rpgLogger;

    public ModKeyHandler(RpgLogger rpgLogger)
    {
        super(new KeyBinding[]{RpgLogger.KB_TOGGLE_WINDOW}, new boolean[]{false});
        this.rpgLogger = rpgLogger;
    }

    @Override
    public String getLabel()
    {
        return null;
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();

        if (mc.currentScreen == null && mc.thePlayer != null && tickEnd)
        {
            if (rpgLogger == null) return;
            rpgLogger.keyboardEvent(kb, mc);
        }
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
    {

    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT);
    }

}
