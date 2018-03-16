package support.kajstech.KajBot.command;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;

public class EvalCommand extends Command {

    private final ScriptEngine engine;

    public EvalCommand() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        try {
            engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util, Packages.net.dv8tion.jda.core, "
                    + "Packages.net.dv8tion.jda.core.entities, Packages.net.dv8tion.jda.core.managers);");
        } catch (ScriptException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, Command.MessageSender chat) {

        if (!IKajBot.isAdmin(e.getMember())) {
            chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
            return;
        }

        String allArgs = e.getMessage().getContentDisplay();
        if (allArgs.contains(" ")) {
            allArgs = allArgs.substring(allArgs.indexOf(' ')).trim();
        }

        engine.put("e", e);
        engine.put("event", e);
        engine.put("api", e.getJDA());
        engine.put("jda", e.getJDA());
        engine.put("chat", chat);
        engine.put("channel", e.getChannel());
        engine.put("author", e.getAuthor());
        engine.put("member", e.getMember());
        engine.put("message", e.getMessage());
        engine.put("guild", e.getGuild());
        engine.put("input", allArgs);
        engine.put("selfUser", e.getJDA().getSelfUser());
        engine.put("selfMember", e.getGuild() == null ? null : e.getGuild().getSelfMember());
        engine.put("mentionedUsers", e.getMessage().getMentionedUsers());
        engine.put("mentionedRoles", e.getMessage().getMentionedRoles());
        engine.put("mentionedChannels", e.getMessage().getMentionedChannels());

        Object out;
        try {
            out = engine.eval("(function() { with (imports) {\n" + allArgs + "\n} })();");
        } catch (Exception ex) {
            chat.sendMessage("**Exception**: ```\n" + ex.getLocalizedMessage() + "```");
            return;
        }

        String outputS;
        if (out == null) {
            outputS = "`Task executed without errors.`";
        } else {
            outputS = "Output: ```\n" + out.toString().replace("`", "\\`") + "\n```";
        }

        if (e.getJDA().getStatus() != JDA.Status.SHUTDOWN) {
            chat.sendMessage(outputS);
        } else {
            System.exit(0);
        }
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("eval", "evaluate", "exec", "execute");
    }

    @Override
    public boolean allowsPrivate() {
        return true;
    }

    @Override
    public boolean authorExclusive() {
        return true;
    }
}