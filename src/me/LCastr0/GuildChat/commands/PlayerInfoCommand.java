package me.LCastr0.GuildChat.commands;

import me.LCastr0.GuildChat.messages.ChatBuilder;
import me.LCastr0.GuildChat.utils.PlayerInfoReader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerInfoCommand {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static boolean onSendChatMessage(String message){
        if(message.startsWith("/pinfo")){
            final String[] msg = message.split(" ");
            if(msg.length >= 2){
                ChatBuilder wait = new ChatBuilder();
                wait.setMessage("Connecting with the website... Please wait!").setColor(EnumChatFormatting.AQUA);
                mc.thePlayer.addChatMessage(wait.build());
                final PlayerInfoReader playerInfoReader = new PlayerInfoReader(msg[1]);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        playerInfoReader.sendMessageToPlayer();
                    }
                }, 3 * 1000);
            } else {
                ChatBuilder needName = new ChatBuilder();
                needName.setMessage("You must specify a player to be checked!").setColor(EnumChatFormatting.RED);
                mc.thePlayer.addChatMessage(needName.build());
            }
            return false;
        }
        return true;
    }

}