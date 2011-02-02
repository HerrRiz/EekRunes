/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.Location;
/**
 *
 * @author chris.stewart
 */
public abstract class Rune
{
    protected EekRunes plugin;
    private boolean isEnabled;

    public Rune(EekRunes plugin)
    {
        this.plugin=plugin;
    }

    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        System.out.println("Method not implemented by rune, returning false");
        return false;
    }

    public boolean runRuneDamage(BlockDamageEvent event)
    {
        return false;
    }
    /*
     * unused - no runes are created redstone
    public boolean runRuneRedstone(BlockRedstoneEvent event)
    {
        return false;
    }*/

    public boolean runRuneRightClickUsingBlock(BlockRightClickEvent event)
    {
        return false;
    }
    public boolean runRuneDamageUsingBlock(BlockDamageEvent event)
    {
        return false;
    }
    public boolean runRuneRedstoneUsingBlock(BlockRedstoneEvent event)
    {
        return false;
    }
    public boolean getIsEnabled()
    {
        return isEnabled;
    }
    public void setEnabled(boolean enabled)
    {
        isEnabled = enabled;
    }
    public Location stringToLoc(String loc)
    {
        String[] coords = loc.split(" ");
        for(org.bukkit.World world:plugin.getServer().getWorlds())
        {
            if(world.getId()==Long.parseLong(coords[0]))
            {
                Location loc1 = world.getBlockAt(Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3])).getLocation();
                return loc1;
            }
        }
        return null;
    }
    public String locToString(Location loc)
    {
        return loc.getWorld().getId()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
    }
}
