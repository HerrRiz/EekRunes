/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockRightClickEvent;
/**
 *
 * @author UserXP
 */
public class FreezerRune extends Rune
{
    public FreezerRune(EekRunes plugin)
    {
        super(plugin);
    }

    @Override
    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        System.out.println("Checking");
        if (canFreeze(block))
        {
            System.out.println("Freezing...");
            for(int i=-2;i<3;i++)
            {
                for(int j=-2;j<3;j++)
                {
                    block.getRelative(i, 0, j).setType(Material.AIR);
                }
            }
            for(int x=-10;x<11;x++)
            {
                for(int y=-10;y<11;y++)
                {
                    for(int z=-10;z<11;z++)
                    {
                        Block block2 = block.getRelative(x, y, z);
                        if (block2.getType()==Material.AIR&&block2.getFace(BlockFace.DOWN).getType()!=Material.AIR && block2.getFace(BlockFace.DOWN).getType()!=Material.SNOW && block2.getFace(BlockFace.DOWN).getType()!=Material.ICE)
                            block2.setType(Material.SNOW);
                        if ((block2.getType()==Material.STATIONARY_WATER||block2.getType()==Material.WATER) && block2.getFace(BlockFace.UP).getType()!=Material.ICE && !(block2.getFace(BlockFace.UP).getType()==Material.WATER || block2.getFace(BlockFace.UP).getType()==Material.STATIONARY_WATER))
                            block2.setType(Material.ICE);
                    }
                }
            }
            return true;
        }
        System.out.println("FAILED");
        return false;
    }
    private boolean canFreeze(Block block)
    {
        for(int i=-2;i<3;i++)
        {
            for(int j=-2;j<3;j++)
            {
                Block block2 = block.getRelative(i, 0, j);
                if (((i+2)%4==0) && ((j+2)%4==0))
                {
                    System.out.println("Checking redstone " + i + " " + j);
                    if (block2.getType()!=Material.REDSTONE_TORCH_ON) return false;
                }
                else if(((i + 2) % 4 == 0) || ((j + 2) % 4 == 0))
                {
                    System.out.println("Checking glass " + i + " " + j);
                    if (block2.getType()!=Material.GLASS) return false;
                }
                else if (i==0&&j==0)
                {
                    System.out.println("Checking glass " + i + " " + j);
                    if (block2.getType()!=Material.GOLD_ORE) return false;
                }
                else 
                {
                    System.out.println("Checking water " + i + " " + j);
                    if (block2.getType() != Material.STATIONARY_WATER) return false;
                }
            }
        }
        return true;
    }
}
