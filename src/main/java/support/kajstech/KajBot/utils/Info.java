package support.kajstech.KajBot.utils;


public class Info {
    private static final Config CONFIG = new Config();


    public static final String OWNER_ID = CONFIG.getValue("ownerid");
    public static final String PREFIX = CONFIG.getValue("prefix");
    public static final String TOKEN = CONFIG.getValue("token");
    public static final String ADMIN_ID = CONFIG.getValue("admin");

    public static final String LIVE_POST_CHANNEL = CONFIG.getValue("livepostchannel");

    public static final String TWITCH_CHANNEL_ID = CONFIG.getValue("twitchchannelid");
    public static final String TWITCH_CLIENT_ID = CONFIG.getValue("twitchclientid");
    public static final String TWITCHCHECK = CONFIG.getValue("twitchcheck");

    public static final String YT_CHANNEL_ID = CONFIG.getValue("ytchannelid");
    public static final String YT_API_KEY = CONFIG.getValue("ytapikey");
    public static final String YTCHECK = CONFIG.getValue("ytcheck");

    public static final String BLACKLISTED = CONFIG.getValue("blacklisted");
    public static final String BLACKLIST_ENABLED = CONFIG.getValue("blacklistenabled");
    public static final String BLACKLIST_LINKS_ENABLED = CONFIG.getValue("blacklistlinksenabled");
    public static final String BLACKLIST_BYPASS_ID = CONFIG.getValue("blacklistbypass");

    public static final String DEFAULT_GAME = CONFIG.getValue("defaultgame");
}