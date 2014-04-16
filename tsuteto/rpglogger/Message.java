package tsuteto.rpglogger;

import net.minecraft.src.ModLoader;

public class Message
{
    public static void load()
    {
        ModLoader.addLocalization("rpglogger.options.openLogFile", "en_US", "Open log file on your desktop");
        ModLoader.addLocalization("rpglogger.options.openLogFile", "ja_JP", "デスクトップでログファイルを開く");

        ModLoader.addLocalization("rpglogger.options.exportLog", "en_US", "Export log to files");
        ModLoader.addLocalization("rpglogger.options.exportLog", "ja_JP", "ログをファイルに残す");

        ModLoader.addLocalization("rpglogger.options.allyPrefix", "en_US", "Ally Prefix");
        ModLoader.addLocalization("rpglogger.options.allyPrefix", "ja_JP", "仲間の呼び方");

        ModLoader.addLocalization("rpglogger.options.position", "en_US", "Position");
        ModLoader.addLocalization("rpglogger.options.position", "ja_JP", "配置");

        ModLoader.addLocalization("rpglogger.options.scale", "en_US", "Scale");
        ModLoader.addLocalization("rpglogger.options.scale", "ja_JP", "スケール");

        ModLoader.addLocalization("rpglogger.options.lines", "en_US", "Num of lines");
        ModLoader.addLocalization("rpglogger.options.lines", "ja_JP", "メッセージ数");

        ModLoader.addLocalization("rpglogger.options.opacity", "en_US", "Opacity");
        ModLoader.addLocalization("rpglogger.options.opacity", "ja_JP", "不透明度");

        ModLoader.addLocalization("rpglogger.options.language", "en_US", "Language in the logger");
        ModLoader.addLocalization("rpglogger.options.language", "ja_JP", "メッセージの言語");

        ModLoader.addLocalization("rpglogger.options.enemyIdType", "en_US", "Enemy ID Type");
        ModLoader.addLocalization("rpglogger.options.enemyIdType", "ja_JP", "敵IDのタイプ");
     
//        options.scale.adjusting=合わせる
        ModLoader.addLocalization("rpglogger.options.scale.adjusting", "en_US", "Adjusting");
        ModLoader.addLocalization("rpglogger.options.scale.adjusting", "ja_JP", "合わせる");
        
//        options.scale.small=小さい
        ModLoader.addLocalization("rpglogger.options.scale.small", "en_US", "Small");
        ModLoader.addLocalization("rpglogger.options.scale.small", "ja_JP", "小さい");
//        options.scale.normal=普通
        ModLoader.addLocalization("rpglogger.options.scale.normal", "en_US", "Normal");
        ModLoader.addLocalization("rpglogger.options.scale.normal", "ja_JP", "普通");
//        options.scale.large=大きい
        ModLoader.addLocalization("rpglogger.options.scale.large", "en_US", "Large");
        ModLoader.addLocalization("rpglogger.options.scale.large", "ja_JP", "大きい");
//        options.position.topright=右上
        ModLoader.addLocalization("rpglogger.options.position.topright", "en_US", "Top-right");
        ModLoader.addLocalization("rpglogger.options.position.topright", "ja_JP", "右上");
//        options.position.topcenter=上中央
        ModLoader.addLocalization("rpglogger.options.position.topcenter", "en_US", "Top-center");
        ModLoader.addLocalization("rpglogger.options.position.topcenter", "ja_JP", "右中央");
//        options.position.topleft=左上
        ModLoader.addLocalization("rpglogger.options.position.topleft", "en_US", "Top-left");
        ModLoader.addLocalization("rpglogger.options.position.topleft", "ja_JP", "左上");
//        options.turnedWindow.on=RPG Loggerウィンドウ オン
        ModLoader.addLocalization("rpglogger.options.turnedWindow.on", "en_US", "RPG Logger window: ON");
        ModLoader.addLocalization("rpglogger.options.turnedWindow.on", "ja_JP", "RPG Loggerウィンドウ オン");
//        options.turnedWindow.off=RPG Loggerウィンドウ オフ
        ModLoader.addLocalization("rpglogger.options.turnedWindow.off", "en_US", "RPG Logger window: OFF");
        ModLoader.addLocalization("rpglogger.options.turnedWindow.off", "ja_JP", "RPG Loggerウィンドウ オフ");
//        options.language.auto=自動
        ModLoader.addLocalization("rpglogger.options.language.auto", "en_US", "Auto");
        ModLoader.addLocalization("rpglogger.options.language.auto", "ja_JP", "自動");
//        options.language.en_US=英語
        ModLoader.addLocalization("rpglogger.options.language.en_US", "en_US", "English");
        ModLoader.addLocalization("rpglogger.options.language.en_US", "ja_JP", "英語");
//        options.language.ja_JP=日本語
        ModLoader.addLocalization("rpglogger.options.language.ja_JP", "en_US", "Japanese");
        ModLoader.addLocalization("rpglogger.options.language.ja_JP", "ja_JP", "日本語");
//        options.language.ja_kana_JP=かな
        ModLoader.addLocalization("rpglogger.options.language.ja_kana_JP", "en_US", "Japanese Kana");
        ModLoader.addLocalization("rpglogger.options.language.ja_kana_JP", "ja_JP", "かな");
//        options.enemyIdType.A=A B C...
        ModLoader.addLocalization("rpglogger.options.enemyIdType.A", "A B C...");
//        options.enemyIdType.N=1 2 3...
        ModLoader.addLocalization("rpglogger.options.enemyIdType.N", "1 2 3...");
//        options.enemyIdType.off=なし
        ModLoader.addLocalization("rpglogger.options.enemyIdType.off", "en_US", "None");
        ModLoader.addLocalization("rpglogger.options.enemyIdType.off", "ja_JP", "なし");
        
        // GLOBAL
        ModLoader.addLocalization("options.GLOBAL", "en_US", "GLOBAL");
        ModLoader.addLocalization("options.GLOBAL", "ja_JP", "共通");
    }
}
