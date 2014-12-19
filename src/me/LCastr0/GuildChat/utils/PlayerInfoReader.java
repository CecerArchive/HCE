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

public class PlayerInfoReader {

    private final String playerToRead;
    private final Minecraft mc = Minecraft.getMinecraft();

    private String returnMessage;
    private String playerName;
    private ChatBuilder rankBuilder;
    private String vanityTokens;
    private String thanksSent;
    private String thanksReceived;
    private String tipsSent;
    private String tipsReceived;
    private String mostRecentGame;
    private String guildName;
    private String mostRecentGadget;
    private String networkLevel;

    private final List<String> lines = new ArrayList<String>();

    public PlayerInfoReader(String playerToRead){
        this.playerToRead = playerToRead;
        read();
    }

    public void read(){
        try {
            URL url = new URL("http://lcastr0.com/hce_playerstats.php?player=" + this.playerToRead);
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
        } else if(this.returnMessage.equals("--PLAYERNOTFOUND--")){
            ChatBuilder builder = new ChatBuilder();
            builder.setMessage("This player was not found or the API is down! Try searching another name!").setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(builder.build());
        } else if(this.returnMessage.equals("--NONAME--")){
            ChatBuilder builder = new ChatBuilder();
            builder.setMessage("You must type a name to be checked!").setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(builder.build());
        } else if(this.returnMessage.equals("--COMPLETED--")){
            if(this.lines.size() >= 12){
                this.playerName = lines.get(1);
                this.rankBuilder = getRankBuilder(lines.get(2), this.playerName);
                this.vanityTokens = lines.get(3);
                this.thanksSent = lines.get(4);
                this.thanksReceived = lines.get(5);
                this.tipsSent = lines.get(6);
                this.tipsReceived = lines.get(7);
                this.mostRecentGame = lines.get(8);
                this.guildName = lines.get(9);
                this.mostRecentGadget = lines.get(10);
                this.networkLevel = lines.get(11);
            }
            if(this.playerName != null && this.rankBuilder != null && this.vanityTokens != null
                    && this.thanksSent != null && this.thanksReceived != null && this.tipsSent != null
                    && this.tipsReceived != null && this.mostRecentGame != null && this.guildName != null
                    && this.mostRecentGadget != null && this.networkLevel != null){
                ChatBuilder key = new ChatBuilder();
                ChatBuilder value = new ChatBuilder();
                key.append(value);
                String preMessage = "---------------------------------------------";
                sendColoredMessageToPlayer(preMessage, EnumChatFormatting.AQUA);
                String m = "Player Info";
                StringBuilder spaces = new StringBuilder();
                int totalLength = m.length() + this.rankBuilder.getTotalLength(true);
                int totalSpaces = (preMessage.length() - totalLength) / 2;
                for(int i = 0; i < totalSpaces; i++){
                    spaces.append(" ");
                }
                ChatBuilder playerName = new ChatBuilder();
                playerName.setMessage(spaces.toString() + "Player Info ").setColor(EnumChatFormatting.GOLD).append("- ", EnumChatFormatting.DARK_AQUA)
                        .append(this.rankBuilder);
                this.mc.thePlayer.addChatMessage(playerName.build());
                sendColoredMessageToPlayer("", EnumChatFormatting.AQUA);
                key.setMessage("Vanity Tokens: ").setColor(EnumChatFormatting.AQUA);
                value.setMessage(this.vanityTokens).setColor(EnumChatFormatting.RED);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Thanks Sent: ");
                value.setMessage(this.thanksSent);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Thanks Received: ");
                value.setMessage(this.thanksReceived);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Tips Send: ");
                value.setMessage(this.tipsSent);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Tips Received: ");
                value.setMessage(this.tipsReceived);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Most Recent Game: ");
                value.setMessage(this.mostRecentGame);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Most Recent Gadget: ");
                value.setMessage(this.mostRecentGadget);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Network Level: ");
                value.setMessage(this.networkLevel);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Guild: ");
                ChatBuilder hoverBuilder = new ChatBuilder();
                hoverBuilder.setMessage("Click here to see more information about this guild!").setColor(EnumChatFormatting.AQUA);
                ClickEvent guildClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/g info " + this.guildName);
                HoverEvent guildHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build());
                value.setMessage(this.guildName).setChatClickEvent(guildClick).setChatHoverEvent(guildHover);
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
        return rankB;
    }

}