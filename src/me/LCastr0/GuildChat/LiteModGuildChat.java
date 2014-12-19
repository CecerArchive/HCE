package me.LCastr0.GuildChat;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.ChatFilter;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.OutboundChatFilter;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.LiteLoaderEventBroker;
import me.LCastr0.GuildChat.commands.AliasCommand;
import me.LCastr0.GuildChat.commands.GuildInfoCommand;
import me.LCastr0.GuildChat.commands.PlayerInfoCommand;
import me.LCastr0.GuildChat.messages.*;
import me.LCastr0.GuildChat.utils.ConfigurationHandler;
import me.LCastr0.GuildChat.utils.TimeAgoUtil;
import me.LCastr0.GuildChat.utils.UpdateChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.*;
import java.util.List;

public class LiteModGuildChat implements ChatFilter, Tickable, JoinGameListener, OutboundChatFilter{

    private static LiteModGuildChat instance;

    Minecraft mc = Minecraft.getMinecraft();
    public boolean isClearingGuild = false;
    public boolean joinedServer = false;
    public boolean isLobbying = false;
    public static int keyGoToLobbyIndex = Keyboard.getKeyIndex("L");
    public static int keyToggleGuildIndex = Keyboard.getKeyIndex("G");
    public static int keyReadGuildIndex = Keyboard.getKeyIndex("R");
    public static int keyClearGuildIndex = Keyboard.getKeyIndex("C");
    public static int keyPreviousGuildIndex = Keyboard.getKeyIndex("P");
    public static int keyNextGuildIndex = Keyboard.getKeyIndex("N");
    public static int keyToggleGGIndex = Keyboard.getKeyIndex("Z");
    public static int keyToggleViewIndex = Keyboard.getKeyIndex("V");
    public static String gc = "\u00A72HCE";
    public static KeyBinding keyGoToLobby = new KeyBinding("Go To Lobby", keyGoToLobbyIndex, gc);
    public static KeyBinding keyToggleGuild = new KeyBinding("Toggle Guild Chat", keyToggleGuildIndex, gc);
    public static KeyBinding keyReadGuild = new KeyBinding("Read Guild Chat", keyReadGuildIndex, gc);
    public static KeyBinding keyClearGuild = new KeyBinding("Clear Guild Chat", keyClearGuildIndex, gc);
    public static KeyBinding keyPreviousGuild = new KeyBinding("Previous Guild Message Page", keyPreviousGuildIndex, gc);
    public static KeyBinding keyNextGuild = new KeyBinding("Next Guild Message Page", keyNextGuildIndex, gc);
    public static KeyBinding keyToggleGG = new KeyBinding("Toggle Auto 'GG'", keyToggleGGIndex, gc);
    public static KeyBinding keyToggleView = new KeyBinding("Toggle Server View", keyToggleViewIndex, gc);
    public String keyGoToLobbyName, keyToggleGuildName, keyReadGuildName, keyClearGuildName, keyPreviousGuildName, keyNextGuildName,
        keyToggleGGName, keyToggleViewName;
    public int refreshTime = 10;
    public List<String> aliases;
    public int actualPageGuild = 0;
    public GuildChatFilter guildChatFilter;
    private int version = 12;
    public UpdateChecker updateChecker;
    public boolean autoGG = false, viewServer = false, reloadingServer = false;
    public String actualServer;

