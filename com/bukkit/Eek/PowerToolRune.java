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
import org.bukkit.inventory.ItemStack;
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
                for(int i=-1;i<2;i++)
                {
                    for(int j=-1;j<2;j++)
                    {
                        for(int k=-1;k<2;k++)
                        {
                            if(Math.abs(i)+Math.abs(j)+Math.abs(k)>0)
                            {
                                Block block2 = block.getRelative(i, j, k);
                                ItemStack stack = plugin.getDrop(block2, event.getPlayer().getItemInHand());
                                ItemStack stackCmp = plugin.getDrop(block, event.getPlayer().getItemInHand());
                                if((stack!=null&&stackCmp!=null&&stack.getType()==stackCmp.getType())||block2.getType()==block.getType())
                                {
                                    block2.setType(Material.AIR);
                                    block2.getWorld().dropItemNaturally(block2.getLocation(), stack);
                                }
                            }
                        }
                    }
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
