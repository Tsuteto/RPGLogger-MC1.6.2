package tsuteto.rpglogger.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.InvalidPropertiesFormatException;
import java.util.Locale;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import net.minecraft.util.StatCollector;
import tsuteto.rpglogger.Lang;
import tsuteto.rpglogger.RpgLogger;

import com.google.common.base.Strings;

/**
 * Translates key to message in a specified language
 *
 * @author Tsuteto
 *
 */
public class RlMsgTranslate
{
    private static RlMsgTranslate instance;

    private Properties rlTranslateTable;
    private Properties customTranslateTable = null;
    private File customLangFile;
    private long customLangLastModified = -1;
    public boolean islangFileUpdated = false;
    public boolean isCustomFileLoaded = false;
    public Lang lang = null;

    private static final String rlLangFileDir = "/lang/rpglogger";
    private static final String customLangFileDir = "/RPGLogger/lang";

    private RlMsgTranslate(Lang lang)
    {
        loadLangFile(lang);
    }

    public static void createInstance(Lang lang)
    {
        instance = new RlMsgTranslate(lang);
    }

    public static RlMsgTranslate getInstance()
    {
        return instance;
    }

    public void setLanguage(Lang lang)
    {
        loadLangFile(lang);
    }

    public void loadLangFile(Lang lang)
    {
        Minecraft mc = ModLoader.getMinecraftInstance();

        boolean isLangChanged = this.lang != lang;

        this.lang = lang;
        islangFileUpdated = false;

        // Set a new lang file path
        customLangFile = new File(Minecraft.getMinecraft().mcDataDir, customLangFileDir + "/" + lang.getLangFile());

        if (!customLangFile.getParentFile().exists())
        {
            // Create a directory
            if (!customLangFile.getParentFile().mkdirs())
            {
                RpgLogger.systemLog("Couldn't make a directory for custom lang files");
            }
        }

        // Check modification of custom lang file
        if (!isCustomFileLoaded || isLangChanged)
        {
            customLangLastModified = customLangFile.lastModified();
        }
        else if (customLangLastModified == customLangFile.lastModified())
        {
            return;
        }
        else
        {
            customLangLastModified = customLangFile.lastModified();
        }

        if (isLangChanged)
        {
            rlTranslateTable = new Properties();
            try
            {
                rlTranslateTable.load((RlMsgTranslate.class).getResourceAsStream(rlLangFileDir + "/" + lang.getLangFile()));
            }
            catch (Exception e)
            {
                RpgLogger.systemLog("Failed to read RPG Logger lang file");
                e.printStackTrace();
            }
        }

        customTranslateTable = new Properties();
        isCustomFileLoaded = false;

        if (!customLangFile.exists())
        {
            saveCustomLangFile();
            customLangLastModified = customLangFile.lastModified();
        }

        // Change locale to English temporarily
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);

        InputStreamReader reader = null;

        try
        {
            // Read as old format first
            try
            {
                FileInputStream fis = new FileInputStream(customLangFile);
                customTranslateTable.loadFromXML(fis);
                isCustomFileLoaded = true;
            }
            catch (InvalidPropertiesFormatException e)
            {
                if (!"Content is not allowed in prolog.".equals(e.getCause().getMessage()))
                {
                    RpgLogger.systemLog("Failed to read custom lang file due to invalid format");
                    e.printStackTrace();
                    return;
                }
            }
            catch (IOException e)
            {
                RpgLogger.systemLog("Failed to read custom lang file");
                e.printStackTrace();
                return;
            }
            finally
            {
                try
                {
                    if (reader != null)
                    {
                        reader.close();
                    }
                }
                catch (IOException e)
                {}
            }

            if (!isCustomFileLoaded)
            {
                // Read as version 3 format
                try
                {
                    reader = new InputStreamReader(new FileInputStream(customLangFile), "UTF-8");
                    customTranslateTable.load(reader);
                    isCustomFileLoaded = true;
                }
                catch (IOException e)
                {
                    RpgLogger.systemLog("Failed to read custom lang file");
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {}
                }
            }

            if (isCustomFileLoaded)
            {
                islangFileUpdated = true;
                RpgLogger.systemLog("Loaded custom lang file: " + lang.getLangFile());
            }
        }
        finally
        {
            // Restore the locale
            Locale.setDefault(originalLocale);
        }
    }

    public void saveCustomLangFile()
    {
        OutputStreamWriter writer = null;
        try
        {
            writer = new OutputStreamWriter(new FileOutputStream(customLangFile), "UTF-8");
            customTranslateTable.store(writer, "RPG Logger - Custom lang file");
            customLangLastModified = customLangFile.lastModified();
            // RpgLogger.systemLog("Saved custom lang File: " + customLangFile.getName());
        }
        catch (IOException e)
        {
            RpgLogger.systemLog("Failed to save custom lang file");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (IOException e) {}
        }
    }

    public String getMsg(String s)
    {
        return getMsgOrDefault(s, "");
    }

    public String getMsgOrDefault(String s, String defaultValue)
    {
        String msg;
        msg = customTranslateTable.getProperty(s);
        if (!Strings.isNullOrEmpty(msg))
        {
            return msg;
        }
        msg = rlTranslateTable.getProperty(s);
        if (!Strings.isNullOrEmpty(msg))
        {
            return msg;
        }
        msg = StatCollector.translateToLocal(s); // returns an empty string if no message found
        if (!msg.equals(s))
        {
            return msg;
        }
        return defaultValue;
    }

    public String getMsg(String s, Object... aobj)
    {
        String s1 = getMsg(s);
        return String.format(s1, aobj);
    }

    public String translateNamedKey(String s)
    {
        String name = customTranslateNamedKey(s);
        // RpgLogger.systemLog("Custom: " + name);
        if (name != null)
        {
            return name;
        }
        name = rlTranslateNamedKey(s);
        // RpgLogger.systemLog("RL translate: " + name);
        if (name != null)
        {
            return name;
        }
        name = StatCollector.translateToLocal(s + ".name"); // returns an empty string if no message found
        //RpgLogger.systemLog("Internal translate: " + name);
        if (name.length() != 0)
        {
            return name;
        }
        return null;
    }

    public String rlTranslateNamedKey(String s)
    {
        String name = rlTranslateTable.getProperty(s + ".name");
        if (name != null && name.length() != 0)
        {
            return name;
        }
        return null;
    }

    public String customTranslateNamedKey(String s)
    {
        String name = customTranslateTable.getProperty(s + ".name");
        if (name != null && name.length() != 0)
        {
            return name;
        }
        return null;
    }

    public boolean isKeyAvailable(String s)
    {
        if (!StatCollector.translateToLocal(s).equals(s))
        {
            return true;
        }
        if (rlTranslateTable.containsKey(s))
        {
            return true;
        }
        String prop = customTranslateTable.getProperty(s);
        if (prop != null && prop.length() > 0)
        {
            return true;
        }
        return false;
    }

    public boolean addNAKeyToCustomTable(String key)
    {
        return addNAKeyToCustomLang(key, "");
    }

    public boolean addNAKeyToCustomLang(String key, String value)
    {
        if (!customTranslateTable.containsKey(key))
        {
            customTranslateTable.setProperty(key, value);
            RpgLogger.systemLog("Added key: " + key);
            return true;
        }
        return false;
    }
}
