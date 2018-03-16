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

public class YouTubeHelper {

    private static String LIVE_API_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&type=video&eventType=live&maxResults=1&channelId=" + Info.YT_CHANNEL_ID + "&key=" + Info.YT_API_KEY;

    private static boolean live = false;

    public static void refresh(ReadyEvent jda) throws IOException {
        if (checkLive()) {
            if (!live) {
                jda.getJDA().getTextChannelById(Info.LIVE_POST_CHANNEL).sendMessage(getName() + " er live lige nu på YouTube! Se med her https://www.youtube.com/watch?v=" + getId()).queue();
                live = true;
            }
        }else {
            live = false;
        }
    }

    private static boolean checkLive() throws IOException {
        String jsonStreams = readFromUrl(LIVE_API_URL);
        JSONObject json = new JSONObject(jsonStreams);

        return json.getJSONArray("items").length() > 0;
    }

    private static String getId() throws IOException {
        if (!checkLive()) return null;
        String jsonChannels = readFromUrl(LIVE_API_URL);
        JSONObject json = new JSONObject(jsonChannels);

        return json.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
    }

    private static String getName() throws IOException {
        if (!checkLive()) return null;
        String jsonChannels = readFromUrl(LIVE_API_URL);
        JSONObject json = new JSONObject(jsonChannels);

        return json.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("channelTitle");
    }


    private static String readFromUrl(String url) throws IOException {
        URL page = new URL(url);
        try (Stream<String> stream = new BufferedReader(new InputStreamReader(
                page.openStream(), StandardCharsets.UTF_8)).lines()) {
            return stream.collect(Collectors.joining(System.lineSeparator()));
        }
    }

}
