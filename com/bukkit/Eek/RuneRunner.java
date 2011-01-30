/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
/**
 *
 * @author chris.stewart
 */
public class RuneRunner extends BlockListener
{
    private Rune[] runes;
    public RuneRunner(Rune[] runes)
    {
        this.runes=runes;
    }

    @Override
    public void onBlockRightClick(BlockRightClickEvent event)
    {
        //if anything's already using the block do that
        for(Rune rune:runes)
        {
            if (rune.getIsEnabled())
            {
                if (rune.runRuneRightClickUsingBlock(event))
                {
                    //block in use, so end of rune
                    return;
                }
            }
        }
        //go through enabled runes, see if it's usable
        for(Rune rune:runes)
        {
            if (rune.getIsEnabled())
            {
                if (rune.runRuneRightClick(event))
                {
                    //one of the other runes already used the event, so end of rune
                    return;
                }
            }
        }
    }
    @Override
    public void onBlockDamage(BlockDamageEvent event)
    {
        //if anything's already using the block do that
        for(Rune rune:runes)
        {
            if (rune.getIsEnabled())
            {
                if (rune.runRuneDamageUsingBlock(event))
                {
                    //block in use, so end of rune
                    return;
                }
            }
        }
        //if anything's not using the block, see if anything special can be done (tools)
        for(Rune rune:runes)
        {
            if (rune.getIsEnabled())
            {
                if (rune.runRuneDamage(event))
                {
                    //block in use, so end of rune
                    return;
                }
            }
        }
    }
    @Override
    public void onBlockRedstoneChange(BlockFromToEvent event)
    {
        BlockRedstoneEvent redstoneEvent = (BlockRedstoneEvent) event;
        //if anything's already using the block do that
        for(Rune rune:runes)
        {
            if (rune.getIsEnabled())
            {
                if (rune.runRuneRedstoneUsingBlock(redstoneEvent))
                {
                    //block in use, so end of rune
                    return;
                }
            }
        }
    }
}
