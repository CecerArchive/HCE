package me.LCastr0.GuildChat.commands;

import me.LCastr0.GuildChat.messages.ChatBuilder;
import me.LCastr0.GuildChat.utils.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AliasCommand {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean onSendChatMessage(String message){
        if(message.startsWith("/addalias")){
            String[] msg = message.split(" ");
            if(msg.length >= 2){
                sendMessage(true, false, msg);
                try {
                    addAliases(msg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                sendMessage(false, false, msg);
            }
            return false;
        } else if(message.startsWith("/removealias")){
            String[] msg = message.split(" ");
            if(msg.length >= 2){
                sendMessage(true, true, msg);
                try {
                    removeAliases(msg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                sendMessage(false, true, msg);
            }
            return false;
        }
        return true;
    }

    private static void sendMessage(boolean confirm, boolean remove, String[] msgs){
        List<String> aliases = new ArrayList<String>();
        for(String msg : msgs){
            if(msgs[0] != msg)
                aliases.add(msg);
        }
        if(!confirm){
            ChatBuilder chatBuilder = new ChatBuilder();
            chatBuilder.setMessage("You must provide at least one alias! (Separate them by using spaces!)").setColor(EnumChatFormatting.RED)
                    .setBold(true);
            mc.thePlayer.addChatMessage(chatBuilder.build());
        } else {
            if(!remove) {
                ChatBuilder chatBuilder = new ChatBuilder();
                ChatBuilder comma = new ChatBuilder();
                chatBuilder.setMessage("Added " + aliases.size() + " new aliases!").setColor(EnumChatFormatting.GREEN);
                comma.setMessage(", ").setColor(EnumChatFormatting.AQUA);
                mc.thePlayer.addChatMessage(chatBuilder.build());
                for (int i = 0; i < aliases.size(); i++) {
                    ChatBuilder aliasBuilder = new ChatBuilder();
                    aliasBuilder.setMessage(" - " + aliases.get(i)).setColor(EnumChatFormatting.YELLOW).setBold(true).setItalic(true);
                    if (i != aliases.size() - 1) {
                        aliasBuilder.append(comma);
                    }
                    mc.thePlayer.addChatMessage(aliasBuilder.build());
                }
            } else {
                ChatBuilder chatBuilder = new ChatBuilder();
                ChatBuilder comma = new ChatBuilder();
                chatBuilder.setMessage("Removed " + aliases.size() + " aliases!").setColor(EnumChatFormatting.GREEN);
                comma.setMessage(", ").setColor(EnumChatFormatting.AQUA);
                mc.thePlayer.addChatMessage(chatBuilder.build());
                for (int i = 0; i < aliases.size(); i++) {
                    ChatBuilder aliasBuilder = new ChatBuilder();
                    aliasBuilder.setMessage(" - " + aliases.get(i)).setColor(EnumChatFormatting.YELLOW).setBold(true).setItalic(true);
                    if (i != aliases.size() - 1) {
                        aliasBuilder.append(comma);
                    }
                    mc.thePlayer.addChatMessage(aliasBuilder.build());
                }
            }
        }
    }

    private static void addAliases(String[] msgs) throws Exception {
        List<String> aliases = new ArrayList<String>();
        for(String msg : msgs){
            if(msgs[0] != msg)
                aliases.add(msg);
        }
        File configFile = ConfigurationHandler.configFile;
        if(configFile != null && configFile.exists()){
            FileReader confReader = new FileReader(configFile.getAbsoluteFile());
            BufferedReader confBR = new BufferedReader(confReader);
            String fullString = confBR.readLine();
            StringBuilder builder = new StringBuilder();
            if(fullString != null)
                builder.append(fullString);
            for(String a : aliases){
                if(aliases.get(0).equals(a)){
                    if(fullString != null){
                        if(fullString.endsWith(",")){
                            builder.append(a);
                        } else {
                            builder.append(","+a);
                        }
                    } else {
                        builder.append(a);
                    }
                } else {
                    builder.append(","+a);
                }
            }
            FileWriter confWriter = new FileWriter(configFile.getAbsoluteFile());
            BufferedWriter confBW = new BufferedWriter(confWriter);
            confBW.write(builder.toString());
            confBW.close();
            confBR.close();
        }
    }

    private static void removeAliases(String[] msgs) throws Exception {
        File configFile = ConfigurationHandler.configFile;
        List<String> aliases = new ArrayList<String>();
        for(String msg : msgs){
            if(msgs[0] != msg)
                aliases.add(msg);
        }
        if(configFile != null && configFile.exists()){
            FileReader fileReader = new FileReader(configFile.getAbsoluteFile());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String fullString = bufferedReader.readLine();
            String[] actualAliases = fullString.split(",");
            List<String> newAliases = new ArrayList<String>();
            if(actualAliases != null){
                for(String a : actualAliases){
                    if(!aliases.contains(a)){
                        newAliases.add(a);
                    }
                }
            }
            StringBuilder builder = new StringBuilder();
            builder.append(newAliases.get(0));
            for(String s : newAliases){
                if(s != newAliases.get(0)){
                    builder.append("," + s);
                }
            }
            FileWriter fileWriter = new FileWriter(configFile.getAbsoluteFile());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(builder.toString());
            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

}