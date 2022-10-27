package me.Matthew.HoloTNT;

import me.Matthew.HoloTNT.utils.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HoloTNTPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        runnable();
    }

    public final ConfigUtil getConfigUtil = new ConfigUtil(this);

    public final FileConfiguration getConfig = getConfigUtil.getConfig("config.yml");

    public String getHologramTemplate(){
        if (getConfig.getString("hologram") == null) return " ";
        return getConfig.getString("hologram");
    }

    public int getDisplayRadius(){
        if (getConfig.get("display-radius") == null) return 50;
        return getConfig.getInt("display-radius");
    }

    public void runnable(){
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player player : Bukkit.getServer().getOnlinePlayers()){
                    for(Entity e : player.getNearbyEntities(getDisplayRadius(), getDisplayRadius(), getDisplayRadius())){
                        if(!(e instanceof TNTPrimed tnt)) continue;
                        tnt.setCustomNameVisible(true);
                        double fuseTime = ((double) tnt.getFuseTicks())/20;
                        if(fuseTime > 2)
                            tnt.setCustomName(translateColors(getHologramTemplate().replaceAll("%timer%", "&a" + String.format("%.1f", fuseTime))));
                        else if(fuseTime > 1)
                            tnt.setCustomName(translateColors(getHologramTemplate().replaceAll("%timer%", "&e" + String.format("%.1f", fuseTime))));
                        else tnt.setCustomName(translateColors(getHologramTemplate().replaceAll("%timer%", "&4" + String.format("%.1f", fuseTime))));
                    }
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    public String translateColors(String string) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            String hexCode = string.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            char[] chars = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder();

            for (char c : chars) builder.append("&").append(c);

            string = string.replace(hexCode, builder.toString());
            matcher = pattern.matcher(string);
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
