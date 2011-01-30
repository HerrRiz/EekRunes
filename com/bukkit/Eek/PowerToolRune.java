/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.HashMap;
/**
 *
 * @author UserXP
 */
public class PowerToolRune extends Rune
{
    private HashMap<String,Integer> powered;
    public PowerToolRune(EekRunes plugin)
    {
        super(plugin);
        powered = new HashMap<String,Integer>();
    }
    @Override
    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        if (block.getFace(BlockFace.UP).getType()==Material.AIR)
            block = block.getFace(BlockFace.UP);
        if (canPower(block,event.getPlayer()))
        {
            powered.put(event.getPlayer().getName()+"_"+event.getItemInHand().getType().name(), 7*plugin.getTier(block.getFace(BlockFace.NORTH_EAST).getType()));
            event.getPlayer().sendMessage("You are now holding a PowerTool");
            for(int i=0;i<5;i++)
            {
                for(int j=0;j<5;j++)
                {
                    block.getRelative(i-2, 0, j-2).setType(Material.AIR);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneDamage(BlockDamageEvent event)
    {
        String id = event.getPlayer().getName()+"_"+event.getPlayer().getItemInHand().getType().name();
        if (powered.containsKey(id))
        {
            if (event.getDamageLevel()==BlockDamageLevel.BROKEN)
            {
                powered.put(id, powered.get(id)-1);
                Block block = event.getBlock();
                Material mat = block.getType();
                if (block.getFace(BlockFace.UP).getType()==mat)
                {
                    block.getFace(BlockFace.UP).setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getFace(BlockFace.UP).getLocation(), block.getState().getData().toItemStack());
                }
                if (block.getFace(BlockFace.DOWN).getType()==mat)
                {
                    block.getFace(BlockFace.DOWN).setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getFace(BlockFace.DOWN).getLocation(), block.getState().getData().toItemStack());
                }
                if (block.getFace(BlockFace.NORTH).getType()==mat)
                {
                    block.getFace(BlockFace.NORTH).setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getFace(BlockFace.NORTH).getLocation(), block.getState().getData().toItemStack());
                }
                if (block.getFace(BlockFace.EAST).getType()==mat)
                {
                    block.getFace(BlockFace.EAST).setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getFace(BlockFace.EAST).getLocation(), block.getState().getData().toItemStack());
                }
                if (block.getFace(BlockFace.SOUTH).getType()==mat)
                {
                    block.getFace(BlockFace.SOUTH).setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getFace(BlockFace.SOUTH).getLocation(), block.getState().getData().toItemStack());
                }
                if (block.getFace(BlockFace.WEST).getType()==mat)
                {
                    block.getFace(BlockFace.WEST).setType(Material.AIR);
                    block.getWorld().dropItemNaturally(block.getFace(BlockFace.WEST).getLocation(), block.getState().getData().toItemStack());
                }
                if (powered.get(id)==0)
                {
                    powered.remove(id);
                    plugin.delTool(event.getPlayer(), event.getPlayer().getItemInHand().getType());
                    event.getPlayer().sendMessage("You can no longer feel power emanating from your tool.");
                }
            }
            return true;
        }
        return false;
    }

    public boolean canPower(Block block, Player player)
    {
        Material stack = player.getItemInHand().getType();
        Material mat = block.getFace(BlockFace.NORTH_EAST).getType();
        return
            (
                !plugin.poweredTool(player, stack)
                &&
                (
                    stack==Material.IRON_PICKAXE
                    ||
                    stack==Material.DIAMOND_PICKAXE
                    ||
                    stack==Material.IRON_SPADE
                    ||
                    stack==Material.DIAMOND_SPADE
                )
                &&
                block.getFace(BlockFace.NORTH).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.EAST).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.SOUTH).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.WEST).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.NORTH_EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_WEST).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_WEST).getType()==mat
                &&
                plugin.getTier(mat)>0
                &&
                block.getFace(BlockFace.NORTH,2).getType()==Material.REDSTONE_TORCH_ON
                &&
                block.getFace(BlockFace.EAST,2).getType()==Material.REDSTONE_TORCH_ON
                &&
                block.getFace(BlockFace.SOUTH,2).getType()==Material.REDSTONE_TORCH_ON
                &&
                block.getFace(BlockFace.WEST,2).getType()==Material.REDSTONE_TORCH_ON
                &&
                block.getFace(BlockFace.NORTH_EAST,2).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.SOUTH_EAST,2).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.SOUTH_WEST,2).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.NORTH_WEST,2).getType()==Material.REDSTONE_WIRE
                &&
                block.getFace(BlockFace.NORTH,2).getFace(BlockFace.WEST).getType()==Material.AIR
                &&
                block.getFace(BlockFace.NORTH,2).getFace(BlockFace.EAST).getType()==Material.AIR
                &&
                block.getFace(BlockFace.EAST,2).getFace(BlockFace.NORTH).getType()==Material.AIR
                &&
                block.getFace(BlockFace.EAST,2).getFace(BlockFace.SOUTH).getType()==Material.AIR
                &&
                block.getFace(BlockFace.SOUTH,2).getFace(BlockFace.EAST).getType()==Material.AIR
                &&
                block.getFace(BlockFace.SOUTH,2).getFace(BlockFace.WEST).getType()==Material.AIR
                &&
                block.getFace(BlockFace.WEST,2).getFace(BlockFace.NORTH).getType()==Material.AIR
                &&
                block.getFace(BlockFace.WEST,2).getFace(BlockFace.SOUTH).getType()==Material.AIR
            );
    }
}
