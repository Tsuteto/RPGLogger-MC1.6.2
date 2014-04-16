package tsuteto.rpglogger.settings;

import net.minecraft.src.ModLoader;
import tsuteto.rpglogger.Lang;
import tsuteto.rpglogger.battle.EnumEnemyIdType;
import tsuteto.rpglogger.settings.moapi.OptionsUsingMoapi;

/**
 * RPG Logger settings
 *
 * @author Tsuteto
 *
 */
public class RpgLoggerSettings
{
    private OptionsApi api;
    private OptionsUsingFml fmlSettings;

    public void setFmlSettings(OptionsUsingFml fmlSettings)
    {
        this.fmlSettings = fmlSettings;
    }

    public void loadApiSettings()
    {
        if (ModLoader.isModLoaded("mod_MOAPI"))
        {
            this.api = new OptionsUsingMoapi();
        }
        else
        {
            this.api = this.fmlSettings;
        }

//        if (ModLoader.isModLoaded("GuiAPI"))
//        {
//            this.api = OptionsUsingGuiApi.getInstance();
//        }
//        else
//        {
//            this.api = mlSettings;
//        }
    }

    public float getOpacity()
    {
        return api.getOpacity();
    }

    public int getLines()
    {
        return api.getLines();
    }

    public int getScale()
    {
        return api.getScale();
    }

    public boolean isEnableExportLog()
    {
        return api.isEnableExportLog();
    }

    public EnumWindowPosition getPosition()
    {
        return api.getPosition();
    }

    public int getAllyPrefix()
    {
        return api.getAllyPrefix();
    }

    public Lang getLanguage()
    {
        return api.getLanguage();
    }

    public EnumEnemyIdType getEnemyIdType()
    {
        return api.getEnemyIdType();
    }

    public int getMemoryAlertThreshold()
    {
        return fmlSettings.getMemoryAlertThreshold();
    }
}
