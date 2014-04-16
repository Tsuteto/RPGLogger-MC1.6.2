package tsuteto.rpglogger.settings;

import net.minecraftforge.common.Configuration;
import tsuteto.rpglogger.Lang;
import tsuteto.rpglogger.battle.EnumEnemyIdType;

/**
 * Handles RPG Logger settings for ForgeModLoader
 *
 * @author Tsuteto
 *
 */
public class OptionsUsingFml implements OptionsApi
{

    public int opacity = 80;
    public int lines = 15;
    public boolean isEnableExportLog = true;
    public int scale = 0;
    public int position = 0;
    public int allyPrefix = 0;
    public String language = "auto";
    public String enemyIdType = "A";
    public int memoryAlert = 90;

    public OptionsUsingFml(Configuration conf)
    {
        conf.load();

        this.opacity = conf.get("display", "opacity", opacity, "Percentage of opacity for the log window.").getInt();
        this.lines = conf.get("display", "lines", lines, "Number of lines in the log window.").getInt();
        this.scale = conf.get("display", "scale", scale, "Log window's scale, text size. 0=adjust to the game settings, 1=small, 2=normal and 3=large.").getInt();
        this.position = conf.get("display", "position", position, "Log window's position. 0=top-right, 1=top-center and 2=top-left.").getInt();

        this.isEnableExportLog = conf.get("system", "enableExportLog", isEnableExportLog, "Toggle exporting log to file. 'true' or 'false'.").getBoolean(isEnableExportLog);

        this.language = conf.get("message", "language", language, "Language in logger messages. auto=follow the game settings, ja_JP=Japanese, ja_kana_JP=Japanese kana, en_US=English.").getString();
        this.allyPrefix = conf.get("message", "allyPrefix", position, "Prefix type of your ally. 0-10").getInt();
        this.enemyIdType = conf.get("message", "enemyIdType", enemyIdType, "Type of enemy identification. A=A B C..., N=1 2 3..., off=none").getString();
        this.memoryAlert = conf.get("message", "memoryAlert", memoryAlert, "Threshold percentage of usage memory for alert message. -1=Never").getInt();

        conf.save();
    }

    @Override
    public float getOpacity()
    {
        return this.opacity / 100F;
    }

    @Override
    public int getLines()
    {
        return this.lines;
    }

    @Override
    public boolean isEnableExportLog()
    {
        return this.isEnableExportLog;
    }

    @Override
    public int getScale()
    {
        return this.scale;
    }

    @Override
    public EnumWindowPosition getPosition()
    {
        return EnumWindowPosition.values()[this.position];
    }

    @Override
    public int getAllyPrefix()
    {
        return this.allyPrefix;
    }

    @Override
    public Lang getLanguage()
    {
        return Lang.valueOf(this.language);
    }

    @Override
    public EnumEnemyIdType getEnemyIdType()
    {
        return EnumEnemyIdType.valueOf(this.enemyIdType);
    }

    public int getMemoryAlertThreshold()
    {
        return this.memoryAlert;
    }
}
