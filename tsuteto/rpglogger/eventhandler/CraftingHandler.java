package tsuteto.rpglogger.eventhandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import tsuteto.rpglogger.RpgLogger;
import cpw.mods.fml.common.ICraftingHandler;

public class CraftingHandler implements ICraftingHandler
{
    private RpgLogger rpgLogger;

    public CraftingHandler(RpgLogger rpgLogger)
    {
        this.rpgLogger = rpgLogger;
    }

    @Override
    public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix)
    {
        if (rpgLogger != null)
        {
            rpgLogger.takenFromCrafting(player, item);
        }
    }

    @Override
    public void onSmelting(EntityPlayer player, ItemStack item)
    {
        if (rpgLogger != null)
        {
            rpgLogger.takenFromFurnace(player, item);
        }
    }

}
