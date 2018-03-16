package support.kajstech.KajBot.utils;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IKajBot {


    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }


    public static boolean isAdmin(Member member) {
        return member.getRoles().stream().anyMatch(r -> r.getId().equals(Info.ADMIN_ID));
    }

    public static IKajBot getAppInfo() {
        return IKajBot.AppInfoHolder.INSTANCE;
    }

    private static final class AppInfoHolder {
        private static final IKajBot INSTANCE = new IKajBot();
    }

    public final String VERSION;
    public final String BUILD_NUMBER;
    public final String BUILD_TIMESTAMP;

    private IKajBot() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/app.properties");
        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
        } catch (IOException e) {
            LogHelper.error(IKajBot.class, "Failed to load app.properties");
        }
        this.VERSION = prop.getProperty("version");
        this.BUILD_NUMBER = prop.getProperty("buildNumber");
        this.BUILD_TIMESTAMP = prop.getProperty("buildTimestamp");
    }



    public static boolean canNotTalk(TextChannel channel) {
        if (channel == null) return true;
        Member member = channel.getGuild().getSelfMember();
        return member == null
                || !member.hasPermission(channel, Permission.MESSAGE_READ)
                || !member.hasPermission(channel, Permission.MESSAGE_WRITE);
    }

    private static void sendMessage(Message message, MessageChannel channel) {
        if (channel instanceof TextChannel && canNotTalk((TextChannel) channel)) return;
        channel.sendMessage(message).queue(null, null);
    }

    public static void sendMessage(MessageEmbed embed, MessageChannel channel) {
        sendMessage(new MessageBuilder().setEmbed(embed).build(), channel);
    }

    public static void sendMessage(String message, MessageChannel channel) {
        sendMessage(new MessageBuilder().append(filter(message)).build(), channel);
    }

    private static String filter(String msgContent) {
        return msgContent.length() > 2000
                ? "*The output message is over 2000 characters!*"
                : msgContent.replace("@everyone", "@\u180Eeveryone").replace("@here", "@\u180Ehere");
    }

    public static String userDiscrimSet(User u) {
        return stripFormatting(u.getName()) + "#" + u.getDiscriminator();
    }

    public static String stripFormatting(String s) {
        return s.replace("*", "\\*")
                .replace("`", "\\`")
                .replace("_", "\\_")
                .replace("~~", "\\~\\~")
                .replace(">", "\u180E>");
    }

}
