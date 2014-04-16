package tsuteto.rpglogger;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ModLoader;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import tsuteto.rpglogger.eventhandler.CraftingHandler;
import tsuteto.rpglogger.eventhandler.ForgeEventHandler;
import tsuteto.rpglogger.eventhandler.GameTickHandler;
import tsuteto.rpglogger.eventhandler.ModKeyHandler;
import tsuteto.rpglogger.eventhandler.PlayerTracker;
import tsuteto.rpglogger.logging.LogFileWriter;
import tsuteto.rpglogger.logging.RlLogManager;
import tsuteto.rpglogger.logging.RlMsgTranslate;
import tsuteto.rpglogger.param.ParamConverter;
import tsuteto.rpglogger.param.ParamEnderCrystal;
import tsuteto.rpglogger.param.ParamEntity;
import tsuteto.rpglogger.param.ParamPlayer;
import tsuteto.rpglogger.param.ParamWorld;
import tsuteto.rpglogger.settings.OptionsUsingFml;
import tsuteto.rpglogger.settings.RpgLoggerSettings;
import tsuteto.rpglogger.stat.StatEntityLivingBase;
import tsuteto.rpglogger.stat.StatEntityTameable;
import tsuteto.rpglogger.stat.StatGame;
import tsuteto.rpglogger.util.EntityNameUtil;
import tsuteto.rpglogger.util.Utilities;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * RPG Logger Implementation Class
 *
 * @author Tsuteto
 */
public class RpgLogger
{
    private static RpgLogger instance;

    /** Key binding to toggle the log window */
    public static final KeyBinding KB_TOGGLE_WINDOW = new KeyBinding("Toggle RPG logger window", Keyboard.KEY_L);

    public static Class dqmEntityTameable = null;

    public boolean loaded = false;
    public boolean isWindowEnabled = true;

    public RlLogManager logger;
    public RpgLoggerSettings settings;
    public RlMsgTranslate msgTrans;
    public LogFileWriter logFileWriter;
    public LogWindowRenderer windowRenderer;
    private LoggerTask loggerTask;

    public StatGame statGame = null;
    public ParamWorld paramWorld = null;
    public ParamPlayer paramPlayer = null;

    public List<ParamEntity> entityParams = new ArrayList<ParamEntity>();

    public List<Class> hostileModClassList = new ArrayList<Class>();

    // For a buggy behavior that the takenFromFurnace is called twice at once
    private boolean hasCalledSmelting = false;
    // To prevent popping same messages when crafting
    private boolean hasCalledCrafting = false;

    /** Waits for loading world */
    public int setupTick = 3;

    /**
     * Initializes RPG Logger
     *
     * @return my instance
     */
    public static void init()
    {
        instance = new RpgLogger();
    }

    /**
     * Returns the instance
     *
     * @return my instance
     */
    public static RpgLogger getInstance()
    {
        if (instance == null)
        {
            instance = new RpgLogger();
        }
        return instance;
    }

    public static boolean hasInstance()
    {
        return instance != null;
    }

    /**
     * Constructor
     */
    private RpgLogger() {}

    public void loadFmlSettings(Configuration conf)
    {
        this.settings = new RpgLoggerSettings();
        this.settings.setFmlSettings(new OptionsUsingFml(conf));
    }

