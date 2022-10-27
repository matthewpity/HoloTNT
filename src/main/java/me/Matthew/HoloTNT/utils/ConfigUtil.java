package me.Matthew.HoloTNT.utils;

import me.Matthew.HoloTNT.HoloTNTPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtil {
    private final ArrayList<FileConfiguration> customConfigs = new ArrayList<FileConfiguration>();
    private final List<String> configNames = new ArrayList<>();
    private HoloTNTPlugin plugin = null;

    public ConfigUtil(HoloTNTPlugin plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig(String name){
        if (customConfigs.size() > 0) {
            for (FileConfiguration conf : customConfigs) {
                if (conf.getName().equalsIgnoreCase(name)) {
                    return conf;
                }
            }
        }

        return createNewCustomConfig(name);
    }

    public void reloadConfigs(){
        customConfigs.clear();
        configNames.clear();
    }

    public FileConfiguration createNewCustomConfig(String name) {
        FileConfiguration fileConfiguration;
        File configFile = new File(plugin.getDataFolder(), name);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(configFile);
            customConfigs.add(fileConfiguration);
            configNames.add(name);
            return fileConfiguration;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setData(FileConfiguration conf, String path, Object data){
        conf.set(path, data);
        return saveData(conf);
    }

    public boolean saveData(FileConfiguration conf){
        try {
            conf.save(new File(plugin.getDataFolder(), configNames.get(customConfigs.indexOf(conf))));
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getString(FileConfiguration conf, String path){
        return conf.getString(path);
    }

    public int getInt(FileConfiguration conf, String path){
        return conf.getInt(path);
    }

    public double getDouble(FileConfiguration conf, String path){
        return conf.getDouble(path);
    }

    public boolean getBoolean(FileConfiguration conf, String path){
        return conf.getBoolean(path);
    }

    public List<?> getList(FileConfiguration conf, String path){
        return conf.getList(path);
    }

    public boolean addLocation(FileConfiguration conf, Location location, String path){
        conf.set(String.format("%s.world", path), location.getWorld().getName());
        conf.set(String.format("%s.x", path), location.getX());
        conf.set(String.format("%s.y", path), location.getY());
        conf.set(String.format("%s.z", path), location.getZ());

        return saveData(conf);
    }

    public Location getLocation(FileConfiguration conf, String path){
        String worldName = getString(conf, String.format("%s.world", path));
        Bukkit.getServer().createWorld(new WorldCreator(worldName));

        World world = Bukkit.getWorld(worldName);
        int x = getInt(conf, String.format("%s.x", path));
        int y = getInt(conf, String.format("%s.y", path));
        int z = getInt(conf, String.format("%s.z", path));

        return new Location(world, x, y, z);
    }
}
