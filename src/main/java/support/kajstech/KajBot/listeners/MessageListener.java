package support.kajstech.KajBot.listeners;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import support.kajstech.KajBot.command.PermitCommand;
import support.kajstech.KajBot.utils.IKajBot;
import support.kajstech.KajBot.utils.Info;

import java.util.ArrayList;
import java.util.List;


public class MessageListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(event.getAuthor() == event.getJDA().getSelfUser()) return;

        for (String item : Info.BLACKLISTED_WEBSITES.split("\\s*,\\s*")) {
            if(PermitCommand.permitted.contains(event.getMessage().getMember())) return;
            if (!event.isFromType(ChannelType.PRIVATE) && !IKajBot.isAdmin(event.getMember()) && event.getMessage().getContentRaw().contains(item)) {
                IKajBot.sendMessage(event.getMember().getAsMention() + " \u26D4 Hey, dette link er ikke tilladt herinde", event.getTextChannel());
                event.getMessage().delete().queue();
                return;
            }
        }


        if (event.isFromType(ChannelType.PRIVATE))
        {
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                    event.getMessage().getContentRaw());
        }
        else
        {
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                    event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                    event.getMessage().getContentRaw());
        }
    }




}
