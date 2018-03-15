package support.kajstech.KajBot.command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class InfoCommand extends Command {

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {

        switch (args.length) {
            case 0:
                embedUser(e.getAuthor(), e.getMember(), e);
                break;

            case 1:
                if (!IKajBot.isAdmin(e.getMember())) { chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!"); return; }

                List <User> userMention = e.getMessage().getMentionedUsers();
                for(User user : userMention) {
                    embedUser(user, e.getGuild().getMember(user), e);
                }
                break;
        }


    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("info");
    }

    private void embedUser(User user, Member member, MessageReceivedEvent e)
    {
        String name, id, dis, nickname, icon, status, game, join, register;

        icon = user.getEffectiveAvatarUrl();

        /* Identity */
        name = user.getName();
        id = user.getId();
        dis = user.getDiscriminator();
        nickname = member == null || member.getNickname() == null ? name : member.getEffectiveName();

        /* Status */
        OnlineStatus stat = member == null ? null : member.getOnlineStatus();
        status = stat == null ? "N/A" : VariableToString("_", stat.getKey());
        game = stat == null ? "N/A" : member.getGame() == null ? "N/A" : member.getGame().getName();

        /* Time */
            join = member == null ? "N/A" : DateTimeFormatter.ofPattern("d/M/u HH:mm:ss").format(member.getJoinDate());
        register = DateTimeFormatter.ofPattern("d/M/u HH:mm:ss").format(user.getCreationTime());

        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(nickname, null, icon).setThumbnail(icon);

        embed.addField(":spy: Identitet", "ID `"+id+"`\n"+
                "Brugernavn: `"+name+ "#" +dis+"`", true);

        embed.addField(":first_quarter_moon: Status", "Spil: `"+game+"`\nStatus: `"+status+"`\n", true);

        embed.addField(":stopwatch: Tid", "Tilsluttet: `"+join+"`\n"+
                "Tilmeldt: `"+register+"`\n", true);

        e.getChannel().sendMessage(embed.build()).queue();
    }

    private static String VariableToString(String regex, String input) {
        String[] splitting = new String[] {input};
        if(regex!=null) splitting = input.split(regex);
        String splitted = "";
        for (String s : splitting) {
            splitted += s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1).toLowerCase(Locale.ENGLISH) + " ";
        }
        return splitted.substring(0,splitted.length()-1);
    }

}