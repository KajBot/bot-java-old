package support.kajstech.KajBot.utils;

import net.dv8tion.jda.core.events.ReadyEvent;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwitchHelper {

    private static String CHANNELS_API = "https://api.twitch.tv/kraken/channels/" + Info.TWITCH_CHANNEL_ID + "/?client_id=" + Info.TWITCH_CLIENT_ID + "&api_version=5";

    private static boolean live = false;

    public static void refresh(ReadyEvent jda) throws IOException {
        if (checkLive()) {
            if (!live) {
                jda.getJDA().getTextChannelById(Info.LIVE_POST_CHANNEL).sendMessage(getName() + " er live lige nu, og der bliver spillet " + getGame() + "! Se med her " + getURL()).queue();
                live = true;
            }
        }else {
            live = false;
        }
    }

    private static boolean checkLive() throws IOException {
        String jsonStreams = readFromUrl("https://api.twitch.tv/kraken/streams/" + Info.TWITCH_CHANNEL_ID + "/?client_id=" + Info.TWITCH_CLIENT_ID + "&api_version=5");
        JSONObject json = new JSONObject(jsonStreams);

        return !json.isNull("stream");
    }

    private static String getName() throws IOException {
        if (!checkLive()) return null;
        String jsonChannels = readFromUrl(CHANNELS_API);
        JSONObject jsonC = new JSONObject(jsonChannels);

        return jsonC.getString("name");
    }

    private static String getGame() throws IOException {
        if (!checkLive()) return null;
        String jsonChannels = readFromUrl(CHANNELS_API);
        JSONObject jsonC = new JSONObject(jsonChannels);

        return jsonC.getString("game");
    }

    private static String getURL() throws IOException {
        if (!checkLive()) return null;
        String jsonChannels = readFromUrl(CHANNELS_API);
        JSONObject jsonC = new JSONObject(jsonChannels);

        return jsonC.getString("url");
    }

    private static String readFromUrl(String url) throws IOException {
        URL page = new URL(url);
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(
                page.openStream(), StandardCharsets.UTF_8)).lines()) {
            return stream.collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
