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

public class GameInfoReader {

    private final String playerToRead;
    private final String gameToRead;
    private final Minecraft mc = Minecraft.getMinecraft();

    private String returnMessage;
    private String playerCoins;
    private String playerWins;
    private String playerKills;
    private ChatBuilder rankBuilder;
    private String playerName;
    private String gunName;
    private String winsBS;
    private String winsRun;
    private String winsWiz;
    private String winsTag;
    private String wins2v2;
    private String kills2v2;
    private String wins4v4;
    private String kills4v4;
    private String winsFFA;
    private String killsFFA;
    private String maxWave;
    private String winsEnder;
    private String winsBounty;
    private String winsFarm;
    private String winsThrow;
    private String winsPartyOne;
    private String winsPartyTwo;
    private String winsDragon;

    private final List<String> lines = new ArrayList<String>();

    public GameInfoReader(String playerToRead, String gameToRead){
        this.playerToRead = playerToRead;
        this.gameToRead = gameToRead;
        read();
    }

    public void read(){
        try {
            URL url = new URL("http://lcastr0.com/hce_gamestats.php?player=" + this.playerToRead + "&game=" + getGameName());
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
            builder.setMessage("This player was not found! Try searching another name!").setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(builder.build());
        } else if(this.returnMessage.equals("--NONAME--")){
            ChatBuilder builder = new ChatBuilder();
            builder.setMessage("You must type a name to be checked!").setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(builder.build());
        } else if(this.returnMessage.equals("--COMPLETED--")){
            int toCheck = 6;
            if(getGameName().equals("Quake"))
                toCheck = 7;
            else if(getGameName().equals("TNTGames"))
                toCheck = 8;
            else if(getGameName().equals("Arena"))
                toCheck = 10;
            else if(getGameName().equals("Arcade"))
                toCheck = 12;
            if(this.lines.size() >= toCheck){
                if(getGameName().equals("TNTGames")){
                    this.playerCoins = lines.get(1);
                    this.winsBS = lines.get(2);
                    this.winsRun = lines.get(3);
                    this.winsWiz = lines.get(4);
                    this.winsTag = lines.get(5);
                    this.playerName = lines.get(7);
                    this.rankBuilder = getRankBuilder(lines.get(6), this.playerName);
                } else if(getGameName().equals("Arena")){
                    this.playerCoins = lines.get(1);
                    this.wins2v2 = lines.get(2);
                    this.kills2v2 = lines.get(3);
                    this.wins4v4 = lines.get(4);
                    this.kills4v4 = lines.get(5);
                    this.winsFFA = lines.get(6);
                    this.killsFFA = lines.get(7);
                    this.playerName = lines.get(9);
                    this.rankBuilder = getRankBuilder(lines.get(8), this.playerName);
                } else if(getGameName().equals("Arcade")){
                    this.playerCoins = lines.get(1);
                    this.maxWave = lines.get(2);
                    this.winsEnder = lines.get(3);
                    this.winsBounty = lines.get(4);
                    this.winsFarm = lines.get(5);
                    this.winsThrow = lines.get(6);
                    this.winsPartyOne = lines.get(7);
                    this.winsPartyTwo = lines.get(8);
                    this.winsDragon = lines.get(9);
                    this.playerName = lines.get(11);
                    this.rankBuilder = getRankBuilder(lines.get(10), this.playerName);
                } else {
                    this.playerCoins = lines.get(1);
                    this.playerWins = lines.get(2);
                    this.playerKills = lines.get(3);
                    this.playerName = lines.get(5);
                    this.rankBuilder = getRankBuilder(lines.get(4), this.playerName);
                    if (getGameName().equals("Quake"))
                        this.gunName = lines.get(6);
                }
            } else {
                ChatBuilder builder = new ChatBuilder();
                builder.setMessage("Could not get all the needed information for that player! Try again later!").setColor(EnumChatFormatting.RED);
                this.mc.thePlayer.addChatMessage(builder.build());
                return;
            }
            ChatBuilder key = new ChatBuilder();
            ChatBuilder value = new ChatBuilder();
            key.append(value);
            String preMessage = "---------------------------------------------";
            sendColoredMessageToPlayer(preMessage, EnumChatFormatting.AQUA);
            String m = "Game Info - " + getFriendlyGame() + " - ";
            StringBuilder spaces = new StringBuilder();
            int length = m.length() + this.rankBuilder.getTotalLength(true);
            int totalSpaces = (preMessage.length() - length) / 2;
            for(int i = 0; i < totalSpaces; i++){
                spaces.append(" ");
            }
            ChatBuilder gameInfo = new ChatBuilder();
            gameInfo.setMessage(spaces.toString() + "Game Info ").setColor(EnumChatFormatting.GOLD).append("- ", EnumChatFormatting.DARK_AQUA)
                    .append(getFriendlyGame(), EnumChatFormatting.DARK_GREEN).append(" - ", EnumChatFormatting.DARK_AQUA)
                    .append(this.rankBuilder);
            this.mc.thePlayer.addChatMessage(gameInfo.build());
            sendColoredMessageToPlayer("", EnumChatFormatting.AQUA);
            key.setMessage("Coins: ").setColor(EnumChatFormatting.AQUA);
            value.setMessage(this.playerCoins).setColor(EnumChatFormatting.RED);
            this.mc.thePlayer.addChatMessage(key.build());
            if(getGameName().equals("Arena")){
                key.setMessage("Wins 2v2: ");
                value.setMessage(this.wins2v2);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Kills 2v2: ");
                value.setMessage(this.kills2v2);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins 4v4: ");
                value.setMessage(this.wins4v4);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Kills 4v4: ");
                value.setMessage(this.kills4v4);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins FFA: ");
                value.setMessage(this.winsFFA);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Kills FFA: ");
                value.setMessage(this.killsFFA);
                this.mc.thePlayer.addChatMessage(key.build());
            } else if(getGameName().equals("TNTGames")){
                key.setMessage("Wins Bow Spleef: ");
                value.setMessage(this.winsBS);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins TNT Run: ");
                value.setMessage(this.winsRun);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins TNT Wizards: ");
                value.setMessage(this.winsWiz);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins TNT Tag: ");
                value.setMessage(this.winsTag);
                this.mc.thePlayer.addChatMessage(key.build());
            } else if(getGameName().equals("Arcade")){
                key.setMessage("Max Creeper Atack Wave: ");
                value.setMessage(this.maxWave);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins Ender Spleef: ");
                value.setMessage(this.winsEnder);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins Bounty Hunters: ");
                value.setMessage(this.winsBounty);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins Farm Hunt: ");
                value.setMessage(this.winsFarm);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins Throw Out: ");
                value.setMessage(this.winsThrow);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins Party Games 1: ");
                value.setMessage(this.winsPartyOne);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins Party Games 2: ");
                value.setMessage(this.winsPartyTwo);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Wins Dragon Wars: ");
                value.setMessage(this.winsDragon);
                this.mc.thePlayer.addChatMessage(key.build());
            } else {
                key.setMessage("Wins: ");
                value.setMessage(this.playerWins);
                this.mc.thePlayer.addChatMessage(key.build());
                key.setMessage("Kills: ");
                value.setMessage(this.playerKills);
                this.mc.thePlayer.addChatMessage(key.build());
                if(getGameName().equals("Quake")){
                    key.setMessage("Gun Name: ");
                    value.setMessage(this.gunName);
                    this.mc.thePlayer.addChatMessage(key.build());
                }
            }
            sendColoredMessageToPlayer("", EnumChatFormatting.AQUA);
            sendColoredMessageToPlayer(preMessage, EnumChatFormatting.AQUA);
        } else {
            this.mc.thePlayer.addChatMessage(new ChatComponentText(this.returnMessage));
        }
    }

