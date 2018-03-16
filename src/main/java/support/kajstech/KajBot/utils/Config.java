package support.kajstech.KajBot.utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class Config {

    private final File configFile = new File("config.json");
    private JSONObject configObject;

    Config() {
        if (!configFile.exists()) {
            create(); // If the config.json file doesn't exist, generate it.
            System.out.println("Created a config file. Please fill in the credentials.");
            System.exit(0);
        }

        JSONObject object = read(configFile);
        if (object.has("token") && object.has("prefix") && object.has("ownerid") && object.has("adminid") && object.has("twitchchannelid") && object.has("twitchclientid") && object.has("livepostchannel") && object.has("twitchcheck") && object.has("blacklisted") && object.has("blacklistenabled") && object.has("blacklistlinksenabled") && object.has("blacklistbypassid") && object.has("defaultgame")) {
            configObject = object;
        } else {
            create(); // If a value is missing, regenerate the config file.
            System.err.println("A value was missing in the config file! Regenerating..");
            System.exit(1);
        }
    }

    String getValue(String key) {
        return configObject == null ? null : configObject.get(key).toString();
    }

    private void create() {
        try {
            Files.write(Paths.get(configFile.getPath()),
                    new JSONObject()
                            .put("ownerid", "")
                            .put("prefix", "-")
                            .put("token", "")
                            .put("adminid", "")

                            .put("livepostchannel", "")

                            .put("twitchchannelid", "")
                            .put("twitchclientid", "")
                            .put("twitchcheck", "false")

                            .put("ytchannelid", "")
                            .put("ytapikey", "")
                            .put("ytcheck", "false")

                            .put("blacklisted", "example.com, anotherexample.com, test phrase, test")
                            .put("blacklistenabled", "false")
                            .put("blacklistlinksenabled", "false")
                            .put("blacklistbypassid", "")

                            .put("defaultgame", "KajsTech.Support")

                            .toString(4)
                            .getBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private JSONObject read(File file) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(new String(Files.readAllBytes(Paths.get(file.getPath())), "UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return obj;
    }
}