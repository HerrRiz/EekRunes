/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
/**
 *
 * @author UserXP
 */
public class MagicDatReader
{
    ArrayList<Integer[]> teleports = new ArrayList<Integer[]>();
    ArrayList<Integer[]> waypoints = new ArrayList<Integer[]>();
    private final int WPL = 0;
    private final int TPL = 1;
    private HashMap<Location,String> waypoint_Locations;
    private HashMap<Location,String> teleport_Locations;
    private EekRunes plugin;
    public void convert(EekRunes plugin)
    {
        this.plugin=plugin;
        try
        {
            ArrayList<ArrayList<Integer[]>> data = new ArrayList<ArrayList<Integer[]>>();
            FileInputStream fin = new FileInputStream(plugin.getDataFolder().getAbsolutePath()+File.separator+"magic.dat");
            InputStreamReader isr = new InputStreamReader(fin);
            int first = readint(isr);

            // einlesen der ersten Zahl: -2

            if (first != -2)
            {
                throw new RuntimeException("Die Datei ist keine magic.dat!");

            }
            int count_md = readint(isr);

            for (int i_md = 0; i_md < count_md; i_md++)
            {
                data.add(new ArrayList<Integer[]>());

                // die anzahl an Elementen, in der Liste
                int count = readint(isr);

                for (int i = 0; i < count; i++)
                {
                    int count_entry = readint(isr);
                    data.get(i_md).add(new Integer[count_entry]);


                    for (int iE = 0; iE < count_entry; iE++)
                    {
                        data.get(i_md).get(i)[iE] = readint(isr);
                    }

                }
            }
            isr.close();
            generateData(data);
            saveWarpState();
            saveTeleState();
        }
        catch(Exception e)
        {
            System.out.println("No magic.dat file to load.");
        }
    }
    private int readint(InputStreamReader stream)
    {
        try
        {
        int byteA = stream.read();
        int byteB = stream.read();
        int byteC = stream.read();
        int byteD = stream.read();


        int result = byteD + 256 * (byteC + 256 * (byteB + 256 * byteA));

        return result;

        }
        catch(Exception e){}
        return 0;
    }
    private void generateData(ArrayList<ArrayList<Integer[]>> data)
    {
        for(Integer[] dat:data.get(WPL))
        {
            waypoint_Locations.put(plugin.getServer().getWorlds()[0].getBlockAt(dat[2], dat[3], dat[4]).getLocation(), deHash(dat[0]));
        }
        for(Integer[] dat:data.get(TPL))
        {
            teleport_Locations.put(plugin.getServer().getWorlds()[0].getBlockAt(dat[0], dat[1], dat[2]).getLocation(), deHash(dat[3]));
        }
    }
    private String deHash(int hash)
    {
        int hashM1 = hash - ((hash << 8) >> 8);
        int M1 = hashM1 >> 24;


        int hashM2 = hash - ((hash << 16) >> 16) - hashM1;
        int M2 = hashM2 >> 16;

        int hashM3 = hash - ((hash << 24) >> 24) - hashM1 - hashM2;
        int M3 = hashM3 >> 8;

        int M4 = hash - hashM1 - hashM2 - hashM3;

        return ""+M4+","+M1+","+M2+","+M3;
    }
    private void saveWarpState()
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
    private void saveTeleState()
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
    public String locToString(Location loc)
    {
        return loc.getWorld().getId()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
    }
}
