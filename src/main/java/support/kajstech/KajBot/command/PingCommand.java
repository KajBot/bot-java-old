package support.kajstech.KajBot.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;

import java.util.Collections;
import java.util.List;

public class PingCommand extends Command {

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        switch (args.length) {
            case 0:
                if (!IKajBot.isAdmin(e.getMember())) {
                    chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
                    return;
                }

                if (args.length == 0) {
                    long time = System.currentTimeMillis();
                    String respond = "Pong:";
                    e.getChannel().sendMessage(respond).queue((Message m) ->
                            m.editMessageFormat(respond + " `%d` ms.\n Heartbeat: `%d` ms.", System.currentTimeMillis() - time, e.getJDA().getPing()).queue());
                }

                break;
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("ping");
    }


}