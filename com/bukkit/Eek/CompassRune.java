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
public class CompassRune extends Rune
{
    public CompassRune(EekRunes plugin)
    {
        super(plugin);
    }
    @Override
    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        if (canCompass(block) && (event.getItemInHand().getType()==Material.AIR || !event.getItemInHand().getType().isBlock()))
        {
            block.setType(Material.AIR);
            block.getFace(BlockFace.NORTH_EAST).setType(Material.AIR);
            block.getFace(BlockFace.NORTH_WEST).setType(Material.AIR);
            block.getFace(BlockFace.NORTH).setType(block.getFace(BlockFace.SOUTH_EAST).getType());
            block.getFace(BlockFace.EAST).setType(block.getFace(BlockFace.SOUTH_EAST).getType());
            block.getFace(BlockFace.WEST).setType(block.getFace(BlockFace.SOUTH_EAST).getType());
            return true;
        }
        return false;
    }
    private boolean canCompass(Block block)
    {
        Material mat = block.getType();
        return
        (
            (this.plugin.getTier(mat)>0)
            &&
            block.getFace(BlockFace.NORTH_EAST).getType()==mat
            &&
            block.getFace(BlockFace.SOUTH_EAST).getType()==mat
            &&
            block.getFace(BlockFace.SOUTH_WEST).getType()==mat
            &&
            block.getFace(BlockFace.NORTH_WEST).getType()==mat
            &&
            block.getFace(BlockFace.NORTH).getType()==Material.AIR
            &&
            block.getFace(BlockFace.EAST).getType()==Material.AIR
            &&
            block.getFace(BlockFace.SOUTH).getType()==Material.AIR
            &&
            block.getFace(BlockFace.WEST).getType()==Material.AIR
        );
    }
}
