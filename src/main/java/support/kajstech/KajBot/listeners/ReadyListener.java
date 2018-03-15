package support.kajstech.KajBot.listeners;


import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import support.kajstech.KajBot.KajBot;
import support.kajstech.KajBot.utils.Info;
import support.kajstech.KajBot.utils.LogHelper;



public class ReadyListener extends ListenerAdapter {

    public void onReady(ReadyEvent event) {

        LogHelper.info( "Logged in...");
        LogHelper.info( "Using command prefix: " + Info.PREFIX);
        LogHelper.info( "Current ping: " + event.getJDA().getPing() + "ms");

        if(Info.TWITCHCHECK.equalsIgnoreCase("true")) {
            KajBot.updateTwitch(event);
        }

    }


}
