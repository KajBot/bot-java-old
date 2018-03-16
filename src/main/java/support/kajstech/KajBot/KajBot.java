package support.kajstech.KajBot;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.InterfacedEventManager;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import support.kajstech.KajBot.command.*;
import support.kajstech.KajBot.listeners.BlacklistListener;
import support.kajstech.KajBot.listeners.ReadyListener;
import support.kajstech.KajBot.utils.*;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KajBot extends ListenerAdapter {

    public static void main(String[] args) {

        LogHelper.info(getVersionInfo());

        String javaVersionMinor = null;
        try {
            javaVersionMinor = System.getProperty("java.version").split("\\.")[1];
        } catch (Exception e) {
            LogHelper.error(KajBot.class, "Exception while checking if Java 8");
        }
        if (!Objects.equals(javaVersionMinor, "8")) {
            LogHelper.warning(KajBot.class, "\n\t\t __      ___   ___ _  _ ___ _  _  ___ \n" +
                    "\t\t \\ \\    / /_\\ | _ \\ \\| |_ _| \\| |/ __|\n" +
                    "\t\t  \\ \\/\\/ / _ \\|   / .` || || .` | (_ |\n" +
                    "\t\t   \\_/\\_/_/ \\_\\_|_\\_|\\_|___|_|\\_|\\___|\n" +
                    "\t\t                                      ");
            LogHelper.warning(KajBot.class, "KajBot only officially supports Java 8. You are running Java" + System.getProperty("java.version"));
        }


        JDABuilder builder = new JDABuilder(AccountType.BOT);

        builder.setAutoReconnect(true);
        builder.setToken(Info.TOKEN);
        builder.setAudioEnabled(true);
        builder.setAudioSendFactory(new NativeAudioSendFactory());
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setGame(Game.watching(Info.DEFAULT_GAME));
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setEventManager(new ThreadedEventManager());

        builder.addEventListener(new BlacklistListener());
        builder.addEventListener(new ReadyListener());

        builder.addEventListener(new MusicCommand());
        builder.addEventListener(new GameCommand());
        builder.addEventListener(new PingCommand());
        builder.addEventListener(new EvalCommand());
        builder.addEventListener(new ClearCommand());
        builder.addEventListener(new AsciiCommand());
        builder.addEventListener(new InfoCommand());
        builder.addEventListener(new PermitCommand());


        try {
            builder.buildAsync();
        } catch (LoginException e) {
            e.printStackTrace();
        }

    }

    private static String getVersionInfo() {
        return "\n\n" +
                "    _   ,         __        \n" +
                "   ' ) /         /  )    _/_\n" +
                "    /-<  __.  o /--<  __ /  \n" +
                "   /   )(_/|_/_/___/_(_)<__ \n" +
                "            /               \n" +
                "          -'                "

                + "\n\tVersion:              " + IKajBot.getAppInfo().VERSION
                + "\n\tBuild Timestamp:      " + IKajBot.getAppInfo().BUILD_TIMESTAMP
                + "\n\tJVM:                  " + System.getProperty("java.version")
                + "\n\tJDA:                  " + JDAInfo.VERSION
                + "\n\tLavaPlayer:           " + PlayerLibrary.VERSION
                + "\n";
    }

    public static void updateStream(ReadyEvent event) {
        while (true) {
            try {
                if (Info.TWITCHCHECK.equalsIgnoreCase("true")) TwitchHelper.refresh(event);
                if (Info.YTCHECK.equalsIgnoreCase("true")) YouTubeHelper.refresh(event);
                Thread.sleep(60000);
            } catch (IOException | InterruptedException io) {
                io.printStackTrace();
            }
        }
    }

    private static class ThreadedEventManager extends InterfacedEventManager {
        private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        @Override
        public void handle(Event e) {
            threadPool.submit(() -> super.handle(e));
        }
    }

}