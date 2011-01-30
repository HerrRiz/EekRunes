/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.bukkit.Location;
import java.io.File;
/**
 *
 * @author UserXP
 */
public class FormatConverter
{
    private HashMap<String,Location> waypoint_Signatures;
    private HashMap<Location,String> waypoint_Locations;
    private HashMap<Location,String> teleport_Locations;
    private HashMap<String,Boolean> options;
    private EekRunes plugin;
    public void convert(EekRunes plugin)
    {
        this.plugin=plugin;
        convertOptions();
        convertDoors();
        File iniFile = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"waypoints.ini");
        File iniFile2 = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"teleports.ini");
        //if the ini file doesn't exist, convert data from old format
        if (!iniFile.exists() && !iniFile2.exists())
        {
            MagicDatReader reader = new MagicDatReader();
            reader.convert(plugin);
        }
        convertWaypointData();
        convertTeleportData();
    }
    private void convertOptions()
    {
        File iniFile = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"settings.ini");
        //if the ini file doesn't exist, convert data from old format
        if (!iniFile.exists())
        {
            options = new HashMap<String,Boolean>();
            try
            {
                FileInputStream fin = new FileInputStream(plugin.getDataFolder().getAbsolutePath()+".options");
                BufferedReader br = new BufferedReader(new InputStreamReader(fin));
                String option = "";
                while ((option = br.readLine())!=null)
                {
                    String[] split = option.split("=", 2);
                    options.put(split[0], Boolean.parseBoolean(split[1]));
                }
                br.close();
                try
                {
                    File oldFile = new File(plugin.getDataFolder().getAbsolutePath()+".options");
                    oldFile.delete();
                }
                catch(Exception e)
                {
                    System.out.println(plugin.getDataFolder().getAbsolutePath()+".options could not be deleted.");
                    System.out.println("Please note that this file is no longer required");
                }
            }
            catch(Exception e)
            {
                System.out.println("Could not load option state - starting new option list");
                options = new HashMap<String,Boolean>();
                options.put("passages", Boolean.TRUE);
                options.put("teleports", Boolean.TRUE);
                options.put("compass", Boolean.TRUE);
                options.put("oracle", Boolean.TRUE);
            }
            try
            {
                FileOutputStream fout = new FileOutputStream(plugin.getDataFolder().getAbsolutePath()+File.separator+"settings.ini");
                OutputStreamWriter osw = new OutputStreamWriter(fout);
                for(String option: options.keySet())
                {
                    osw.write(option+"="+options.get(option)+"\r\n");
                }
                osw.close();
            }
            catch(Exception e)
            {
                System.out.println("Could not save option state - the location may be write protected");
                System.out.println(plugin.getDataFolder().getAbsolutePath()+File.separator+"settings.ini");
            }
        }
    }
    private void convertWaypointData()
    {
        waypoint_Signatures = new HashMap<String,Location>();
        waypoint_Locations = new HashMap<Location,String>();
        File iniFile = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"waypoints.ini");
        //if the ini file doesn't exist, convert data from old format
        if (!iniFile.exists())
        {
            try
            {
                FileInputStream fin = new FileInputStream(this.plugin.getDataFolder().getAbsolutePath()+".waypoints.list");
                ObjectInputStream ois = new ObjectInputStream(fin);
                HashMap<String,String> loading = (HashMap<String,String>)ois.readObject();
                for(String loc:loading.keySet())
                {
                    Location loc1 = stringToLoc(loc);
                    waypoint_Locations.put(loc1, loading.get(loc));
                    waypoint_Signatures.put(loading.get(loc),loc1);
                }
                ois.close();
                try
                {
                    File oldFile = new File(this.plugin.getDataFolder().getAbsolutePath()+".waypoints.list");
                    oldFile.delete();
                }
                catch(Exception e)
                {
                    System.out.println(this.plugin.getDataFolder().getAbsolutePath()+".waypoints.list could not be deleted.");
                    System.out.println("Please note that this file is no longer required");
                }
            }
            catch(Exception e)
            {
                System.out.println("Could not load waypoint state - starting new waypoint list");
            }
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
    }
    private void convertTeleportData()
    {
        teleport_Locations = new HashMap<Location,String>();
        File iniFile = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"teleports.ini");
        //if the ini file doesn't exist, convert data from old format
        if (!iniFile.exists())
        {
            try
            {
                FileInputStream fin = new FileInputStream(this.plugin.getDataFolder().getAbsolutePath()+".teleports.list");
                ObjectInputStream ois = new ObjectInputStream(fin);
                HashMap<String,String> loading = (HashMap<String,String>)ois.readObject();
                for(String loc:loading.keySet())
                {
                    Location loc1 = stringToLoc(loc);
                    teleport_Locations.put(loc1, loading.get(loc));
                }
                ois.close();
                try
                {
                    File oldFile = new File(this.plugin.getDataFolder().getAbsolutePath()+".teleports.list");
                    oldFile.delete();
                }
                catch(Exception e)
                {
                    System.out.println(this.plugin.getDataFolder().getAbsolutePath()+".teleports.list could not be deleted.");
                    System.out.println("Please note that this file is no longer required");
                }
            }
            catch(Exception e)
            {
                System.out.println("Could not load teleport state into converter - starting new teleport list");
            }
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
    }

    private void convertDoors()
    {
        HashMap<Location,Boolean> doors = new HashMap<Location,Boolean>();
        HashMap<Location,Location> doorEndPoints;
        File iniFile = new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"doors.ini");
        //if the ini file doesn't exist, convert data from old format
        if (!iniFile.exists())
        {
            doorEndPoints = new HashMap<Location,Location>();
            try
            {
                FileInputStream fin = new FileInputStream(this.plugin.getDataFolder().getAbsolutePath()+".doors.list");
                ObjectInputStream ois = new ObjectInputStream(fin);
                HashMap<String,Boolean> doors2 = (HashMap<String,Boolean>)ois.readObject();
                ois.close();
                for(String loc:doors2.keySet())
                {
                    Location loc1 = readNativeLocString(loc);
                    doors.put(loc1, doors2.get(loc));
                    doorEndPoints.put(loc1, new Location(loc1.getWorld(),loc1.getBlockX(),loc1.getBlockY()-2,loc1.getBlockZ()));
                }
                try
                {
                    File oldFile = new File(this.plugin.getDataFolder().getAbsolutePath()+".doors.list");
                    oldFile.delete();
                }
                catch(Exception e)
                {
                    System.out.println(this.plugin.getDataFolder().getAbsolutePath()+".doors.list could not be deleted.");
                    System.out.println("Please note that this file is no longer required");
                }
            }
            catch(Exception e)
            {
                 System.out.println("Could not load door state - starting new door list");
            }
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
    }
    public String locToString(Location loc)
    {
        return loc.getWorld().getId()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
    }
    public Location stringToLoc(String loc)
    {
        String[] coords = loc.split(" ");
        for(org.bukkit.World world:plugin.getServer().getWorlds())
        {
            if(world.getId()==Long.parseLong(coords[0]))
            {
                Location loc1 = world.getBlockAt(Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3])).getLocation();
                return loc1;
            }
        }
        return null;
    }
    private Location readNativeLocString(String loc)
    {
        String[] split1 = loc.split("world=",2);
        String[] split2 = split1[1].split("x=",2);
        String[] split3 = split2[1].split("y=",2);
        String[] split4 = split3[1].split("z=",2);
        String[] split5 = split4[1].split("pitch=",2);
        String world=split2[0];
        Double xVal=Double.parseDouble(split3[0]);
        Double yVal=Double.parseDouble(split4[0]);
        Double zVal=Double.parseDouble(split5[0]);
        for(org.bukkit.World worldSearch:plugin.getServer().getWorlds())
        {
            if(worldSearch.toString().equals(world))
            {
                Location loc1 = worldSearch.getBlockAt(xVal.intValue(), yVal.intValue(), zVal.intValue()).getLocation();
                return loc1;
            }
        }
        return plugin.getServer().getWorlds()[0].getBlockAt(xVal.intValue(), yVal.intValue(), zVal.intValue()).getLocation();
    }
}