    public void load()
    {
        Message.load();

        // Register event handlers
        GameRegistry.registerPlayerTracker(new PlayerTracker(this));
        TickRegistry.registerTickHandler(new GameTickHandler(this), Side.CLIENT);
        GameRegistry.registerCraftingHandler(new CraftingHandler(this));
        KeyBindingRegistry.registerKeyBinding(new ModKeyHandler(this));
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler(this));
    }

    public void prepare()
    {
        this.settings.loadApiSettings();

        this.logFileWriter = null;

        // Create and obtain translator
        Lang lang = Utilities.chooseLanguage(settings.getLanguage());
        RlMsgTranslate.createInstance(lang);
        this.msgTrans = RlMsgTranslate.getInstance();

        // Create renderer of the log window on the screen
        this.windowRenderer = new LogWindowRenderer();

        // Obtain log writer
        this.logger = new RlLogManager(this);

        // Create logger task
        this.loggerTask = new LoggerTask(this);
        //this.loggerTask.setName("RPG Logger thread");
        //this.loggerTask.setDaemon(true);

        // Start the task
        //this.loggerTask.start();

        // Attempt to obtain DQM tameable class
        try
        {
            this.dqmEntityTameable = Class.forName("DQMII.Client.MobEntityPet.DqmEntityTameable");
            systemLog("DQM tameable class found.");
        }
        catch (ClassNotFoundException e)
        {
            //systemLog("DQM tameable class not found...");
        }

        if (this.msgTrans.isCustomFileLoaded)
        {
            this.registerModEntities();
        }
    }

    /**
     * Registers entity types added by MODs
     */
    public void registerModEntities()
    {
        String vanillaEntityMapping =
                "0110000001" + // 0-9
                "1111111111" + // 10
                "1110000000" +
                "0000000000" +
                "1100000011" +
                "1111111111" + // 50
                "1111111000" +
                "0000000000" +
                "0000000000" +
                "1111111111" +
                "1000000000";  // 100
        List<Integer> jumpedEntityIds = new ArrayList<Integer>();
        jumpedEntityIds.add(120);
        jumpedEntityIds.add(200);

        Map entityEntryMap = null;
        try
        {
            // Access to the field IDtoClassMapping in EntityList
            entityEntryMap = (Map) ModLoader.getPrivateValue(net.minecraft.entity.EntityList.class, null, 2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (entityEntryMap == null)
        {
            return;
        }

        for (Entry entityEntry : (Entry[]) entityEntryMap.entrySet().toArray(new Entry[0]))
        {
            // System.out.println("entityEntryMap entry.getValue(): " + entry.getValue());
            Class entityClass = (Class) entityEntry.getValue();
            Integer id = (Integer) entityEntry.getKey();
            if (id != null
                    && (id < vanillaEntityMapping.length() && vanillaEntityMapping.charAt(id) == '0'
                    || id >= vanillaEntityMapping.length() && !jumpedEntityIds.contains(id)))
            {
                if (!EntityLivingBase.class.isAssignableFrom(entityClass))
                {
                    continue;
                }
                ModEntityAccessor accessor = ModEntityAccessor.registerEntityClass(entityClass);
                if (accessor != null)
                {
                    msgTrans.addNAKeyToCustomTable("entity." + EntityList.getStringFromID(id) + ".name");
                    for (String key : accessor.getStatList())
                    {
                        key = EntityList.getStringFromID(id) + "." + key;
                        msgTrans.addNAKeyToCustomTable(key + ".true");
                        msgTrans.addNAKeyToCustomTable(key + ".false");
                    }
                }
                systemLog("Registered mod entity: " + EntityList.getStringFromID(id));

            }
        }
        msgTrans.saveCustomLangFile();
    }

    public void onPlayerLoginWorld(EntityPlayer player)
    {
        Minecraft mc = Minecraft.getMinecraft();

        msgTrans.setLanguage(Utilities.chooseLanguage(settings.getLanguage()));

        paramWorld = new ParamWorld(player.worldObj, player, mc.currentScreen);
        paramPlayer = new ParamPlayer(player, paramWorld);

        this.logFileWriter = new LogFileWriter(paramWorld);
        this.statGame = new StatGame(paramPlayer, paramWorld, settings);
        this.loaded = false;

        if (!msgTrans.isCustomFileLoaded)
        {
            player.addChatMessage(msgTrans.getMsg("system.langfile.incorrect"));
        }

        logger.addMsgTranslate(player, "env.enterWorld", Color.yellow, new Object[] { paramWorld.worldName });
        RpgLogger.systemLog(String.format("Initialized for the world '%s'", paramWorld.worldName));
    }

    public void onPlayerTraveledDimension(EntityPlayer player)
    {
        int d = player.dimension;
        String dimLocalName = Utilities.getDimensionName(d, paramPlayer);
        if (d != 0)
        {
            logger.addMsgTranslate("env.traveledDimension", Color.green, dimLocalName);
        }
        else
        {
            logger.addMsgTranslate("env.traveledBack", Color.green, dimLocalName);
        }

        Minecraft mc = Minecraft.getMinecraft();

        paramWorld = new ParamWorld(player.worldObj, player, mc.currentScreen);
        paramPlayer = new ParamPlayer(player, paramWorld);
        this.statGame = new StatGame(paramPlayer, paramWorld, settings);
        setupTick = 3;
    }

    /**
     * Called on tick
     *
     * @param f
     * @param mc
     */
    public void onTick(float f, final Minecraft mc)
    {
        //long start = System.nanoTime();
        //if (!this.loaded) return;

        EntityPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(mc.thePlayer.username);
        if (player == null) return;

        World world = player.worldObj;

        paramWorld = new ParamWorld(world, mc.thePlayer, mc.currentScreen);
        paramPlayer = new ParamPlayer(mc.thePlayer, paramWorld);

        // ----------------------------------------
        // Collect entities in the world
        // ----------------------------------------
        //Entity[] worldEntityList = (Entity[]) world.loadedEntityList.toArray(new Entity[0]);
        List<Entity> worldEntityList = world.getEntitiesWithinAABB(EntityLivingBase.class, mc.thePlayer.boundingBox.expand(32.0D, 32.0D, 32.0D));

        List<ParamEntity> list = ParamConverter.convertAllEntities(worldEntityList, player, paramWorld);

        synchronized (entityParams)
        {
            this.entityParams = list;
        }

        // Game Settings
        if (statGame.statInMenu.checkVal(paramWorld.currentScreen == GuiIngameMenu.class) && !statGame.statInMenu.getVal())
        {
            boolean isLangChanged = msgTrans.lang != settings.getLanguage();
            // Reload lang file
            msgTrans.setLanguage(Utilities.chooseLanguage(settings.getLanguage()));
            if (msgTrans.islangFileUpdated && !isLangChanged)
            {
                mc.thePlayer.addChatMessage(msgTrans.getMsg("system.langfile.updated"));
            }
            if (!msgTrans.isCustomFileLoaded)
            {
                mc.thePlayer.addChatMessage(msgTrans.getMsg("system.langfile.incorrect"));
            }
        }

        if (!loaded)
        {
            this.statGame = new StatGame(paramPlayer, paramWorld, settings);
            loaded = true;
        }

        //loggerTask.kick();
        loggerTask.task();

        // Set flags
        hasCalledSmelting = false;
        hasCalledCrafting = false;
        //RpgLogger.systemLog(String.format("onTick time: %.3fms", (System.nanoTime() - start) / 1000000.0D));
    }

    /**
     * Releases the logger system
     */
    public void releaseLogger()
    {
        if (loaded)
        {
            loaded = false;
            logger.clearLogList();
            logFileWriter.closeLogfile();
            setupTick = 3;
            systemLog(String.format("Released from the world '%s'", paramWorld.worldName));
        }
    }

    /**
     * Called when the entity hurts
     *
     * @param entity
     * @param damagesource
     * @param i
     */
    public void onAttack(EntityLivingBase entity, DamageSource damagesource, float i)
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (!loaded || entity.worldObj.isRemote || mc.thePlayer == null)
        {
            return;
        }

        String damage = i >= 10000 ? msgTrans.getMsg("damage.amount.fatal")
                                   : msgTrans.getMsg("damage.amount.numeral", String.format("%.1f", i));
        String cause = msgTrans.getMsg("damage." + damagesource.damageType);

        ParamEntity param = ParamConverter.convertEntityToParam(entity, mc.thePlayer, paramWorld);
        ParamEntity damagesourceEntity = ParamConverter.convertEntityToParam(damagesource.getEntity(), mc.thePlayer, paramWorld);

        String entityName = EntityNameUtil.getMobName(param);

        Map<Integer, StatEntityLivingBase> mobEntities = statGame.getMobEntities();

        String dmgSrcEntityName, key;

        if (damagesourceEntity != null)
        {
            dmgSrcEntityName = EntityNameUtil.getMobName(damagesourceEntity);
            key = "mob".equals(damagesource.damageType) ? "tookDmgByMob" : "tookDmgBy";
        }
        else
        {
            dmgSrcEntityName = "";
            key = "tookDmg";
        }

        // The player gave damage
        if (damagesourceEntity != null && damagesourceEntity.entityId == paramPlayer.entityId)
        {
            if ("player".equals(damagesource.damageType))
            {
                key = "player.gaveDmg";
            }
            else
            {
                key = "player.gaveDmgBy";
            }
            logger.addMsgTranslate(key, Color.yellow, damage, cause, entityName);
            if (statGame.statBattle.isInBattle())
            {
                statGame.statBattle.isPlayerJoined = true;
            }
            return;
        }
        // The player took damage
        if (param.entityId == paramPlayer.entityId)
        {
            logger.addMsgTranslate("player." + key, Color.yellow, damage, cause, dmgSrcEntityName);

            // When the player is to die
//            if (isForgeEnabled && i >= entity.getHealth() || !isForgeEnabled && 0 >= entity.getHealth())
//            {
//                onDeath(entity, damagesource);
//            }
            return;
        }

        // Ally mobs took damage
        if (mobEntities.containsKey(param.entityId))
        {
            if (entityName == null)
            {
                return;
            }
            StatEntityLivingBase stat = mobEntities.get(param.entityId);
            if (stat.isTamed())
            {
                logger.addMsgTranslate("mob." + key, Color.pink, entityName, damage, cause, dmgSrcEntityName);
                return;
            }
        }

        /*
         * Giving damage
         */
        if (entityName == null)
        {
            return;
        }

        if (damagesourceEntity != null && mobEntities.containsKey(damagesourceEntity.entityId))
        {
            StatEntityLivingBase stat = mobEntities.get(damagesourceEntity.entityId);
            if ("mob".equals(damagesource.damageType))
            {
                key = "mob.gaveDmg";
            }
            else
            {
                key = "mob.gaveDmgBy";
            }
            if (stat.isTamed())
            {
                // Ally mobs gave damage
                logger.addMsgTranslate(key, Color.pink, dmgSrcEntityName, damage, cause, entityName);
                if (statGame.statBattle.isInBattle())
                {
                    statGame.statBattle.isAllyJoined = true;
                }
            }
            else
            {
                // Untamed mobs gave damage
                logger.addMsgTranslate(key, Color.white, dmgSrcEntityName, damage, cause, entityName);
            }
            return;
        }

        if (mobEntities.containsKey(param.entityId))
        {
            StatEntityLivingBase stat = mobEntities.get(param.entityId);
            if (!(stat instanceof StatEntityTameable && ((StatEntityTameable) stat).statTamed.getVal()))
            {
                // Mobs took damage
                logger.addMsgTranslate("mob." + key, Color.white, entityName, damage, cause, dmgSrcEntityName);
            }
            return;
        }
    }

    /**
     * Called when the player picks items up
     *
     * @param entityplayer
     * @param itemstack
     */
    public void onItemPickup(EntityPlayer entityplayer, ItemStack itemstack)
    {
        String itemName = EntityNameUtil.getItemName(itemstack);
        // ML and FML always sets 0, Forge sets an actual size to the stackSize.
        int stackSize = itemstack.stackSize == 0 ? 1 : itemstack.stackSize;

        Map<String, Integer> itemsPickedUp = statGame.statPlayer.itemsPickedUp;
        if (itemsPickedUp.containsKey(itemName))
        {
            itemsPickedUp.put(itemName, itemsPickedUp.get(itemName) + stackSize);
        }
        else
        {
            itemsPickedUp.put(itemName, stackSize);
            logger.addMsgTranslate("player.itemPickup", Color.yellow, EntityNameUtil.getItemName(itemstack));
        }
    }

    /**
     * Called when the player crafts items
     *
     * @param entityplayer
     * @param itemstack
     */
    public void takenFromCrafting(EntityPlayer entityplayer, ItemStack itemstack)
    {
        if (!hasCalledCrafting)
        {
            // Can't get how many items the player crafted...
            // When crafting 2 by 2: 2 -> 4 -> 6 -> 8...
            // When crafting with shift key: 0 -> 0 -> 0...
            logger.addMsgTranslate("player.crafted", Color.yellow,
                    EntityNameUtil.getItemName(itemstack),
                    itemstack.stackSize,
                    Utilities.chooseNounForm(itemstack.stackSize, msgTrans.getMsg("craft"), msgTrans.getMsg("crafts")));
            hasCalledCrafting = true;
        }
    }

    /**
     * Called when smelting
     *
     * @param entityplayer
     * @param itemstack
     */
    public void takenFromFurnace(EntityPlayer entityplayer, ItemStack itemstack)
    {
        if (!hasCalledSmelting)
        {
            Minecraft mc = ModLoader.getMinecraftInstance();
            logger.addMsgTranslate("player.smelted", Color.yellow,
                    EntityNameUtil.getItemName(itemstack),
                    itemstack.stackSize,
                    Utilities.chooseNounForm(itemstack.stackSize, msgTrans.getMsg("smelting"), msgTrans.getMsg("smeltings")));
            hasCalledSmelting = true;
        }
    }

    /**
     * Called on death
     *
     * @param entity
     * @param damagesource
     */
    public void onDeath(EntityLivingBase entity, DamageSource damagesource)
    {
        Minecraft mc = ModLoader.getMinecraftInstance();

        if (!loaded || entity.worldObj.isRemote || mc.thePlayer == null)
        {
            return;
        }

        ParamEntity param = ParamConverter.convertEntityToParam(entity, mc.thePlayer, paramWorld);
        ParamEntity damagesourceEntity = ParamConverter.convertEntityToParam(damagesource.getEntity(), mc.thePlayer, paramWorld);

        String entityName = EntityNameUtil.getMobName(param);

        Map<Integer, StatEntityLivingBase> mobEntities = statGame.getMobEntities();

        String dmgSrcEntityName, key;
        if (damagesourceEntity != null)
        {
            dmgSrcEntityName = EntityNameUtil.getMobName(damagesourceEntity);
            key = damagesource.damageType.equals("mob") ? "diedByMob" : "diedBy";
        }
        else
        {
            dmgSrcEntityName = msgTrans.getMsg("something");
            key = "died";
        }

        // The player died
        if (param.entityId == paramPlayer.entityId)
        {
            CombatTracker combatTracker = entity.func_110142_aN();
            logger.log(combatTracker.func_94546_b().toString(), Color.yellow);
            return;
        }

        if (damagesourceEntity != null && damagesourceEntity.entityId == paramPlayer.entityId)
        {
            if (param instanceof ParamEnderCrystal)
            {
                // The player broke Ender Crystal
                logger.addMsgTranslate("player.brokeEntity", Color.yellow, entityName);
            }
            else
            {
                // The player defeated mobs
                logger.addMsgTranslate("player.defeated", Color.yellow, entityName);
                if (statGame.statBattle.isInBattle())
                {
                    statGame.statBattle.killCount++;
                }
                if (param.entityClass == EntityDragon.class)
                {
                    logger.addMsgTranslate("congrats", Color.orange);
                }
            }
            return;
        }

        // Ally mobs defeated mobs
        if (damagesourceEntity != null && mobEntities.containsKey(damagesourceEntity.entityId))
        {
            StatEntityLivingBase stat = mobEntities.get(damagesourceEntity.entityId);
            if (stat.isTamed())
            {
                logger.addMsgTranslate("mob.defeated", Color.pink, dmgSrcEntityName, entityName);
                if (statGame.statBattle.isInBattle())
                {
                    statGame.statBattle.killCount++;
                }
                return;
            }
        }

        if (mobEntities.containsKey(param.entityId))
        {
            StatEntityLivingBase stat = mobEntities.get(param.entityId);
            if (stat.isTamed())
            {
                // Ally mobs killed
                logger.addMsgTranslate("death." + damagesource.damageType,
                        Color.pink, entityName, dmgSrcEntityName);
                return;
            }
            else
            {
                logger.addMsgTranslate("death." + damagesource.damageType,
                        Color.white, entityName, dmgSrcEntityName);
            }
        }
    }

    /**
     * Called when falling
     *
     * @param entity
     * @param f
     */
    public void onFall(EntityLivingBase entity, float f)
    {
        if (!loaded || entity.worldObj.isRemote)
        {
            return;
        }

        if (entity instanceof EntityPlayer && f > 5.0f)
        {
            logger.addMsgTranslate("player.fell", Color.yellow, (int) (f * 100F) / 100F);
        }
    }

    /**
     * Handles keyboard event
     *
     * @param event
     */
    public void keyboardEvent(KeyBinding event, Minecraft mc)
    {
        if (event.equals(KB_TOGGLE_WINDOW))
        {
            if (mc.thePlayer != null)
            {
                if (isWindowEnabled ^= true)
                {
                    mc.thePlayer.addChatMessage(msgTrans.getMsg("rpglogger.options.turnedWindow.on"));
                }
                else
                {
                    mc.thePlayer.addChatMessage(msgTrans.getMsg("rpglogger.options.turnedWindow.off"));
                }
            }
        }
    }

    public ParamEntity getParamByEntityId(int id)
    {
        for (ParamEntity p : this.entityParams)
        {
            if (p.entityId == id) return p;
        }
        return null;
    }

    /**
     * Prints system log
     *
     * @param msg
     */
    public static void systemLog(String msg, Object... obj)
    {
        FMLLog.log("RPGLogger", Level.INFO, msg, obj);
    }
}
