package me.LCastr0.GuildChat.commands;

import me.LCastr0.GuildChat.messages.ChatBuilder;
import me.LCastr0.GuildChat.utils.GuildInfoReader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;

import java.util.Timer;
import java.util.TimerTask;

public class GuildInfoCommand {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static boolean onSendChatMessage(String message){
        if(message.startsWith("/g info")){
            final String[] msg = message.split(" ");
            if(msg.length >= 3){
                ChatBuilder wait = new ChatBuilder();
                wait.setMessage("Connecting with the website... Please wait!").setColor(EnumChatFormatting.AQUA);
                mc.thePlayer.addChatMessage(wait.build());
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 2; i < msg.length; i++){
                    if(i != msg.length -1) {
                        stringBuilder.append(msg[i] + " ");
                    } else {
                        stringBuilder.append(msg[i]);
                    }
                }
                final GuildInfoReader guildInfoReader = new GuildInfoReader(stringBuilder.toString());
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        guildInfoReader.sendMessageToPlayer();
                    }
                }, 3 * 1000);
            } else {
                ChatBuilder needName = new ChatBuilder();
                needName.setMessage("You must specify a guild to be checked!").setColor(EnumChatFormatting.RED);
                mc.thePlayer.addChatMessage(needName.build());
            }
            return false;
        }
        return true;
    }

}