package tsuteto.rpglogger;

/**
 * Defines language for message
 *
 * @author Tsuteto
 *
 */
public enum Lang
{
    auto(null), en_US("en_US.lang"), ja_JP("ja_JP.lang"), ja_kana_JP("ja-kana_JP.lang");

    String langFile;

    Lang(String langFile)
    {
        this.langFile = langFile;
    }

    public String getLangFile()
    {
        return langFile;
    }
}

