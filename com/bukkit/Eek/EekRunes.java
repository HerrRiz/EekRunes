/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bukkit.Eek;
import java.io.File;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import org.bukkit.block.BlockFace;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import java.util.HashSet;
import java.util.Random;
/**
 *
 * @author UserXP
 */
public class EekRunes extends JavaPlugin {
    private RuneRunner runeRunner;
    private WarpRune warpRune;
    private TeleportRune teleportRune;
    private CompassRune compassRune;
    private OracleRune oracleRune;
    private DoorRune doorRune;
    private PowerToolRune powerToolRune;
    private FreezerRune freezerRune;
    private ChronoTriggerRune chronoTriggerRune;
    private HashMap<Integer,Integer> tiers;
    private HashMap<String,Boolean> options;
    private Random rand = new Random();

    private HashSet<String> tool;

    public EekRunes(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        getDataFolder().mkdir();
        FormatConverter conv = new FormatConverter();
        conv.convert(this);
        warpRune = new WarpRune(this);
        teleportRune = new TeleportRune(this,warpRune);
        compassRune = new CompassRune(this);
        oracleRune = new OracleRune(this);
        doorRune = new DoorRune(this);
        powerToolRune = new PowerToolRune(this);
        freezerRune = new FreezerRune(this);
        chronoTriggerRune = new ChronoTriggerRune(this);
        Rune[] runes = {warpRune,teleportRune,compassRune,oracleRune,doorRune,powerToolRune,freezerRune,chronoTriggerRune};
        runeRunner = new RuneRunner(runes);
        tool = new HashSet<String>();
        loadState();
        registerEvents();
        
        tiers = new HashMap<Integer,Integer>();
        tiers.put(Material.AIR.getId(), 0);
        tiers.put(Material.DIRT.getId(), 0);
        tiers.put(Material.GRASS.getId(), 0);
        tiers.put(Material.STONE.getId(), 0);

        tiers.put(Material.COBBLESTONE.getId(), 1);
        tiers.put(Material.YELLOW_FLOWER.getId(), 1);
        tiers.put(Material.RED_ROSE.getId(), 1);
        tiers.put(Material.GRAVEL.getId(), 1);
        tiers.put(Material.LOG.getId(), 1);
        tiers.put(Material.SAND.getId(), 1);
        tiers.put(Material.WOOD.getId(), 1);
        tiers.put(Material.FIRE.getId(), 1);
        tiers.put(Material.NETHERRACK.getId(), 1);
        tiers.put(Material.SOUL_SAND.getId(), 1);
        tiers.put(Material.WOOL.getId(), 1);
        tiers.put(Material.SANDSTONE.getId(), 1);

        tiers.put(Material.CHEST.getId(), 2);
        tiers.put(Material.SOIL.getId(), 2);
        tiers.put(Material.FURNACE.getId(), 2);
        tiers.put(Material.GLASS.getId(), 2);
        tiers.put(Material.LAVA.getId(), 2);
        tiers.put(Material.LEAVES.getId(), 2);
        tiers.put(Material.RED_MUSHROOM.getId(), 2);
        tiers.put(Material.BROWN_MUSHROOM.getId(), 2);
        tiers.put(Material.STEP.getId(), 2);
        tiers.put(Material.COBBLESTONE_STAIRS.getId(), 2);
        tiers.put(Material.WOOD_STAIRS.getId(), 2);
        tiers.put(Material.WATER.getId(), 2);
        tiers.put(Material.WORKBENCH.getId(), 2);
        tiers.put(Material.GLOWSTONE.getId(), 2);
        tiers.put(Material.NOTE_BLOCK.getId(), 2);

        tiers.put(Material.BRICK.getId(), 3);
        tiers.put(Material.CACTUS.getId(), 3);
        tiers.put(Material.CLAY.getId(), 3);
        tiers.put(Material.IRON_ORE.getId(), 3);
        tiers.put(Material.REDSTONE_ORE.getId(), 3);
        tiers.put(Material.SUGAR_CANE_BLOCK.getId(), 3);
        tiers.put(Material.TORCH.getId(), 3);
        tiers.put(Material.LAPIS_BLOCK.getId(), 3);
        tiers.put(Material.LAPIS_ORE.getId(), 3);
        tiers.put(Material.CAKE_BLOCK.getId(), 3);
        tiers.put(Material.REDSTONE_WIRE.getId(), 3);

        tiers.put(Material.BEDROCK.getId(), 4);
        tiers.put(Material.BOOKSHELF.getId(), 4);
        tiers.put(Material.GOLD_ORE.getId(), 4);
        tiers.put(Material.IRON_BLOCK.getId(), 4);
        tiers.put(Material.MOSSY_COBBLESTONE.getId(), 4);
        tiers.put(Material.TNT.getId(), 4);

        tiers.put(Material.GOLD_BLOCK.getId(), 5);
        tiers.put(Material.JUKEBOX.getId(), 5);
        tiers.put(Material.OBSIDIAN.getId(), 5);

        tiers.put(Material.DIAMOND_BLOCK.getId(), 6);
    }

