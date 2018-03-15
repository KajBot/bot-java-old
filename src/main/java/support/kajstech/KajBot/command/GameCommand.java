package support.kajstech.KajBot.command;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;

import java.util.*;

public class GameCommand extends Command {

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        Guild guild = e.getGuild();
                if (!IKajBot.isAdmin(e.getMember())) { chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!"); return; }
                    StringBuilder sb = new StringBuilder();
                    Arrays.stream(args).forEach(s -> sb.append(s).append(" "));

                    if(sb.toString().length() < 1) {chat.sendMessage("\u26D4 Du mangler at angive en besked!"); return; }

                    guild.getJDA().getPresence().setGame(Game.playing(sb.toString()));
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("spil");
    }
}