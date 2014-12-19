package me.LCastr0.GuildChat.messages;

import net.minecraft.util.IChatComponent;

import java.util.HashMap;
import java.util.Map;

public class GuildMessageStorer {

    private static Map<Integer, IChatComponent> messages = new HashMap<Integer, IChatComponent>();
    private static Map<Integer, Long> times = new HashMap<Integer, Long>();

    public static void addMessage(IChatComponent message){
        messages.put(messages.size(), message);
        times.put(times.size(), System.currentTimeMillis());
    }

    public static void deleteAll(){
        messages.clear();
    }

    public static IChatComponent getMessage(int i){
        return messages.get(i);
    }

    public static long getTime(int i){
        return times.get(i);
    }

    public static int getCount(){
        return messages.size();
    }

}