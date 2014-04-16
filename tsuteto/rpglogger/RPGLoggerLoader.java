package tsuteto.rpglogger;

import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Loader Class of the RPG Logger
 *
 * @author Tsuteto
 *
 */
@Mod(modid = RPGLoggerLoader.modid, name = "RPG Logger", version = "3.7.1-MC1.6.2")
public class RPGLoggerLoader
{
    public static final String modid = "RPGLogger";

    // @MLProp(name = "enabled", info = "if false, the RPG Logger will not activate.") FOR DEBUG
    public static boolean enabled = true;

    private final RpgLogger rpgLogger;

    public RPGLoggerLoader()
    {
        RpgLogger.init();
        this.rpgLogger = RpgLogger.getInstance();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration conf = new Configuration(event.getSuggestedConfigurationFile());
        this.rpgLogger.loadFmlSettings(conf);
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        if (enabled)
        {
            this.rpgLogger.load();
        }
    }

    @Mod.EventHandler
    public void modsLoaded(FMLPostInitializationEvent event)
    {
        if (enabled)
        {
            rpgLogger.prepare();
        }
    }
}