    @Override
    public boolean onChat(IChatComponent chat, String message, LiteLoaderEventBroker.ReturnValue<IChatComponent> newMessage) {
        if (chat.getUnformattedText().startsWith("Guild >")) {
            return this.guildChatFilter.onChat(chat, message);
        } else if (chat.getUnformattedText().endsWith("joined.")){
            ChatBuilder hoverBuilder = new ChatBuilder();
            String[] words = chat.getUnformattedText().split(" ");
            String name = "";
            if(words[0] != null)
                name = words[0];
            hoverBuilder.setMessage("Click to message this player!").setColor(EnumChatFormatting.AQUA);
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + name + " ");
            ChatBuilder builder = new ChatBuilder();
            builder.setMessage("").appendFromComponent(chat).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()))
                    .setChatClickEvent(clickEvent);
            this.mc.thePlayer.addChatMessage(builder.build());
            return false;
        } else if (chat.getUnformattedText().startsWith("Your game was boosted by ") && chat.getUnformattedText().endsWith("triple coins!")){
            ChatBuilder hoverBuilder = new ChatBuilder();
            hoverBuilder.setMessage("Click here to tip this player!").setColor(EnumChatFormatting.AQUA);
            String[] words = chat.getUnformattedText().split(" ");
            String name = "";
            if(words[5] != null)
                name = words[5].replaceAll("'s", "");
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tip " + name);
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build());
            ChatBuilder messageBefore = new ChatBuilder();
            ChatBuilder playerName = new ChatBuilder();
            ChatBuilder messageAfter = new ChatBuilder();
            ChatBuilder messageTriple = new ChatBuilder();
            playerName.setMessage(name).setColor(EnumChatFormatting.GOLD).setChatHoverEvent(hoverEvent).setChatClickEvent(clickEvent);
            messageBefore.setMessage("Your game was boosted by ").setColor(EnumChatFormatting.DARK_AQUA);
            messageAfter.setMessage("'s ").setColor(EnumChatFormatting.DARK_AQUA);
            messageTriple.setMessage("triple coins").setColor(EnumChatFormatting.RED).setBold(true);
            messageAfter.append(messageTriple).append("!", EnumChatFormatting.DARK_AQUA, false, false, false, false, false);
            messageBefore.append(playerName).append(messageAfter);
            this.mc.thePlayer.addChatMessage(messageBefore.build());
            return false;
        } else if(chat.getUnformattedText().contains("Reward Summary")){
            if(autoGG){
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mc.thePlayer.sendChatMessage("gg");
                    }
                }, 900);
            }
        } else if(chat.getUnformattedText().startsWith("You are currently on server")){
            if(reloadingServer) {
                String[] words = chat.getUnformattedText().split(" ");
                this.actualServer = words[words.length - 1];
                reloadingServer = false;
                return false;
            }
        } else if(chat.getUnformattedText().contains("Unknown command")){
            if(reloadingServer){
                reloadingServer = false;
                this.actualServer = "You're not in the Hypixel Server!";
                return false;
            }
        } else if(chat.getUnformattedText().startsWith("From ")){
            ChatBuilder pmBuilder = new ChatBuilder();
            ChatBuilder hoverBuilder = new ChatBuilder();
            hoverBuilder.setMessage("Click here to answer this player!").setColor(EnumChatFormatting.AQUA);
            pmBuilder.setMessage("From ").setColor(EnumChatFormatting.LIGHT_PURPLE);
            String[] words = chat.getUnformattedText().split(" ");
            String playerName = words[1];
            if(playerName.contains("[") && playerName.contains("]"))
                playerName = words[2];
            playerName.replaceAll(":", "");
            ChatBuilder toAppend = getRankBuilder(words[1].replaceAll(":", ""), playerName);
            pmBuilder.append(toAppend);
            List<String> wordList = new ArrayList<String>();
            for(String s : words){
                wordList.add(s);
            }
            wordList.remove("From"); wordList.remove(playerName);
            for(String s : wordList){
                if(!s.startsWith("[") && !s.endsWith("]"))
                    pmBuilder.append(s + " ", EnumChatFormatting.GRAY);
            }
            pmBuilder.setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + playerName.replaceAll(":", "") + " "))
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
            this.mc.thePlayer.addChatMessage(pmBuilder.build());
            return false;
        }
        return true;
    }

        @Override
        public String getVersion() {
        return "1.5";
    }


    @Override
    public void init(File configPath) {
        LiteLoader.getInput().registerKeyBinding(keyGoToLobby);
        LiteLoader.getInput().registerKeyBinding(keyToggleGuild);
        LiteLoader.getInput().registerKeyBinding(keyReadGuild);
        LiteLoader.getInput().registerKeyBinding(keyClearGuild);
        LiteLoader.getInput().registerKeyBinding(keyPreviousGuild);
        LiteLoader.getInput().registerKeyBinding(keyNextGuild);
        LiteLoader.getInput().registerKeyBinding(keyToggleGG);
        LiteLoader.getInput().registerKeyBinding(keyToggleView);
        try {
            ConfigurationHandler.createConfig();
            ConfigurationHandler.readConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.aliases = Arrays.asList(ConfigurationHandler.aliases);
        this.reloadConfig();
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public String getName() {
        return "HCE";
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if(instance == null){
            instance = this;
            this.guildChatFilter = new GuildChatFilter();
            this.updateChecker = new UpdateChecker();
        }
        this.indicatorEvent();
        this.keyEvent();
        this.sendJoinMessage();
        keyGoToLobbyName = Keyboard.getKeyName(keyGoToLobby.getKeyCode());
        keyToggleGuildName = Keyboard.getKeyName(keyToggleGuild.getKeyCode());
        keyReadGuildName = Keyboard.getKeyName(keyReadGuild.getKeyCode());
        keyClearGuildName = Keyboard.getKeyName(keyClearGuild.getKeyCode());
        keyPreviousGuildName = Keyboard.getKeyName(keyPreviousGuild.getKeyCode());
        keyNextGuildName = Keyboard.getKeyName(keyNextGuild.getKeyCode());
        keyToggleGGName = Keyboard.getKeyName(keyToggleGG.getKeyCode());
        keyToggleViewName = Keyboard.getKeyName(keyToggleView.getKeyCode());
    }

    private void indicatorEvent(){
        this.guildChatFilter.addGui();
        if(this.mc.currentScreen == null) {
            if(autoGG)
                this.mc.fontRendererObj.drawString("Auto 'GG'", 5, 25, 0xFFAA00);
            if(viewServer)
                this.mc.fontRendererObj.drawString(this.actualServer, 5, 15, 0xFFAA00);
        }
    }

    private void keyEvent(){
        int totalPagesGuild = (int) Math.ceil(GuildMessageStorer.getCount() / 10.0);
        if(this.mc.currentScreen == null){
            if(keyGoToLobby.isPressed()){
                if(isLobbying){
                    if(this.actualServer != null){
                        if(this.actualServer.contains("lobby")){
                            this.mc.thePlayer.sendChatMessage("/lobby");
                        } else {
                            this.mc.thePlayer.sendChatMessage("/lobby");
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                 mc.thePlayer.sendChatMessage("/lobby");
                                }
                            }, 500);
                        }
                    } else {
                        this.mc.thePlayer.sendChatMessage("/lobby");
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mc.thePlayer.sendChatMessage("/lobby");
                            }
                        }, 500);
                    }
                    this.isLobbying = false;
                } else {
                    this.isLobbying = true;
                    resetLobbying();
                }
            } else if(keyToggleGuild.isPressed()){
                if(this.guildChatFilter.isToggled()){
                    this.guildChatFilter.setToggled(false);
                    sendColoredMessageToPlayer("The Guild Chat is now toggled off!", EnumChatFormatting.DARK_GREEN);
                    sendColoredMessageToPlayer("You have received " + GuildMessageStorer.getCount() + " message(s)! To read them, press "
                            + this.keyReadGuildName + "!", EnumChatFormatting.AQUA);
                } else {
                    this.guildChatFilter.setToggled(true);
                    sendColoredMessageToPlayer("The Guild Chat is now toggled on!", EnumChatFormatting.DARK_GREEN);
                }
            } else if(keyClearGuild.isPressed()){
                if(GuildMessageStorer.getCount() <= 0){
                    sendColoredMessageToPlayer("There is no stored message!", EnumChatFormatting.RED);
                    return;
                }
                if(this.isClearingGuild){
                    GuildMessageStorer.deleteAll();
                    sendColoredMessageToPlayer("Cleared all the stored messages!", EnumChatFormatting.RED);
                    this.isClearingGuild = false;
                } else {
                    sendColoredMessageToPlayer("Are you sure you want to clear all the stored messages?", EnumChatFormatting.RED);
                    sendColoredMessageToPlayer("Press " + this.keyClearGuildName + " to confirm! (You have " + this.refreshTime +
                                    " second(s) to confirm!)", EnumChatFormatting.RED);
                    this.isClearingGuild = true;
                    resetClear(false);
                }
            } else if(keyReadGuild.isPressed()){
                if(GuildMessageStorer.getCount() <= 0){
                    sendColoredMessageToPlayer("There is no stored message!", EnumChatFormatting.RED);
                    return;
                }
                sendMessageListGuild(totalPagesGuild, false, false);
            } else if(keyNextGuild.isPressed()){
                if(GuildMessageStorer.getCount() <= 0){
                    sendColoredMessageToPlayer("There is no stored message!", EnumChatFormatting.RED);
                    return;
                }
                sendMessageListGuild(totalPagesGuild, false, true);
            } else if(keyPreviousGuild.isPressed()){
                if(GuildMessageStorer.getCount() <= 0){
                    sendColoredMessageToPlayer("There is no stored message!", EnumChatFormatting.RED);
                    return;
                }
                sendMessageListGuild(totalPagesGuild, true, false);
            } else if(keyToggleGG.isPressed()){
                if(autoGG){
                    autoGG = false;
                    sendColoredMessageToPlayer("Auto 'GG' is now toggled off!", EnumChatFormatting.RED);
                } else {
                    autoGG = true;
                    sendColoredMessageToPlayer("Auto 'GG' is now toggled on!", EnumChatFormatting.GREEN);
                }
            } else if(keyToggleView.isPressed()){
                if(viewServer){
                    viewServer = false;
                    sendColoredMessageToPlayer("Server View is now toggled off!", EnumChatFormatting.RED);
                } else {
                    viewServer = true;
                    sendColoredMessageToPlayer("Server View is now toggled on!", EnumChatFormatting.GREEN);
                    reloadServer();
                }
            }
        }
    }

    private void sendMessageListGuild(int totalPages, boolean previous, boolean next){
        if(previous){
            if(actualPageGuild == 1) {
                sendColoredMessageToPlayer("There is no previous page!", EnumChatFormatting.RED);
                return;
            }
            actualPageGuild = actualPageGuild -1;
        } else if(next){
            if(actualPageGuild + 1 == totalPages + 1) {
                sendColoredMessageToPlayer("There is no next page!", EnumChatFormatting.RED);
                return;
            }
            actualPageGuild = actualPageGuild + 1;
        } else {
            actualPageGuild = 1;
        }
        boolean before = this.guildChatFilter.isToggled();
        this.guildChatFilter.setToggled(false);
        int fM = (actualPageGuild * 10) - 10;
        int lM = fM + 10;
        String c = "---------------------------------------------";
        sendColoredMessageToPlayer(c, EnumChatFormatting.AQUA);
        String m = "Message List (Page " + actualPageGuild + " of " + totalPages + ")";
        sendColoredMessageToPlayer(StringUtils.center(m, 80-m.length()), EnumChatFormatting.GOLD);
        sendColoredMessageToPlayer("", EnumChatFormatting.AQUA);
        for(int i = fM; i < lM; i++){
            if(GuildMessageStorer.getCount() >= i + 1){
                int msgN = i + 1;
                ChatBuilder count = new ChatBuilder();
                count.setMessage(msgN + ". ").setColor(EnumChatFormatting.AQUA)
                        .appendFromComponent(GuildMessageStorer.getMessage(i));
                ChatBuilder timeAgo = new ChatBuilder();
                timeAgo.setMessage(" [" + TimeAgoUtil.getTimeAgo(GuildMessageStorer.getTime(i)) + "]")
                    .setColor(EnumChatFormatting.YELLOW).setItalic(true);
                count.append(timeAgo);
                this.mc.thePlayer.addChatMessage(count.build());
            }
        }
        sendColoredMessageToPlayer("", EnumChatFormatting.AQUA);
        sendColoredMessageToPlayer(c, EnumChatFormatting.AQUA);
        sendColoredMessageToPlayer("Previous Page -> " + this.keyPreviousGuildName +
                " | Next Page -> " + this.keyNextGuildName, EnumChatFormatting.DARK_GREEN);
        sendColoredMessageToPlayer("To clear the messages, press " + this.keyClearGuildName, EnumChatFormatting.RED);
        this.guildChatFilter.setToggled(before);
    }

    private void resetClear(boolean msg){
        Timer timer = new Timer();
        if(!msg) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isClearingGuild = false;
                }
            }, refreshTime * 1000);
        }
    }

    private void resetLobbying(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isLobbying = false;
            }
        }, 2 * 1000);
    }

    private void sendJoinMessage(){
        if(this.mc.currentScreen == null){
            if(joinedServer){
                ChatBuilder hceMod = new ChatBuilder();
                ChatBuilder guildToggle = new ChatBuilder();
                ChatBuilder lobbyKey = new ChatBuilder();
                ChatBuilder autoGGMessage = new ChatBuilder();
                ChatBuilder toggleViewMessage = new ChatBuilder();
                hceMod.setMessage("HCE Mod enabled!").setColor(EnumChatFormatting.DARK_GREEN);
                guildToggle.setMessage("Press ").setColor(EnumChatFormatting.DARK_GREEN).append(this.keyToggleGuildName, EnumChatFormatting.AQUA)
                        .append(" to toggle the guild chat!", EnumChatFormatting.DARK_GREEN);
                lobbyKey.setMessage("Press ").setColor(EnumChatFormatting.DARK_GREEN).append(this.keyGoToLobbyName, EnumChatFormatting.AQUA)
                        .append(" twice to go to the lobby!", EnumChatFormatting.DARK_GREEN);
                autoGGMessage.setMessage("Press ").setColor(EnumChatFormatting.DARK_GREEN).append(this.keyToggleGGName, EnumChatFormatting.AQUA)
                        .append(" to toggle Auto 'GG'!", EnumChatFormatting.DARK_GREEN);
                toggleViewMessage.setMessage("Press ").setColor(EnumChatFormatting.DARK_GREEN).append(this.keyToggleViewName, EnumChatFormatting.AQUA)
                        .append(" to toggle the Server Viewer!", EnumChatFormatting.DARK_GREEN);
                this.mc.thePlayer.addChatMessage(hceMod.build());
                this.mc.thePlayer.addChatMessage(guildToggle.build());
                this.mc.thePlayer.addChatMessage(lobbyKey.build());
                this.mc.thePlayer.addChatMessage(autoGGMessage.build());
                this.mc.thePlayer.addChatMessage(toggleViewMessage.build());
                joinedServer = false;
            }
        }
    }

    @Override
    public void onJoinGame(INetHandler netHandler, S01PacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer) {
        joinedServer = true;
    }

    private void reloadConfig(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    ConfigurationHandler.readConfig();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                aliases = Arrays.asList(ConfigurationHandler.aliases);
                updateChecker.check(version);
            }
        }, 60 * 1000, 60 * 1000);
    }

    private void reloadServer(){
        reloadingServer = true;
        mc.thePlayer.sendChatMessage("/whereami");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reloadingServer = true;
                mc.thePlayer.sendChatMessage("/whereami");
            }
        }, 90 * 1000, 90 * 1000);
    }

    public static LiteModGuildChat getInstance(){
        return instance;
    }

    private void sendColoredMessageToPlayer(String message, EnumChatFormatting color){
        ChatBuilder chatBuilder = new ChatBuilder();
        chatBuilder.setMessage(message).setColor(color).setBold(true);
        this.mc.thePlayer.addChatMessage(chatBuilder.build());
    }

    @Override
    public boolean onSendChatMessage(String message) {
        if(message.startsWith("/pinfo")){
            return PlayerInfoCommand.onSendChatMessage(message);
        } else if(message.startsWith("/addalias") || message.startsWith("/removealias")){
            return AliasCommand.onSendChatMessage(message);
        } else if(message.startsWith("/g info")){
            return GuildInfoCommand.onSendChatMessage(message);
        }
        return true;
    }

    public ChatBuilder getRankBuilder(String rank, String playerName){
        ChatBuilder rankB = new ChatBuilder();
        boolean canAppend = true;
        if (rank.equals("[VIP]")) {
            rankB.setMessage("[VIP] ").setColor(EnumChatFormatting.GREEN);
        } else if (rank.equals("[VIP+]")) {
            rankB.setMessage("[VIP").setColor(EnumChatFormatting.GREEN).append("+", EnumChatFormatting.GOLD)
                    .append("] ", EnumChatFormatting.GREEN);
        } else if (rank.equals("[MVP]")) {
            rankB.setMessage("[MVP] ").setColor(EnumChatFormatting.AQUA);
        } else if (rank.equals("[MVP+]")) {
            rankB.setMessage("[MVP").setColor(EnumChatFormatting.AQUA).append("+", EnumChatFormatting.RED)
                    .append("] ", EnumChatFormatting.AQUA);
        } else if (rank.equals("[JR HELPER]")) {
            rankB.setMessage("[JR HELPER] ").setColor(EnumChatFormatting.BLUE);
        } else if (rank.equals("[HELPER]")) {
            rankB.setMessage("[HELPER] ").setColor(EnumChatFormatting.BLUE);
        } else if (rank.equals("[MOD]")) {
            rankB.setMessage("[MOD] ").setColor(EnumChatFormatting.DARK_GREEN);
        } else if (rank.equals("[ADMIN]")) {
            rankB.setMessage("[ADMIN] ").setColor(EnumChatFormatting.RED);
        } else if (rank.equals("[YT]")) {
            rankB.setMessage("[YT] ").setColor(EnumChatFormatting.GOLD);
        } else if (rank.equals("[BUILD TEAM]")) {
            rankB.setMessage("[BUILD TEAM] ").setColor(EnumChatFormatting.DARK_AQUA);
        } else if (rank.equals("[BUILD TEAM+]")) {
            rankB.setMessage("[BUILD TEAM").setColor(EnumChatFormatting.DARK_AQUA).append("+", EnumChatFormatting.RED)
                    .append("] ", EnumChatFormatting.DARK_AQUA);
        } else if (rank.equals("[MCProHosting]")) {
            rankB.append("[").setColor(EnumChatFormatting.RED).append("MC", EnumChatFormatting.GREEN)
                    .append("ProHosting", EnumChatFormatting.WHITE).append("] ", EnumChatFormatting.RED);
        } else if (rank.equals("[OWNER]")) {
            rankB.setMessage("[OWNER] ").setColor(EnumChatFormatting.RED);
        } else if (rank.equals("[MOJANG]")) {
            rankB.append("[MOJANG] ").setColor(EnumChatFormatting.GOLD);
        } else if (rank.equals("[ANGUS]")) {
            rankB.setMessage("[ANGUS] ").setColor(EnumChatFormatting.RED);
        } else {
            canAppend = false;
            rankB.setMessage(rank).setColor(EnumChatFormatting.GRAY).append(": ", EnumChatFormatting.WHITE);
        }
        if(canAppend) {
            rankB.append(playerName.replaceAll(":", "")).append(": ", EnumChatFormatting.WHITE);
        }
        return rankB;
    }

}