    private void sendColoredMessageToPlayer(String message, EnumChatFormatting color){
        ChatBuilder chatBuilder = new ChatBuilder();
        chatBuilder.setMessage(message).setColor(color);
        this.mc.thePlayer.addChatMessage(chatBuilder.build());
    }

    private String getFriendlyGame(){
        if (this.gameToRead.equals("quake")) {
            return "Quakecraft";
        } else if (this.gameToRead.equals("pb")) {
            return "Paintball";
        } else if (this.gameToRead.equals("paintball")) {
            return "Paintball";
        } else if (this.gameToRead.equals("cvc")) {
            return "CvC";
        } else if (this.gameToRead.equals("cac")) {
            return "CvC";
        } else if (this.gameToRead.equals("megawalls")) {
            return "Mega Walls";
        } else if (this.gameToRead.equals("mw")) {
            return "Mega Walls";
        } else if (this.gameToRead.equals("walls")) {
            return "Walls";
        } else if (this.gameToRead.equals("blitz")) {
            return "Blitz Survival Games";
        } else if (this.gameToRead.equals("bsg")) {
            return "Blitz Survival Games";
        } else if (this.gameToRead.equals("vz")) {
            return "VampireZ";
        } else if (this.gameToRead.equals("vampirez")) {
            return "VampireZ";
        } else if (this.gameToRead.equals("arena")) {
            return "Arena Brawl";
        } else if (this.gameToRead.equals("arenabrawl")) {
            return "Arena Brawl";
        } else if (this.gameToRead.equals("tnt")) {
            return "TNT Games";
        } else if (this.gameToRead.equals("tntgames")) {
            return "TNT Games";
        } else if (this.gameToRead.equals("arcade")) {
            return "Arcade";
        }
        return "Error!";
    }

