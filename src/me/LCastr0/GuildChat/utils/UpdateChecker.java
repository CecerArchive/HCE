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

public class UpdateChecker {

    private Minecraft mc = Minecraft.getMinecraft();
    private boolean hasSent = false;

    public UpdateChecker(){}

    public void check(int version){
        if(this.mc.currentScreen == null) {
            try {
                URL url = new URL("http://lcastr0.com/gchatv.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                while ((str = in.readLine()) != null) {
                    if (Integer.valueOf(str) > version) {
                        notifyPlayer();
                        this.hasSent = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyPlayer(){
        if(!hasSent) {
            ChatBuilder chatBuilder = new ChatBuilder();
            chatBuilder.setMessage("There is a new version of the HCE Mod available!").setBold(true)
                    .setColor(EnumChatFormatting.GREEN);
            this.mc.thePlayer.addChatMessage(chatBuilder.build());
            chatBuilder.setMessage("Download it at ");
            ChatBuilder link = new ChatBuilder();
            link.setMessage("http://lcastr0.com/hce").setColor(EnumChatFormatting.AQUA).setBold(true).setItalic(true)
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://lcastr0.com/hce/"))
                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(EnumChatFormatting.YELLOW.toString()
                            + "Click to update!")));
            chatBuilder.append(link);
            this.mc.thePlayer.addChatMessage(chatBuilder.build());
        }
    }

}