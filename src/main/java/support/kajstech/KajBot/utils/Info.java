package support.kajstech.KajBot.utils;


public class Info {
    private static final Config CONFIG = new Config();
    public static final String AUTHOR_ID = CONFIG.getValue("authorid");
    public static final String TWITCH_POST_CHANNEL = CONFIG.getValue("twitchpostchannel");
    public static final String PREFIX = CONFIG.getValue("prefix");
    public static final String TOKEN = CONFIG.getValue("token");
    public static final String ADMIN_ID = CONFIG.getValue("adminid");
    public static final String TWITCH_CHANNEL_ID = CONFIG.getValue("twitchchannelid");
    public static final String TWITCH_CLIENT_ID = CONFIG.getValue("twitchclientid");
    public static final String TWITCHCHECK = CONFIG.getValue("twitchcheck");
}