    private String getGameName(){
        if (this.gameToRead.equals("quake")) {
            return "Quake";
        } else if (this.gameToRead.equals("pb")) {
            return "Paintball";
        } else if (this.gameToRead.equals("paintball")) {
            return "Paintball";
        } else if (this.gameToRead.equals("cvc")) {
            return "MCGO";
        } else if (this.gameToRead.equals("cac")) {
            return "MCGO";
        } else if (this.gameToRead.equals("megawalls")) {
            return "Walls3";
        } else if (this.gameToRead.equals("mw")) {
            return "Walls3";
        } else if (this.gameToRead.equals("walls")) {
            return "Walls";
        } else if (this.gameToRead.equals("blitz")) {
            return "HungerGames";
        } else if (this.gameToRead.equals("bsg")) {
            return "HungerGames";
        } else if (this.gameToRead.equals("vz")) {
            return "VampireZ";
        } else if (this.gameToRead.equals("vampirez")) {
            return "VampireZ";
        } else if (this.gameToRead.equals("arena")) {
            return "Arena";
        } else if (this.gameToRead.equals("arenabrawl")) {
            return "Arena";
        } else if (this.gameToRead.equals("tnt")) {
            return "TNTGames";
        } else if (this.gameToRead.equals("tntgames")) {
            return "TNTGames";
        } else if (this.gameToRead.equals("arcade")) {
            return "Arcade";
        }
        return "Quakecraft";
    }

    public static boolean canCheck(String game){
        return game.equals("quake") || game.equals("pb") || game.equals("paintball") || game.equals("cvc")
                || game.equals("cac") || game.equals("megawalls") || game.equals("mw") || game.equals("walls")
                || game.equals("blitz") || game.equals("bsg") || game.equals("vz") || game.equals("vampirez")
                || game.equals("arena") || game.equals("arenabrawl") || game.equals("tnt") || game.equals("tntgames")
                || game.equals("arcade");
    }

    public ChatBuilder getRankBuilder(String rank, String playerName){
        ChatBuilder rankB = new ChatBuilder();
        if (rank.equals("DEFAULT")) {
            rankB.setMessage("").setColor(EnumChatFormatting.GRAY);
        } else if (rank.equals("VIP")) {
            rankB.setMessage("[VIP] ").setColor(EnumChatFormatting.GREEN);
        } else if (rank.equals("VIP+")) {
            rankB.setMessage("[VIP").setColor(EnumChatFormatting.GREEN).append("+", EnumChatFormatting.GOLD)
                    .append("] ", EnumChatFormatting.GREEN);
        } else if (rank.equals("MVP")) {
            rankB.setMessage("[MVP] ").setColor(EnumChatFormatting.AQUA);
        } else if (rank.equals("MVP+")) {
            rankB.setMessage("[MVP").setColor(EnumChatFormatting.AQUA).append("+", EnumChatFormatting.RED)
                    .append("] ", EnumChatFormatting.AQUA);
        } else if (rank.equals("JR_HELPER")) {
            rankB.setMessage("[JR HELPER] ").setColor(EnumChatFormatting.BLUE);
        } else if (rank.equals("HELPER")) {
            rankB.setMessage("[HELPER] ").setColor(EnumChatFormatting.BLUE);
        } else if (rank.equals("MODERATOR")) {
            rankB.setMessage("[MOD] ").setColor(EnumChatFormatting.DARK_GREEN);
        } else if (rank.equals("ADMIN")) {
            rankB.setMessage("[ADMIN] ").setColor(EnumChatFormatting.RED);
        } else if (rank.equals("YOUTUBER")) {
            rankB.setMessage("[YT] ").setColor(EnumChatFormatting.GOLD);
        } else if (rank.equals("ANGUS")) {
            rankB.setMessage("[PreparedAngus] ").setColor(EnumChatFormatting.RED);
        }
        if(playerName.equalsIgnoreCase("hypixel")){
            rankB.setMessage("[OWNER]").setColor(EnumChatFormatting.RED);
            rankB.clearAppends();
        }
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pinfo " + this.playerName);
        ChatBuilder hoverBuilder = new ChatBuilder();
        hoverBuilder.setMessage("Click here to see more information about this player!").setColor(EnumChatFormatting.AQUA);
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build());
        rankB.setChatClickEvent(clickEvent).setChatHoverEvent(hoverEvent);
        rankB.append(playerName);
        return rankB;
    }

}