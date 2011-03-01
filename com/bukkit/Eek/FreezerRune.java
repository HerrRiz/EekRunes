/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.Location;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
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
        if (canFreeze(block))
        {
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
                    for(int z=-1;z<11;z++)
                    {
                        Block block2 = block.getRelative(x, y, z);
                        if (block2.getType()==Material.AIR&&block2.getFace(BlockFace.DOWN).getType()!=Material.AIR && block2.getFace(BlockFace.DOWN).getType()!=Material.SNOW)
                            block2.setType(Material.SNOW);
                    }
                }
            }
            return true;
        }
        return false;
    }
    private boolean canFreeze(Block block)
    {
        for(int i=-2;i<3;i++)
        {
            for(int j=-2;j<3;j++)
            {
                Block block2 = block.getRelative(i, 0, j);
                if (((i+2)%5==0) && ((i+2)%5==0))
                {
                    if (block2.getType()!=Material.REDSTONE_TORCH_ON) return false;
                }
                else if(((i + 2) % 5 == 0) || ((i + 2) % 5 == 0))
                {
                    if (block2.getType()!=Material.ICE) return false;
                }
                else if (i==0&&j==0)
                {
                    if (block2.getType()!=Material.GOLD_ORE) return false;
                }
                else if (block2.getType()!=Material.WATER) return false;
            }
        }
        return true;
    }
}
