package support.kajstech.KajBot.command;


import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;
import support.kajstech.KajBot.utils.Info;

import java.util.*;

public class PermitCommand extends Command {

    public static List<Member> permitted = new ArrayList<>();

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        List<Member> userMention = e.getMessage().getMentionedMembers();

        switch (args.length) {
            case 1:
                if (!IKajBot.isAdmin(e.getMember())) {
                    chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
                    return;
                }
                if (!e.getMessage().getMentionedUsers().isEmpty()) {
                    if(Info.BLACKLIST_ENABLED.equalsIgnoreCase("false") && Info.BLACKLIST_LINKS_ENABLED.equalsIgnoreCase("false")){
                        chat.sendMessage("Hov, blacklist er slået fra!");
                        return;
                    }
                    for (Member member : userMention) {
                        permitted.add(member);
                        chat.sendMessage(member.getAsMention() + " har nu adgang til at sende blacklisted ord/links de næste 60 sekunder!");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                permitted.remove(member);
                            }
                        }, 60000);
                    }
                }
        }

    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("permit");
    }
}