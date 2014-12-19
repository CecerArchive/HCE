package me.LCastr0.GuildChat.utils;

import me.LCastr0.GuildChat.messages.ChatBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GuildInfoReader {

    private final String guildToRead;
    private final Minecraft mc = Minecraft.getMinecraft();

    private String returnMessage;
    private String guildName;
    private String guildTag;
    private String guildCoins;
    private String guildMembers;
    private String guildMaster;
    private ChatBuilder rankBuilder;

    private final List<String> lines = new ArrayList<String>();

    public GuildInfoReader(String guildToRead){
        this.guildToRead = guildToRead;
        read();
    }

    public void read(){
        try {
            URL url = new URL("http://lcastr0.com/hce_guild.php?guild=" + this.guildToRead.replaceAll(" ", "%20"));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                for(String s : str.split("%%%%%")){
                    lines.add(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        interpret();
    }

    public void interpret(){
        if(lines.size() >= 1){
            this.returnMessage = lines.get(0);
        } else {
            this.returnMessage = "--APIISDOWN--";
        }
    }

    public void sendMessageToPlayer(){
        if(this.returnMessage.equals("--APIISDONW--")){
            ChatBuilder builder = new ChatBuilder();
            builder.setMessage("The Public API or the website is down! Check again later! (Or your connection is down!)")
                    .setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(builder.build());
        } else if(this.returnMessage.equals("--GUILDNOTFOUND--")){
            ChatBuilder builder = new ChatBuilder();
            builder.setMessage("This guild was not found or the API is down! Try searching another name!").setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(builder.build());
        } else if(this.returnMessage.equals("--NONAME--")){
            ChatBuilder builder = new ChatBuilder();
            builder.setMessage("You must type a name to be checked!").setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(builder.build());
        } else if(this.returnMessage.equals("--COMPLETED--")){
            if(this.lines.size() >= 7){
                this.guildName = lines.get(1);
                this.guildTag = lines.get(2);
                this.guildCoins = lines.get(3);
                this.guildMembers = lines.get(4);
                this.guildMaster = lines.get(6);
                this.rankBuilder = getRankBuilder(lines.get(5), this.guildMaster);
            }
            if(this.guildName != null && this.guildTag != null && this.guildCoins != null
                    && this.guildMembers != null && this.guildMaster != null){
                ChatBuilder key = new ChatBuilder();
                ChatBuilder value = new ChatBuilder();
                key.append(value);
                String preMessage = "---------------------------------------------";
                sendColoredMessageToPlayer(preMessage, EnumChatFormatting.AQUA);
                ChatBuilder guildNameBuilder = new ChatBuilder();
                int length = "Guild Info - ".length() + this.guildName.length();
                int totalSpaces = (preMessage.length() - length) / 2;
                StringBuilder spaces = new StringBuilder();
                for(int i = 0; i < totalSpaces; i++){
                    spaces.append(" ");
                }
                guildNameBuilder.setMessage(spaces.toString() + "Guild Info ").setColor(EnumChatFormatting.GOLD).append("- ", EnumChatFormatting.DARK_AQUA)
                        .append(this.guildName, EnumChatFormatting.DARK_GREEN);
                this.mc.thePlayer.addChatMessage(guildNameBuilder.build());
                sendColoredMessageToPlayer("", EnumChatFormatting.AQUA);
                key.setMessage("Guild Tag: ").setColor(EnumChatFormatting.AQUA);
                value.setMessage("[" + this.guildTag + "]").setColor(EnumChatFormatting.GRAY);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Guild Coins: ");
                value.setMessage(this.guildCoins).setColor(EnumChatFormatting.RED);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Guild Members: ");
                value.setMessage(this.guildMembers);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Guild Master: ");
                key.clearAppends();
                key.append(this.rankBuilder);
                this.mc.thePlayer.addChatMessage(key.build());
                sendColoredMessageToPlayer("", EnumChatFormatting.AQUA);
                sendColoredMessageToPlayer(preMessage, EnumChatFormatting.AQUA);
            } else {
                ChatBuilder builder = new ChatBuilder();
                builder.setMessage("Could not get all the needed information for that player! Try again later!").setColor(EnumChatFormatting.RED);
                this.mc.thePlayer.addChatMessage(builder.build());
            }
        } else {
            this.mc.thePlayer.addChatMessage(new ChatComponentText(this.returnMessage));
        }
    }

    private void sendColoredMessageToPlayer(String message, EnumChatFormatting color){
        ChatBuilder chatBuilder = new ChatBuilder();
        chatBuilder.setMessage(message).setColor(color);
        this.mc.thePlayer.addChatMessage(chatBuilder.build());
    }

    public ChatBuilder getRankBuilder(String rank, String playerName){
        ChatBuilder rankB = new ChatBuilder();
        if (rank.equals("DEFAULT")) {
            rankB.setMessage("").setColor(EnumChatFormatting.GRAY);
        } else if (rank.equals("VIP")) {
            rankB.setMessage("[VIP] ").setColor(EnumChatFormatting.GREEN);
        } else if (rank.equals("VIPPLUS")) {
            rankB.setMessage("[VIP").setColor(EnumChatFormatting.GREEN).append("+", EnumChatFormatting.GOLD)
                    .append("] ", EnumChatFormatting.GREEN);
        } else if (rank.equals("MVP")) {
            rankB.setMessage("[MVP] ").setColor(EnumChatFormatting.AQUA);
        } else if (rank.equals("MVPPLUS")) {
            rankB.setMessage("[MVP").setColor(EnumChatFormatting.AQUA).append("+", EnumChatFormatting.RED)
                    .append("] ", EnumChatFormatting.AQUA);
        } else if (rank.equals("JELPER")) {
            rankB.setMessage("[JR HELPER] ").setColor(EnumChatFormatting.BLUE);
        } else if (rank.equals("HELPER")) {
            rankB.setMessage("[HELPER] ").setColor(EnumChatFormatting.BLUE);
        } else if (rank.equals("MOD")) {
            rankB.setMessage("[MOD] ").setColor(EnumChatFormatting.DARK_GREEN);
        } else if (rank.equals("ADMIN")) {
            rankB.setMessage("[ADMIN] ").setColor(EnumChatFormatting.RED);
        } else if (rank.equals("YT")) {
            rankB.setMessage("[YT] ").setColor(EnumChatFormatting.GOLD);
        } else if (rank.equals("BUILDTEAM")) {
            rankB.setMessage("[BUILD TEAM] ").setColor(EnumChatFormatting.DARK_AQUA);
        } else if (rank.equals("BUILDTEAMPLUS")) {
            rankB.setMessage("[BUILD TEAM").setColor(EnumChatFormatting.DARK_AQUA).append("+", EnumChatFormatting.RED)
                    .append("] ", EnumChatFormatting.DARK_AQUA);
        } else if (rank.equals("MCPROHOSTING")) {
            rankB.append("[").setColor(EnumChatFormatting.RED).append("MC", EnumChatFormatting.GREEN)
                    .append("ProHosting", EnumChatFormatting.WHITE).append("] ", EnumChatFormatting.RED);
        } else if (rank.equals("OWNER")) {
            rankB.setMessage("[OWNER] ").setColor(EnumChatFormatting.RED);
        } else if (rank.equals("MOJANG")) {
            rankB.append("[MOJANG] ").setColor(EnumChatFormatting.GOLD);
        } else if (rank.equals("ANGUS")) {
            rankB.setMessage("[ANGUS] ").setColor(EnumChatFormatting.RED);
        }
        rankB.append(playerName);
        ChatBuilder hoverBuilder = new ChatBuilder();
        hoverBuilder.setMessage("Click here to see more information about this player!").setColor(EnumChatFormatting.AQUA);
        rankB.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build())).setChatClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND, "/pinfo " + playerName));
        return rankB;
    }

}