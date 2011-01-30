/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
/**
 *
 * @author UserXP
 */
public class OracleRune extends Rune
{
    public OracleRune(EekRunes plugin)
    {
        super(plugin);
    }
    @Override
    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        if
        (
            block.getFace(BlockFace.NORTH).getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.NORTH_EAST).getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.EAST).getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.SOUTH_EAST).getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.SOUTH).getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.SOUTH_WEST).getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.WEST).getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.NORTH_WEST).getType()==Material.REDSTONE_WIRE
        )
        {
            if(this.plugin.getTier(block.getType())>-1)
            {
                event.getPlayer().sendMessage("This block is tier " + this.plugin.getTier(block.getType()) + ".");
            }
            else
            {
                event.getPlayer().sendMessage("This block cannot be used in runes.");
            }
            block.getFace(BlockFace.NORTH).setType(Material.AIR);
            block.getFace(BlockFace.NORTH_EAST).setType(Material.AIR);
            block.getFace(BlockFace.EAST).setType(Material.AIR);
            block.getFace(BlockFace.SOUTH_EAST).setType(Material.AIR);
            block.getFace(BlockFace.SOUTH).setType(Material.AIR);
            block.getFace(BlockFace.SOUTH_WEST).setType(Material.AIR);
            block.getFace(BlockFace.WEST).setType(Material.AIR);
            block.getFace(BlockFace.NORTH_WEST).setType(Material.AIR);
            return true;
        }
        return false;
    }
}
