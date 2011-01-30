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
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.util.Arrays;
import org.bukkit.block.BlockDamageLevel;
/**
 *
 * @author chris.stewart
 */
public class WarpRune extends Rune
{
    private HashMap<String,Location> waypoint_Signatures;
    private HashMap<Location,String> waypoint_Locations;
    public WarpRune(EekRunes plugin)
    {
        super(plugin);
        loadState();
    }
    @Override
    public boolean runRuneRightClickUsingBlock(BlockRightClickEvent event)
    {
        if (waypoint_Locations.containsKey(event.getBlock().getLocation()))
        {
            event.getPlayer().sendMessage("Waypoint already activated");
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        if (canWaypoint(block))
        {
            String sig="";
            sig+=block.getFace(BlockFace.NORTH).getTypeId();
            sig+=","+block.getFace(BlockFace.EAST).getTypeId();
            sig+=","+block.getFace(BlockFace.SOUTH).getTypeId();
            sig+=","+block.getFace(BlockFace.WEST).getTypeId();

            if (waypoint_Signatures.containsKey(sig))
            {
                event.getPlayer().sendMessage("This waypoint signature is already in use!");
            }
            else
            {
                event.getPlayer().sendMessage("Waypoint created!");
                waypoint_Signatures.put(sig, block.getLocation());
                waypoint_Locations.put(block.getLocation(), sig);
                saveState();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneDamageUsingBlock(BlockDamageEvent event)
    {
        Location[] waypoints = getWaypoints(event.getBlock());
        if (waypoints!=null)
        {
            if (event.getDamageLevel()==BlockDamageLevel.BROKEN)
            {
                for(Location loc:waypoints)
                {
                    String sig = waypoint_Locations.get(loc);
                    waypoint_Locations.remove(loc);
                    waypoint_Signatures.remove(sig);
                    event.getPlayer().sendMessage("Waypoint destroyed");
                }
            }
            return true;
        }
        return false;
    }

    private boolean canWaypoint(Block block)
    {
        Material mat = block.getType();
        if (this.plugin.getTier(mat)<1) return false;
        return
            (
                block.getFace(BlockFace.NORTH_EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_EAST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH_WEST).getType()==mat
                &&
                block.getFace(BlockFace.NORTH_WEST).getType()==mat
                &&
                block.getFace(BlockFace.NORTH,2).getType()==mat
                &&
                block.getFace(BlockFace.NORTH,2).getFace(BlockFace.WEST).getType()==mat
                &&
                block.getFace(BlockFace.NORTH,2).getFace(BlockFace.EAST).getType()==mat
                &&
                block.getFace(BlockFace.EAST,2).getType()==mat
                &&
                block.getFace(BlockFace.EAST,2).getFace(BlockFace.NORTH).getType()==mat
                &&
                block.getFace(BlockFace.EAST,2).getFace(BlockFace.SOUTH).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH,2).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH,2).getFace(BlockFace.WEST).getType()==mat
                &&
                block.getFace(BlockFace.SOUTH,2).getFace(BlockFace.EAST).getType()==mat
                &&
                block.getFace(BlockFace.WEST,2).getType()==mat
                &&
                block.getFace(BlockFace.WEST,2).getFace(BlockFace.NORTH).getType()==mat
                &&
                block.getFace(BlockFace.WEST,2).getFace(BlockFace.SOUTH).getType()==mat
                &&
                !(
                    block.getFace(BlockFace.NORTH).getType()==Material.AIR
                    &&
                    block.getFace(BlockFace.EAST).getType()==Material.AIR
                    &&
                    block.getFace(BlockFace.SOUTH).getType()==Material.AIR
                    &&
                    block.getFace(BlockFace.SOUTH).getType()==Material.AIR
                )
                &&
                block.getFace(BlockFace.NORTH).getType()!=mat
                &&
                block.getFace(BlockFace.EAST).getType()!=mat
                &&
                block.getFace(BlockFace.SOUTH).getType()!=mat
                &&
                block.getFace(BlockFace.WEST).getType()!=mat
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

    private Location[] getWaypoints(Block block)
    {
        Location[] locs = new Location[0];
        for(int i=-2;i<3;i++)
        {
            for(int j=-2;j<3;j++)
            {
                if (!((((i+2)%4==0) && ((j+2)%4==0)) || (((i+1)%2==0) && j==0) || (((j+1)%2==0) && i==0)))
                if (waypoint_Locations.containsKey(block.getRelative(i, 0, j).getLocation()))
                {
                    locs = Arrays.copyOf(locs, locs.length+1);
                    locs[locs.length-1]=block.getRelative(i, 0, j).getLocation();
                }
            }
        }
        if (waypoint_Locations.containsKey(block.getLocation()))
        {
            locs = Arrays.copyOf(locs, locs.length+1);
            locs[locs.length-1]=block.getLocation();
        }
        if (locs.length>0) return locs;
        return null;
    }

    private void saveState()
    {
        try
        {
            FileOutputStream fout = new FileOutputStream(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"waypoints.ini");
            OutputStreamWriter osw = new OutputStreamWriter(fout);
            for(Location loc:waypoint_Locations.keySet())
            {
                osw.write(locToString(loc)+"="+waypoint_Locations.get(loc)+"\r\n");
            }
            osw.close();
        }
        catch(Exception e)
        {
            System.out.println("Could not save Waypoint list - the location may be write protected");
            System.out.println(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"waypoints.ini");
        }
    }
    private void loadState()
    {
        waypoint_Signatures = new HashMap<String,Location>();
        waypoint_Locations = new HashMap<Location,String>();
        try
        {
            FileInputStream fin = new FileInputStream(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"waypoints.ini");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String waypoint = "";
            while ((waypoint = br.readLine())!=null)
            {
                String[] split = waypoint.split("=", 2);
                waypoint_Locations.put(stringToLoc(split[0]), split[1]);
                waypoint_Signatures.put(split[1], stringToLoc(split[0]));
            }
            br.close();
        }
        catch(Exception e)
        {
            System.out.println("Could not load waypoint state - starting new waypoint list");
        }
    }
    public HashMap<String,Location> getWarps()
    {
        return waypoint_Signatures;
    }
}
