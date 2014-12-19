package me.LCastr0.GuildChat.commands;

import me.LCastr0.GuildChat.messages.ChatBuilder;
import me.LCastr0.GuildChat.utils.GameInfoReader;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.EnumChatFormatting;

import java.util.Timer;
import java.util.TimerTask;

public class GameInfoCommand {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static boolean onSendChatMessage(String message){
        if(message.startsWith("/pginfo")){
            final String[] msg = message.split(" ");
            if(msg.length >= 3){
                if(GameInfoReader.canCheck(msg[1].toLowerCase())) {
                    ChatBuilder wait = new ChatBuilder();
                    wait.setMessage("Connecting with the website... Please wait!").setColor(EnumChatFormatting.AQUA);
                    mc.thePlayer.addChatMessage(wait.build());
                    final GameInfoReader gameInfoReader = new GameInfoReader(msg[2], msg[1].toLowerCase());
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            gameInfoReader.sendMessageToPlayer();
                        }
                    }, 3 * 1000);
                } else {
                    ChatBuilder availableGames = new ChatBuilder();
                    ChatBuilder hoverBuilder = new ChatBuilder();
                    ChatBuilder quakeBuilder = new ChatBuilder();
                    ChatBuilder paintballBuilder = new ChatBuilder();
                    ChatBuilder cvcBuilder = new ChatBuilder();
                    ChatBuilder megawallsBuilder = new ChatBuilder();
                    ChatBuilder wallsBuilder = new ChatBuilder();
                    ChatBuilder bsgBuilder = new ChatBuilder();
                    ChatBuilder vampirezBuilder = new ChatBuilder();
                    ChatBuilder tntgamesBuilder = new ChatBuilder();
                    ChatBuilder arenaBuilder = new ChatBuilder();
                    ChatBuilder arcadeBuilder = new ChatBuilder();
                    ChatBuilder commaBuilder = new ChatBuilder();
                    hoverBuilder.setMessage("Click here to see the stats of this player in this game!").setColor(EnumChatFormatting.AQUA);
                    commaBuilder.setMessage(", ").setColor(EnumChatFormatting.RED);
                    quakeBuilder.setMessage("Quake").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo quake " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    paintballBuilder.setMessage("Paintball").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo paintball " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    cvcBuilder.setMessage("Cvc").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo cvc " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    megawallsBuilder.setMessage("MegaWalls").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo mw " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    wallsBuilder.setMessage("Walls").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo walls " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    bsgBuilder.setMessage("Blitz Survival Games").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo bsg " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    vampirezBuilder.setMessage("VampireZ").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo vampirez " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    tntgamesBuilder.setMessage("TNT Games").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo tnt " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    arenaBuilder.setMessage("Arena").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo arena " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    arcadeBuilder.setMessage("Arcade").setColor(EnumChatFormatting.GOLD).setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/pginfo arcade " + msg[2])).setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverBuilder.build()));
                    availableGames.setMessage("Invalid game type! ").setColor(EnumChatFormatting.RED)
                            .append("(Valid game types: ", EnumChatFormatting.RED).append(quakeBuilder).append(commaBuilder).append(paintballBuilder)
                            .append(commaBuilder).append(cvcBuilder).append(commaBuilder).append(megawallsBuilder).append(commaBuilder)
                            .append(wallsBuilder).append(commaBuilder).append(bsgBuilder).append(commaBuilder)
                            .append(vampirezBuilder).append(commaBuilder).append(tntgamesBuilder).append(commaBuilder).append(arenaBuilder)
                            .append(" and ", EnumChatFormatting.RED).append(arcadeBuilder).append("!)", EnumChatFormatting.RED);
                    mc.thePlayer.addChatMessage(availableGames.build());
                }
            } else {
                ChatBuilder needName = new ChatBuilder();
                needName.setMessage("You must specify a player and a game to be checked! (/pginfo <quake | paintball | megawalls " +
                        "| walls | bsg | vampirez | tnt | arena | arcade>" +
                        " <player>)").setColor(EnumChatFormatting.RED);
                mc.thePlayer.addChatMessage(needName.build());
            }
            return false;
        }
        return true;
    }

}