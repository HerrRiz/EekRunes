/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import java.util.HashMap;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import org.bukkit.block.BlockDamageLevel;
/**
 *
 * @author chris.stewart
 */
public class TeleportRune extends Rune
{
    private HashMap<Location,String> teleport_Locations;
    private WarpRune warper;
    public TeleportRune(EekRunes plugin, WarpRune warper)
    {
        super(plugin);
        this.warper=warper;
        loadState();
    }

    @Override
    public boolean runRuneRightClickUsingBlock(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        if(!teleport_Locations.containsKey(block.getLocation()))
        {
            if (block.getFace(BlockFace.UP).getType()==Material.AIR)
            {
                block = block.getFace(BlockFace.UP);
            }
        }
        if (teleport_Locations.containsKey(block.getLocation()))
        {
            Location loc = warper.getWarps().get(teleport_Locations.get(block.getLocation()));
            if (loc!=null)
            {
                Block toBlock = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                if (this.plugin.getTier(toBlock.getType())>this.plugin.getTier(block.getFace(BlockFace.NORTH).getType()))
                {
                    //key required
                    if (this.plugin.getTier(toBlock.getType())>=this.plugin.getTier(block.getType()))
                    {
                        //key accepted
                        loc.getWorld().dropItemNaturally(getSafeLocation(loc), new org.bukkit.inventory.ItemStack(block.getType()));
                        event.getPlayer().teleportTo(getSafeLocation(loc));
                        block.setType(Material.AIR);
                    }
                    else
                        event.getPlayer().sendMessage("The waypoint is not responding");
                }
                else
                {
                    loc = getSafeLocation(loc);
                    event.getPlayer().teleportTo(loc);
                }
            }
            else
                event.getPlayer().sendMessage("The waypoint is not responding");
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        if(!canTeleport(block)&&!canPersonalTeleport(block))
        {
            if (block.getFace(BlockFace.UP).getType()==Material.AIR)
            {
                block = block.getFace(BlockFace.UP);
            }
        }
        if (canTeleport(block))
        {
            String sig="";
            sig+=block.getFace(BlockFace.NORTH,2).getTypeId();
            sig+=","+block.getFace(BlockFace.EAST,2).getTypeId();
            sig+=","+block.getFace(BlockFace.SOUTH,2).getTypeId();
            sig+=","+block.getFace(BlockFace.WEST,2).getTypeId();

            if (!warper.getWarps().containsKey(sig))
            {
                event.getPlayer().sendMessage("There is no waypoint with this signature!");
            }
            else
            {
                event.getPlayer().sendMessage("Teleporter created!");
                teleport_Locations.put(block.getLocation(), sig);
                saveState();
            }
            return true;
        }
        else if (canPersonalTeleport(block))
        {
            String sig="";
            sig+=block.getFace(BlockFace.NORTH).getTypeId();
            sig+=","+block.getFace(BlockFace.EAST).getTypeId();
            sig+=","+block.getFace(BlockFace.SOUTH).getTypeId();
            sig+=","+block.getFace(BlockFace.WEST).getTypeId();
            Location loc = warper.getWarps().get(sig);
            if (loc!=null)
            {
                event.getPlayer().teleportTo(getSafeLocation(loc));
                block.setType(Material.AIR);
                block.getFace(BlockFace.NORTH).setType(Material.AIR);
                block.getFace(BlockFace.EAST).setType(Material.AIR);
                block.getFace(BlockFace.SOUTH).setType(Material.AIR);
                block.getFace(BlockFace.WEST).setType(Material.AIR);
                block.getFace(BlockFace.NORTH_EAST).setType(Material.AIR);
                block.getFace(BlockFace.SOUTH_EAST).setType(Material.AIR);
                block.getFace(BlockFace.SOUTH_WEST).setType(Material.AIR);
                block.getFace(BlockFace.NORTH_WEST).setType(Material.AIR);
            }
            else
                event.getPlayer().sendMessage("The waypoint is not responding");
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneDamageUsingBlock(BlockDamageEvent event)
    {
        Location[] teleports = getTeleports(event.getBlock());
        if (teleports!=null)
        {
            if (event.getDamageLevel()==BlockDamageLevel.BROKEN)
            {
                for(Location loc:teleports)
                {
                    teleport_Locations.remove(loc);
                    event.getPlayer().sendMessage("Teleport destroyed");
                }
            }
            return true;
        }
        return false;
    }
    private Location[] getTeleports(Block block)
    {
        Location[] locs = new Location[0];
        for(int i=-2;i<3;i++)
        {
            for(int j=-2;j<3;j++)
            {
                if (((i+1)%2==0) || ((j+1)%2==0))
                if (teleport_Locations.containsKey(block.getRelative(i, 0, j).getLocation()))
                {
                    locs = Arrays.copyOf(locs, locs.length+1);
                    locs[locs.length-1]=block.getRelative(i, 0, j).getLocation();
                }
            }
        }
        if (locs.length>0) return locs;
        return null;
    }
    private Location getSafeLocation(Location loc)
    {
        loc.getWorld().loadChunk(loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getChunk());
        boolean safe = false;
        while(!safe)
        {
            safe = true;
            if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getType()!=Material.AIR)
            {
                safe=false;
                loc = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getFace(BlockFace.UP).getLocation();
            }
            if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getFace(BlockFace.UP).getType()!=Material.AIR)
            {
                safe=false;
                loc = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getFace(BlockFace.UP).getLocation();
            }
        }
        safe = false;
        while(!safe)
        {
            safe = true;
            if (loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getFace(BlockFace.DOWN).getType()==Material.AIR)
            {
                safe = false;
                loc = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getFace(BlockFace.DOWN).getLocation();
            }
        }
        if ((int)loc.getX()==loc.getX())
        {
            loc.setX(loc.getX()+0.5);
        }
        if ((int)loc.getY()==loc.getY())
        {
            loc.setY(loc.getY()+0.5);
        }
        if ((int)loc.getZ()==loc.getZ())
        {
            loc.setZ(loc.getZ()+0.5);
        }
        plugin.getServer().getWorlds()[0].loadChunk(loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getChunk());
        return loc;
    }

    private boolean canTeleport(Block block)
    {
        Material mat = block.getFace(BlockFace.NORTH).getType();
        if (this.plugin.getTier(mat)<1) return false;
        return
            (
                block.getFace(BlockFace.NORTH).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_EAST).getType()==mat
                &&
                block.getFace(BlockFace.EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_WEST).getType()==mat
                &&
                block.getFace(BlockFace.WEST).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_WEST).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_WEST).getFace(BlockFace.NORTH).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_EAST).getFace(BlockFace.NORTH).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_EAST).getFace(BlockFace.EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_EAST).getFace(BlockFace.EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_WEST).getFace(BlockFace.SOUTH).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_EAST).getFace(BlockFace.SOUTH).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_WEST).getFace(BlockFace.WEST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_WEST).getFace(BlockFace.WEST).getType()==mat
                &&
                !(
                    block.getFace(BlockFace.NORTH,2).getType()==Material.AIR
                    &&
                    block.getFace(BlockFace.EAST,2).getType()==Material.AIR
                    &&
                    block.getFace(BlockFace.SOUTH,2).getType()==Material.AIR
                    &&
                    block.getFace(BlockFace.WEST,2).getType()==Material.AIR
                )
                &&
                block.getFace(BlockFace.NORTH,2).getType()!=mat
                &&
                block.getFace(BlockFace.EAST,2).getType()!=mat
                &&
                block.getFace(BlockFace.SOUTH,2).getType()!=mat
                &&
                block.getFace(BlockFace.WEST,2).getType()!=mat
                &&
                block.getFace(BlockFace.NORTH_EAST,2).getType()==Material.AIR
                &&
                block.getFace(BlockFace.SOUTH_EAST,2).getType()==Material.AIR
                &&
                block.getFace(BlockFace.SOUTH_WEST,2).getType()==Material.AIR
                &&
                block.getFace(BlockFace.NORTH_WEST,2).getType()==Material.AIR
            );
    }
    private boolean canPersonalTeleport(Block block)
    {
        return
        (
            block.getType()==Material.REDSTONE_WIRE
            &&
            block.getFace(BlockFace.NORTH_EAST).getType()==Material.REDSTONE_TORCH_ON
            &&
            block.getFace(BlockFace.SOUTH_EAST).getType()==Material.REDSTONE_TORCH_ON
            &&
            block.getFace(BlockFace.SOUTH_WEST).getType()==Material.REDSTONE_TORCH_ON
            &&
            block.getFace(BlockFace.NORTH_WEST).getType()==Material.REDSTONE_TORCH_ON
        );
    }

    private void saveState()
    {
        try
        {
            FileOutputStream fout = new FileOutputStream(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"teleports.ini");
            OutputStreamWriter osw = new OutputStreamWriter(fout);
            for(Location loc:teleport_Locations.keySet())
            {
                osw.write(locToString(loc)+"="+teleport_Locations.get(loc)+"\r\n");
            }
            osw.close();
        }
        catch(Exception e)
        {
            System.out.println("Could not save Teleport list - the location may be write protected");
            System.out.println(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"teleports.ini");
            e.printStackTrace();
        }
    }
    private void loadState()
    {
        teleport_Locations = new HashMap<Location,String>();
        try
        {
            FileInputStream fin = new FileInputStream(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"teleports.ini");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String waypoint = "";
            while ((waypoint = br.readLine())!=null)
            {
                String[] split = waypoint.split("=", 2);
                teleport_Locations.put(stringToLoc(split[0]), split[1]);
            }
            br.close();
        }
        catch(Exception e)
        {
            System.out.println("Could not load teleport state - starting new teleport list");
        }
    }
}