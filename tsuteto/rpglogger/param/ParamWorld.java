package tsuteto.rpglogger.param;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import tsuteto.rpglogger.util.Utilities;

/**
 * Represents parameters of the world
 *
 * @author Tsuteto
 *
 */
public class ParamWorld
{

    public boolean isDaytime;
    public boolean isRaining;
    public boolean isThundering;
    public int rainTime;

    public int biomeID;
    public int mapHeight;
    public float temperature;
    public float rainfall;

    public long worldTime;
    public String worldName;

    public Class currentScreen;

    public int worldObjHash;

    public ParamWorld(World world, EntityPlayer player, GuiScreen currentScreen)
    {
        int px = MathHelper.floor_double(player.posX);
        int py = MathHelper.floor_double(player.posY);
        int pz = MathHelper.floor_double(player.posZ);

        isDaytime = world.isDaytime();
        isRaining = world.isRaining();
        isThundering = world.isThundering();
        rainTime = world.getWorldInfo().getRainTime();

        Chunk chunk = world.getChunkFromBlockCoords(px, pz);
        BiomeGenBase biome = chunk.getBiomeGenForWorldCoords(px & 15, pz & 15, world.getWorldChunkManager());

        biomeID = biome.biomeID;

        worldTime = world.getWorldTime();
        worldName = world.getWorldInfo().getWorldName();

        mapHeight = chunk.getHeightValue(px & 15, pz & 15);

    	temperature = Utilities.getTemperature(world, px, pz);
    	rainfall = Utilities.getRainfall(world, px, pz);

        this.currentScreen = currentScreen == null ? null : currentScreen.getClass();

        worldObjHash = world.hashCode();
    }

    public BiomeGenBase getBiome()
    {
        return BiomeGenBase.biomeList[biomeID];
    }

}
