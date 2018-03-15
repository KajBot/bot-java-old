package support.kajstech.KajBot.command;


import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;

import java.util.*;

public class PermitCommand extends Command {

    public static List<Member> permitted = new ArrayList<>();

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {

        switch (args.length) {
            case 1:
                if (!IKajBot.isAdmin(e.getMember())) {
                    chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
                    return;
                }

                if (!e.getMessage().getMentionedUsers().isEmpty()) {
                    permitted.add(e.getMessage().getMentionedMembers().get(0));
                    chat.sendMessage(e.getMessage().getMentionedUsers().get(0) + " har nu adgang til at sende blacklisted links de næste 60 sekunder!");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            permitted.remove(e.getMessage().getMentionedMembers().get(0));
                        }
                    }, 60000);
                }
        }

    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("permit");
    }
}