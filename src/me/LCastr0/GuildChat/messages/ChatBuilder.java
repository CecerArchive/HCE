package me.LCastr0.GuildChat.messages;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;

public class ChatBuilder {

    private IChatComponent iChatComponent;
    private String message;
    private EnumChatFormatting color;
    private boolean bold = false;
    private boolean italic = false;
    private boolean underlined = false;
    private boolean strikethrough = false;
    private boolean obfuscated = false;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private List<ChatBuilder> appends = new ArrayList<ChatBuilder>();

    public ChatBuilder(){}

    public ChatBuilder setMessage(String message){
        this.message = message;
        return this;
    }

    public ChatBuilder setColor(EnumChatFormatting color){
        this.color = color;
        return this;
    }

    public ChatBuilder setBold(boolean bold){
        this.bold = bold;
        return this;
    }

    public ChatBuilder setItalic(boolean italic){
        this.italic = italic;
        return this;
    }

    public ChatBuilder setUnderlined(boolean underlined){
        this.underlined = underlined;
        return this;
    }

    public ChatBuilder setStrikethrough(boolean strikethrough){
        this.strikethrough = strikethrough;
        return this;
    }

    public ChatBuilder setObfuscated(boolean obfuscated){
        this.obfuscated = obfuscated;
        return this;
    }

    public ChatBuilder setChatClickEvent(ClickEvent clickEvent){
        this.clickEvent = clickEvent;
        return this;
    }

    public ChatBuilder setChatHoverEvent(HoverEvent hoverEvent){
        this.hoverEvent = hoverEvent;
        return this;
    }

    public ChatBuilder append(ChatBuilder chatBuilder){
        appends.add(chatBuilder);
        return this;
    }

    public ChatBuilder append(String message, EnumChatFormatting color, boolean bold, boolean italic, boolean underlined, boolean strikethrough,
                              boolean obfuscated){
        ChatBuilder newBuilder = new ChatBuilder();
        newBuilder.setMessage(message).setBold(bold).setItalic(italic).setUnderlined(underlined).setStrikethrough(strikethrough)
            .setObfuscated(obfuscated).setColor(color);
        append(newBuilder);
        return this;
    }

    public ChatBuilder append(String message, EnumChatFormatting color){
        ChatBuilder newBuilder = new ChatBuilder();
        newBuilder.setMessage(message).setColor(color);
        append(newBuilder);
        return this;
    }

    public ChatBuilder append(String message){
        ChatBuilder newBuilder = new ChatBuilder();
        newBuilder.setMessage(message);
        append(newBuilder);
        return this;
    }

    public ChatBuilder appendFromComponent(IChatComponent component){
        ChatBuilder toAppend = new ChatBuilder();
        toAppend.setMessage(component.getFormattedText()).setColor(component.getChatStyle().getColor())
            .setBold(component.getChatStyle().getBold()).setItalic(component.getChatStyle().getItalic())
            .setUnderlined(component.getChatStyle().getUnderlined()).setStrikethrough(component.getChatStyle().getStrikethrough())
            .setObfuscated(component.getChatStyle().getObfuscated()).setChatClickEvent(component.getChatStyle().getChatClickEvent())
            .setChatHoverEvent(component.getChatStyle().getChatHoverEvent());
        appends.add(toAppend);
        return this;
    }

    public ChatBuilder clearAppends(){
        appends.clear();
        return this;
    }

    public String getMessage(boolean countAppends){
        if(countAppends){
            StringBuilder builder = new StringBuilder();
            builder.append(this.message);
            for(ChatBuilder chatBuilder : appends){
                builder.append(chatBuilder.message);
            }
            return builder.toString();
        }
        return this.message;
    }

    public int getTotalLength(boolean countAppends){
        if(countAppends)
            return getMessage(true).length();
        return getMessage(false).length();
    }

    public IChatComponent build(){
        if(this.message != null){
            this.iChatComponent = new ChatComponentText(this.message);
            if(this.color != null){
                this.iChatComponent.getChatStyle().setColor(this.color);
            }
            this.iChatComponent.getChatStyle().setBold(this.bold);
            this.iChatComponent.getChatStyle().setItalic(this.italic);
            this.iChatComponent.getChatStyle().setUnderlined(this.underlined);
            this.iChatComponent.getChatStyle().setStrikethrough(this.strikethrough);
            this.iChatComponent.getChatStyle().setObfuscated(this.obfuscated);
            if(this.clickEvent != null){
                this.iChatComponent.getChatStyle().setChatClickEvent(this.clickEvent);
            }
            if(this.hoverEvent != null){
                this.iChatComponent.getChatStyle().setChatHoverEvent(this.hoverEvent);
            }
            for(ChatBuilder chatBuilder : this.appends){
                this.iChatComponent.appendSibling(chatBuilder.build());
            }
            return this.iChatComponent;
        }
        return new ChatComponentText("");
    }

}