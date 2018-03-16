package support.kajstech.KajBot.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClearCommand extends Command {

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        if (!IKajBot.isAdmin(e.getMember())) {
            chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
            return;
        }
        String input = String.join(" ", Arrays.copyOfRange(args, 0, args.length));

        if (IKajBot.isInteger(input)) {
            Integer AMOUNT = Integer.parseInt(input);
            if (AMOUNT < 2 || AMOUNT > 100) {
                chat.sendMessage("\u26D4 Du skal vælge et helt tal mellem 1-100!");
                return;
            }
            try {
                MessageHistory history = new MessageHistory(e.getTextChannel());
                List<Message> msgs;
                msgs = history.retrievePast(AMOUNT).complete();
                e.getTextChannel().deleteMessages(msgs).queue();
                chat.sendMessage("Slettede " + (AMOUNT) + " besked(er)!");
            } catch (Exception exc) {
                chat.sendMessage("\u26D4 En eller flere beskeder er over 14 dage gammel!");
            }
        } else {
            chat.sendMessage("\u26D4 Du skal vælge et helt tal mellem 2-100!");
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("clear");
    }
}