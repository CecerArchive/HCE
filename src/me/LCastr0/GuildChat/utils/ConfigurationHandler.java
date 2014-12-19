package me.LCastr0.GuildChat.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigurationHandler {

    public static String[] aliases = new String[]{};
    public static Minecraft mc = Minecraft.getMinecraft();
    public static File configFile;

    public static void createConfig() throws Exception {
        File confFile = new File(mc.mcDataDir.getPath(), "mods/HCE/aliases.txt");
        if(!confFile.exists()){
            confFile.getParentFile().mkdirs();
            confFile.createNewFile();
            FileWriter confWriter = new FileWriter(confFile.getAbsoluteFile());
            BufferedWriter confBW = new BufferedWriter(confWriter);
            confBW.write("aliasone,aliastwo,nospaceaftercomma");
            confBW.close();
        }
        configFile = confFile;
    }

    public static void readConfig() throws Exception {
        File confFile = new File(mc.mcDataDir.getPath(), "mods/HCE/aliases.txt");
        if(confFile.exists()){
            FileReader confReader = new FileReader(confFile.getAbsoluteFile());
            BufferedReader confBR = new BufferedReader(confReader);
            String fullString = confBR.readLine();
            if(fullString != null) {
                aliases = fullString.split(",");
            }
            confBR.close();
        }
    }

}