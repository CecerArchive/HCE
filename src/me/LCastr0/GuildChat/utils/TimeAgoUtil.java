package me.LCastr0.GuildChat.utils;

import java.util.concurrent.TimeUnit;

public class TimeAgoUtil {

    public static String getTimeAgo(long timeBefore){
        long days = TimeUnit.DAYS.convert(getDateDiff(timeBefore), TimeUnit.MILLISECONDS);
        long hours = TimeUnit.HOURS.convert(getDateDiff(timeBefore), TimeUnit.MILLISECONDS);
        long minutes = TimeUnit.MINUTES.convert(getDateDiff(timeBefore), TimeUnit.MILLISECONDS);
        long seconds = TimeUnit.SECONDS.convert(getDateDiff(timeBefore), TimeUnit.MILLISECONDS);
        if(days >= 1){
            return days + " day(s) ago";
        } else if(hours >= 1){
            return hours + " hour(s) ago";
        } else if(minutes >= 1){
            return minutes + " minute(s) ago";
        }
        return seconds + " second(s) ago";
    }

    private static long getDateDiff(long timeBefore){
        long actualTime = System.currentTimeMillis();
        long differenceTime = actualTime - timeBefore;
        return differenceTime;
    }

}