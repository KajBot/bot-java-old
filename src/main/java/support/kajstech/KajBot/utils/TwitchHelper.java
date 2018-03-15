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




    public static void refresh(ReadyEvent jda) throws IOException {
        if(TwitchHelper.checkIfOnline()){
            jda.getJDA().getTextChannelById(Info.TWITCH_POST_CHANNEL).sendMessage(TwitchHelper.getName() + " er live lige nu, og der bliver spillet " +  TwitchHelper.getGame() + "! Se med her " + TwitchHelper.getURL()).queue();
        }
    }






    private static String channel = Info.TWITCH_CHANNEL_ID;

    private static boolean checkIfOnline() throws IOException {
        String streamsUrl = "https://api.twitch.tv/kraken/streams/" + channel + "/?client_id=" + Info.TWITCH_CLIENT_ID + "&api_version=5";


        String jsonStreams = readFromUrl(streamsUrl);// reads text from URL
        JSONObject json = new JSONObject(jsonStreams);

        return !json.isNull("stream");
    }

    public static String getName () throws IOException{

        String channelsUrl = "https://api.twitch.tv/kraken/channels/" + channel + "/?client_id=" + Info.TWITCH_CLIENT_ID + "&api_version=5";
        String jsonChannels = readFromUrl(channelsUrl);// reads text from URL
        JSONObject jsonC = new JSONObject(jsonChannels);

        return jsonC.getString("name");
    }

    private static String getGame() throws IOException{

        String channelsUrl = "https://api.twitch.tv/kraken/channels/" + channel + "/?client_id=" + Info.TWITCH_CLIENT_ID + "&api_version=5";
        String jsonChannels = readFromUrl(channelsUrl);// reads text from URL
        JSONObject jsonC = new JSONObject(jsonChannels);

        return jsonC.getString("game");
    }

    private static String getURL() throws IOException{

        String channelsUrl = "https://api.twitch.tv/kraken/channels/" + channel + "/?client_id=" + Info.TWITCH_CLIENT_ID + "&api_version=5";
        String jsonChannels = readFromUrl(channelsUrl);// reads text from URL
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
