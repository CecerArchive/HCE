package me.LCastr0.GuildChat.messages;

import me.LCastr0.GuildChat.LiteModGuildChat;
import me.LCastr0.GuildChat.utils.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GuildChatFilter {

    private boolean isToggled = false;
    private Minecraft mc = Minecraft.getMinecraft();
    private LiteModGuildChat mod = LiteModGuildChat.getInstance();

    public GuildChatFilter(){}

    public boolean onChat(IChatComponent chat, String message){
        if(this.isToggled){
            if(chat.getUnformattedText().startsWith("Guild >")){
                if(message.contains(this.mc.thePlayer.getName())){
                    sendColoredMessageToPlayer("The Guild Chat is now toggled on!", EnumChatFormatting.DARK_GREEN);
                    this.isToggled = false;
                    sendColoredMessageToPlayer("The Guild Chat was automatically toggled because your name was mentioned!",
                            EnumChatFormatting.YELLOW);
                    this.mc.thePlayer.playSound("note.pling", 1F, 1F);
                    sendColoredMessageToPlayer("You have received " + GuildMessageStorer.getCount() + " message(s)! To read them, press "
                                    + mod.keyReadGuildName + "!", EnumChatFormatting.AQUA);
                    return true;
                }
                for(String s : mod.aliases){
                    if(message.contains(s)){
                        sendColoredMessageToPlayer("The Guild Chat is now toggled on!", EnumChatFormatting.DARK_GREEN);
                        this.isToggled = false;
                        sendColoredMessageToPlayer("The Guild Chat was automatically toggled because your name was mentioned!",
                                EnumChatFormatting.YELLOW);
                        this.mc.thePlayer.playSound("note.pling", 1F, 1F);
                        sendColoredMessageToPlayer("You have received " + GuildMessageStorer.getCount() + " message(s)! To read them, press "
                                        + mod.keyReadGuildName + "!", EnumChatFormatting.AQUA);
                        return true;
                    }
                }
                GuildMessageStorer.addMessage(chat);
                return false;
            }
        }
        return true;
    }

    public void setToggled(boolean isToggled){
        this.isToggled = isToggled;
    }

    public boolean isToggled(){
        return this.isToggled;
    }

    public void addGui(){
        if(this.mc.currentScreen == null) {
            if (this.isToggled()) {
                this.mc.fontRendererObj.drawString("Guild Chat -> " + GuildMessageStorer.getCount() + " message(s)!", 5, 5, 0xFFAA00);
            }
        }
    }

    private void sendColoredMessageToPlayer(String message, EnumChatFormatting color){
        ChatBuilder chatBuilder = new ChatBuilder();
        chatBuilder.setMessage(message).setColor(color).setBold(true);
        this.mc.thePlayer.addChatMessage(chatBuilder.build());
    }

}