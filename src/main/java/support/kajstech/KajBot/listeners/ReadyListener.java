package support.kajstech.KajBot.listeners;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import support.kajstech.KajBot.KajBot;
import support.kajstech.KajBot.utils.Info;
import support.kajstech.KajBot.utils.LogHelper;

import java.awt.*;

public class ReadyListener extends ListenerAdapter {

    public void onReady(ReadyEvent event) {

        System.out.println("\n");

        LogHelper.info( "Logged in...");
        LogHelper.info( "Using command prefix: " + Info.PREFIX);
        LogHelper.info( "Current ping: " + event.getJDA().getPing() + "ms");

    }


}