    public void onDisable() {
        System.out.println("EekRunes unloaded!");
    }

    public void onEnable() {
        System.out.println("EekRunes "+this.getDescription().getVersion()+" loaded!");
    }

    private void registerEvents() {
        if (options.get("passages")!=Boolean.TRUE)
            options.put("passages", Boolean.FALSE);

        if (options.get("teleports")!=Boolean.TRUE)
            options.put("teleports", Boolean.FALSE);

        if (options.get("compass")!=Boolean.TRUE)
            options.put("compass", Boolean.FALSE);

        if (options.get("oracle")!=Boolean.TRUE)
            options.put("oracle", Boolean.FALSE);

        if (options.get("powertool")!=Boolean.TRUE)
            options.put("powertool", Boolean.FALSE);

        if (options.get("freezer")!=Boolean.TRUE)
            options.put("freezer", Boolean.FALSE);

        if (options.get("chrono")!=Boolean.TRUE)
            options.put("chrono", Boolean.FALSE);
        
        saveState();
        doorRune.setEnabled(options.get("passages")==Boolean.TRUE);
        teleportRune.setEnabled(options.get("teleports")==Boolean.TRUE);
        warpRune.setEnabled(options.get("teleports")==Boolean.TRUE);
        compassRune.setEnabled(options.get("compass")==Boolean.TRUE);
        oracleRune.setEnabled(options.get("oracle")==Boolean.TRUE);
        powerToolRune.setEnabled(options.get("powertool")==Boolean.TRUE);
        System.out.println("freezer enabled? " + options.get("freezer"));
        freezerRune.setEnabled(options.get("freezer")==Boolean.TRUE);
        chronoTriggerRune.setEnabled(options.get("chrono")==Boolean.TRUE);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICKED, runeRunner, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGED, runeRunner, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.REDSTONE_CHANGE, runeRunner, Priority.Normal, this);
    }
    public int getTier(Material mat)
    {
        Integer i = tiers.get(mat.getId());
        if (i==null) return -1;
        return i;
    }
    private void saveState()
    {
        try
        {
            FileOutputStream fout = new FileOutputStream(getDataFolder().getAbsolutePath()+File.separator+"settings.ini");
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
            System.out.println(getDataFolder().getAbsolutePath()+File.separator+"settings.ini");
        }
    }
    private void loadState()
    {
        options = new HashMap<String,Boolean>();
        try
        {
            FileInputStream fin = new FileInputStream(getDataFolder().getAbsolutePath()+File.separator+"settings.ini");
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            String option = "";
            while ((option = br.readLine())!=null)
            {
                String[] split = option.split("=", 2);
                options.put(split[0], Boolean.parseBoolean(split[1]));
            }
            br.close();
        }
        catch(Exception e)
        {
            System.out.println("Could not load option state - starting new option list");
            options = new HashMap<String,Boolean>();
            options.put("passages", Boolean.TRUE);
            options.put("teleports", Boolean.TRUE);
            options.put("compass", Boolean.TRUE);
            options.put("oracle", Boolean.TRUE);
            options.put("powertool", Boolean.TRUE);
            options.put("freezer", Boolean.TRUE);
            options.put("chrono", Boolean.TRUE);
            saveState();
        }
    }
    private Location getSafeLocation(Location loc)
    {
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
        loc.getWorld().loadChunk(loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getChunk());
        return loc;
    }
    public void addTool(Player player, Material material)
    {
        tool.add(player.getName()+"_"+material.name());
    }
    public void delTool(Player player, Material material)
    {
        tool.remove(player.getName()+"_"+material.name());
    }
    public boolean poweredTool(Player player, Material material)
    {
        return tool.contains(player.getName()+"_"+material.name());
    }
    public ItemStack getDrop(Block block, ItemStack itemInHand)
    {
        int type=block.getType().getId();
    	int drop;
    	int amount=1;
        int data=0;
        int pick = pickLevel(itemInHand.getType());

    	switch (type)
        {
            case 1://Stone
                if (pick>0) drop=4;
                else drop = 0;
                break;
            case 2://Grass
                drop=3;
                break;
            case 4://cobble
                if (pick>0) drop=4;
                else drop = 0;
                break;
            case 13://Gravel
                if (rand.nextDouble() >= 0.9) {drop=318;}
                else{drop=13;}
                break;
            case 14://gold ore
                if (pick>0) drop=14;
                else drop = 0;
                break;
            case 15://iron ore
                if (pick>0) drop=15;
                else drop = 0;
                break;
            case 16://Coal ore
                if (pick>0) drop=263;
                else drop = 0;
                break;
            case 18://Leaves
                drop=0;
                break;
            case 20://Glass
                drop=0;
                break;
            case 21://lapis ore
                if (pick>0) drop=351;
                else drop = 0;
                data=4;
                break;
            case 22://lapis block
                if (pick>0) drop=22;
                else drop = 0;
                break;
            case 23://dispenser
                if (pick>0) drop=23;
                else drop = 0;
                break;
            case 41://gold block
                if (pick>0) drop=41;
                else drop = 0;
                break;
            case 42://iron
                if (pick>0) drop=42;
                else drop = 0;
                break;
            case 43://Double step
                if (pick>0) drop=44;
                else drop = 0;
                break;
            case 44://stone slab
                if (pick>0) drop=44;
                else drop = 0;
                break;
            case 47://Bookshelf
                drop=0;
                break;
            case 49://obsidian
                if (pick>0) drop=49;
                else drop = 0;
                break;
            case 52://Spawner
                drop=0;
                break;
            case 55://Redstone
                drop=331;
                break;
            case 56://Diamond ore
                if (pick>0) drop=264;
                else drop = 0;
                break;
            case 57://diamond block
                if (pick>0) drop=57;
                else drop = 0;
                break;
            case 60://Soil
                drop=3;
                break;
            case 61://furnace
                if (pick>0) drop=61;
                else drop = 0;
                break;
            case 62://Burning furnace
                if (pick>0) drop=61;
                else drop = 0;
                break;
            case 63://Sign post
                drop=323;
                break;
            case 67://cobble stairs
                if (pick>0) drop=4;
                else drop = 0;
                break;
            case 68://Wall sign
                drop=323;
                break;
            case 73://Redstone ore
                if (pick>0) drop=331;
                else drop = 0;
                amount=4;
                break;
            case 74://Glowing redstone ore
                if (pick>0) drop=331;
                else drop = 0;
                amount=4;
                break;
            case 75://Redstone torch off
                drop=76;
                break;
            case 78://Snow
                drop=0;
                break;
            case 79://Ice
                drop=0;
                break;
            case 80://Snowblock
                drop=332;
                amount=4;
                break;
            case 82://Clay
                drop=337;
                amount=4;
                break;
            case 83://Reed
                drop=338;
                break;
            case 89://Lightstone
                drop=348;
                break;
            default://Items that drop their own id, like cobblestone and dirt
                drop=type;
    	}
    	if(drop>0)
        {
            ItemStack ret = new ItemStack(drop);
            ret.setAmount(amount);
            if (data>0)
            {
                ret.setDurability((short)data);
            }
            return ret;
    	}
        return null;
    }
    private int pickLevel(Material mat)
    {
        if (mat==Material.WOOD_PICKAXE)return 1;
        if (mat==Material.GOLD_PICKAXE)return 2;
        if (mat==Material.STONE_PICKAXE)return 3;
        if (mat==Material.IRON_PICKAXE)return 4;
        if (mat==Material.DIAMOND_PICKAXE)return 5;
        return 0;
    }
}


