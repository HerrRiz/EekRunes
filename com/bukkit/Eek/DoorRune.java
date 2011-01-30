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
public class DoorRune extends Rune
{
    private HashMap<Location,Boolean> doors;
    private HashMap<Location,Location> doorEndPoints;
    public DoorRune(EekRunes plugin)
    {
        super(plugin);
        loadState();
    }
    @Override
    public boolean runRuneRedstoneUsingBlock(BlockRedstoneEvent event)
    {
        Block block = event.getBlock();
        boolean powered = event.getNewCurrent()>0;
        //anything supplying power, except torch on wall, supplies power to surroundings
        if (!((block.getType()==Material.REDSTONE_TORCH_ON||block.getType()==Material.REDSTONE_TORCH_OFF)&&block.getData()!=5))
        {
            doorOpen(getDoorBlock(block.getFace(BlockFace.NORTH)),powered);
            doorOpen(getDoorBlock(block.getFace(BlockFace.EAST)),powered);
            doorOpen(getDoorBlock(block.getFace(BlockFace.SOUTH)),powered);
            doorOpen(getDoorBlock(block.getFace(BlockFace.WEST)),powered);
        }
        else
        {
            Block northPower = getDoorBlock(block.getFace(BlockFace.NORTH));
            Block eastPower = getDoorBlock(block.getFace(BlockFace.EAST));
            Block southPower = getDoorBlock(block.getFace(BlockFace.SOUTH));
            Block westPower = getDoorBlock(block.getFace(BlockFace.WEST));
            switch (block.getData())
            {
                case 1:
                    northPower=null;
                    break;
                case 2:
                    southPower=null;
                    break;
                case 3:
                    eastPower=null;
                    break;
                case 4:
                    westPower=null;
                    break;
            }
            doorOpen(northPower,powered);
            doorOpen(eastPower,powered);
            doorOpen(southPower,powered);
            doorOpen(westPower,powered);
        }
        if (block.getType()==Material.REDSTONE_TORCH_ON||block.getType()==Material.REDSTONE_TORCH_OFF)
        {
            if (block.getData()==5)
            {
                doorOpen(getDoorBlock(block.getFace(BlockFace.UP,2)),powered);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneRightClickUsingBlock(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        Block doorBlock=getDoorBlock(block);
        if (doorBlock!=null)
        {
            //clicking an existing door
            Boolean needsRedstone = doors.get(doorBlock.getLocation());
            if (!needsRedstone)
            {
                BlockFace dir = BlockFace.DOWN;
                int dist = 2;
                Location loc = doorBlock.getLocation();
                Location loc2=doorEndPoints.get(loc);
                if (loc2!=null)
                {
                    if(loc2.getBlockX()<loc.getBlockX())
                    {
                        dir = BlockFace.NORTH;
                    }
                    else if(loc2.getBlockX() > loc.getBlockX())
                    {
                        dir = BlockFace.SOUTH;
                    }
                    if(loc2.getBlockZ()<loc.getBlockZ())
                    {
                        dir = BlockFace.EAST;
                    }
                    if(loc2.getBlockZ()>loc.getBlockZ())
                    {
                        dir = BlockFace.WEST;
                    }
                }
                doorOpen(doorBlock,doorBlock.getFace(dir).getType()!=Material.AIR);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneRightClick(BlockRightClickEvent event)
    {
        Block block = event.getBlock();
        BlockFace LEFT=null;
        BlockFace RIGHT=null;
        BlockFace TOP=null;
        if (canDoor(block,BlockFace.NORTH,BlockFace.SOUTH)==BlockFace.UP)
        {
            LEFT = BlockFace.NORTH;
            RIGHT = BlockFace.SOUTH;
            TOP = BlockFace.UP;
        }
        else if (canDoor(block,BlockFace.EAST,BlockFace.WEST)==BlockFace.UP)
        {
            LEFT = BlockFace.EAST;
            RIGHT = BlockFace.WEST;
            TOP = BlockFace.UP;
        }
        else
        {
            TOP = canDoor(block,BlockFace.EAST,BlockFace.WEST);
            if(TOP!=null)
            {
                LEFT = BlockFace.WEST;
                RIGHT = BlockFace.EAST;
                switch (TOP)
                {
                    case EAST:
                        LEFT=BlockFace.NORTH;
                        RIGHT=BlockFace.SOUTH;
                        break;
                    case SOUTH:
                        LEFT=BlockFace.EAST;
                        RIGHT=BlockFace.WEST;
                        break;
                    case WEST:
                        LEFT=BlockFace.SOUTH;
                        RIGHT=BlockFace.NORTH;
                        break;
                }
            }
        }
        if (LEFT!=null)
        {
            boolean redstoned = false;
            if (event.getItemInHand()!=null)
            {
                redstoned = event.getItemInHand().getType() == Material.REDSTONE_TORCH_ON;
            }
            doors.put(block.getFace(TOP).getLocation(), redstoned);
            doorEndPoints.put(block.getFace(TOP).getLocation(), block.getFace(TOP,-1).getLocation());
            saveState();
            block.getFace(TOP).setType(block.getType());
            doorOpen(block.getFace(TOP),true);
            block.getFace(TOP).getFace(LEFT).setType(Material.AIR);
            block.getFace(TOP).getFace(RIGHT).setType(Material.AIR);
            block.getFace(TOP,-1).getFace(LEFT).setType(Material.AIR);
            block.getFace(TOP,-1).getFace(RIGHT).setType(Material.AIR);
            block.getFace(LEFT).setType(Material.AIR);
            block.getFace(RIGHT).setType(Material.AIR);
            String msg = (redstoned?" Note: This door can only be opened by redstone":"");
            event.getPlayer().sendMessage("Hidden Passage created."+msg);
            return true;
        }
        return false;
    }

    @Override
    public boolean runRuneDamageUsingBlock(BlockDamageEvent event)
    {
        Block block = event.getBlock();
        Block doorBlock = getDoorBlock(block);
        if (doorBlock!=null)
        {
            if (event.getDamageLevel()==BlockDamageLevel.BROKEN)
            {
                doorOpen(doorBlock,true);
                doors.remove(doorBlock.getLocation());
                saveState();
                event.setCancelled(true);
                event.getPlayer().sendMessage("Hidden Passage Destroyed");
            }
            return true;
        }
        return false;
    }

    public Block getDoorBlock(Block block)
    {
        if (doors.containsKey(block.getLocation())) return block;

        if (doors.containsKey(block.getFace(BlockFace.UP).getLocation()))
        {
            if(block.getFace(BlockFace.UP).getFace(getDownwards(block.getFace(BlockFace.UP).getLocation()))==block)return block.getFace(BlockFace.UP);
        }
        if (doors.containsKey(block.getFace(BlockFace.UP,2).getLocation()))
        {
            if(block.getFace(BlockFace.UP,2).getFace(getDownwards(block.getFace(BlockFace.UP,2).getLocation()),2)==block)return block.getFace(BlockFace.UP,2);
        }
        for(int i=0;i<2;i++)
        {
            if (doors.containsKey(block.getFace(BlockFace.NORTH,i+1).getLocation()))
            {
                if(block.getFace(BlockFace.NORTH,i+1).getFace(getDownwards(block.getFace(BlockFace.NORTH,i+1).getLocation()))==block)return block.getFace(BlockFace.NORTH,i+1);
            }
            if (doors.containsKey(block.getFace(BlockFace.EAST,i+1).getLocation()))
            {
                if(block.getFace(BlockFace.EAST,i+1).getFace(getDownwards(block.getFace(BlockFace.EAST,i+1).getLocation()))==block)return block.getFace(BlockFace.EAST,i+1);
            }
            if (doors.containsKey(block.getFace(BlockFace.SOUTH,i+1).getLocation()))
            {
                if(block.getFace(BlockFace.SOUTH,i+1).getFace(getDownwards(block.getFace(BlockFace.SOUTH,i+1).getLocation()))==block)return block.getFace(BlockFace.SOUTH,i+1);
            }
            if (doors.containsKey(block.getFace(BlockFace.WEST,i+1).getLocation()))
            {
                if(block.getFace(BlockFace.WEST,i+1).getFace(getDownwards(block.getFace(BlockFace.WEST,i+1).getLocation()))==block)return block.getFace(BlockFace.WEST,i+1);
            }
        }
        return null;
    }
    public void doorOpen(Block doorBlock, boolean open)
    {
        if (doorBlock!=null)
        {
            Location loc = doorBlock.getLocation();
            Location loc2 = doorEndPoints.get(loc);
            BlockFace dir = BlockFace.DOWN;
            int dist = 2;
            if (loc2!=null)
            {
                if(loc2.getBlockX()<loc.getBlockX())
                {
                    dir = BlockFace.NORTH;
                }
                else if(loc2.getBlockX() > loc.getBlockX())
                {
                    dir = BlockFace.SOUTH;
                }
                if(loc2.getBlockZ()<loc.getBlockZ())
                {
                    dir = BlockFace.EAST;
                }
                if(loc2.getBlockZ()>loc.getBlockZ())
                {
                    dir = BlockFace.WEST;
                }
                dist = Math.abs(loc2.getBlockZ()-loc.getBlockZ()) + Math.abs(loc2.getBlockX()-loc.getBlockX()) + Math.abs(loc2.getBlockY()-loc.getBlockY());
            }
            if (open)
            {
                //open door, so replace with air
                for(int i=0;i<dist;i++)
                {
                    doorBlock.getFace(dir,i+1).setType(Material.AIR);
                }
            }
            else
            {
                //close door, so replace with material
                for(int i=0;i<dist;i++)
                {
                    doorBlock.getFace(dir,i+1).setType(doorBlock.getType());
                }
            }
        }
    }
    private BlockFace canDoor(Block block, BlockFace LEFT, BlockFace RIGHT)
    {
        boolean doorVertical=
            (
                block.getFace(LEFT).getType()==Material.IRON_ORE
                &&
                block.getFace(RIGHT).getType()==Material.IRON_ORE
                &&
                block.getFace(BlockFace.UP).getType()==Material.COBBLESTONE
                &&
                block.getFace(BlockFace.UP).getFace(LEFT).getType()==Material.COBBLESTONE
                &&
                block.getFace(BlockFace.UP).getFace(RIGHT).getType()==Material.COBBLESTONE
                &&
                block.getFace(BlockFace.DOWN).getType()==Material.IRON_ORE
                &&
                block.getFace(BlockFace.DOWN).getFace(LEFT).getType()==Material.COBBLESTONE
                &&
                block.getFace(BlockFace.DOWN).getFace(RIGHT).getType()==Material.COBBLESTONE
            );
        if (doorVertical)return BlockFace.UP;
        if (canDoor(block,BlockFace.NORTH)) return BlockFace.NORTH;
        if (canDoor(block,BlockFace.EAST)) return BlockFace.EAST;
        if (canDoor(block,BlockFace.SOUTH)) return BlockFace.SOUTH;
        if (canDoor(block,BlockFace.WEST)) return BlockFace.WEST;
        return null;
    }
    private boolean canDoor(Block block, BlockFace face)
    {
        BlockFace LEFT = BlockFace.WEST;
        BlockFace RIGHT = BlockFace.EAST;
        BlockFace UP = BlockFace.NORTH;
        BlockFace DOWN = BlockFace.SOUTH;
        switch (face)
        {
            case EAST:
                UP=BlockFace.EAST;
                DOWN=BlockFace.WEST;
                LEFT=BlockFace.NORTH;
                RIGHT=BlockFace.SOUTH;
                break;
            case SOUTH:
                UP=BlockFace.SOUTH;
                DOWN=BlockFace.NORTH;
                LEFT=BlockFace.EAST;
                RIGHT=BlockFace.WEST;
                break;
            case WEST:
                UP=BlockFace.WEST;
                DOWN=BlockFace.EAST;
                LEFT=BlockFace.SOUTH;
                RIGHT=BlockFace.NORTH;
                break;
        }
        if
            (
                block.getFace(LEFT).getType()==Material.IRON_ORE
                &&
                block.getFace(RIGHT).getType()==Material.IRON_ORE
                &&
                block.getFace(UP).getType()==Material.COBBLESTONE
                &&
                block.getFace(UP).getFace(LEFT).getType()==Material.COBBLESTONE
                &&
                block.getFace(UP).getFace(RIGHT).getType()==Material.COBBLESTONE
                &&
                block.getFace(DOWN).getType()==Material.IRON_ORE
                &&
                block.getFace(DOWN).getFace(LEFT).getType()==Material.COBBLESTONE
                &&
                block.getFace(DOWN).getFace(RIGHT).getType()==Material.COBBLESTONE
            )
            return true;
        return false;
    }
    private void saveState()
    {
        try
        {
            FileOutputStream fout = new FileOutputStream(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"doors.ini");
            OutputStreamWriter osw = new OutputStreamWriter(fout);
            for(Location key: doors.keySet())
            {
                Location loc = doorEndPoints.get(key);
                if(loc==null)
                {
                    loc = new Location(loc.getWorld(),loc.getBlockX(),loc.getBlockY()-2,loc.getBlockZ());
                }
                osw.write(locToString(key)+"="+doors.get(key)+","+locToString(loc)+"\r\n");
            }
            osw.close();
        }
        catch(Exception e)
        {
            System.out.println("Could not save doors list - the location may be write protected");
            System.out.println(this.plugin.getDataFolder().getAbsolutePath()+File.separator+"doors.ini");
            e.printStackTrace();
        }
    }
    private void loadState()
    {
        doors = new HashMap<Location,Boolean>();
        doorEndPoints = new HashMap<Location,Location>();
        try
        {
            FileInputStream fin = new FileInputStream(plugin.getDataFolder().getAbsolutePath()+File.separator+"doors.ini");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String door = "";
            while ((door = br.readLine())!=null)
            {
                String[] split = door.split("=", 2);
                String[] split2 = split[1].split(",",2);
                doors.put(stringToLoc(split[0]), Boolean.parseBoolean(split2[0]));
                doorEndPoints.put(stringToLoc(split[0]), stringToLoc(split2[1]));
            }
            br.close();
        }
        catch(Exception e)
        {
            System.out.println("Could not load door state - starting new door list");
        }
    }
    private BlockFace getDownwards(Location loc)
    {
        BlockFace dir = BlockFace.DOWN;
        int dist = 2;
        Location loc2=doorEndPoints.get(loc);
        if (loc2!=null)
        {
            if(loc2.getBlockX()<loc.getBlockX())
            {
                dir = BlockFace.NORTH;
            }
            else if(loc2.getBlockX() > loc.getBlockX())
            {
                dir = BlockFace.SOUTH;
            }
            if(loc2.getBlockZ()<loc.getBlockZ())
            {
                dir = BlockFace.EAST;
            }
            if(loc2.getBlockZ()>loc.getBlockZ())
            {
                dir = BlockFace.WEST;
            }
        }
        return dir;
    }
}
