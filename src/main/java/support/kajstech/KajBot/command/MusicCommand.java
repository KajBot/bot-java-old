package support.kajstech.KajBot.command;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;
import support.kajstech.KajBot.audio.AudioInfo;
import support.kajstech.KajBot.audio.AudioPlayerSendHandler;
import support.kajstech.KajBot.audio.TrackManager;
import support.kajstech.KajBot.utils.IKajBot;
import support.kajstech.KajBot.utils.Info;

import java.util.*;

public class MusicCommand extends Command {

    private static final int PLAYLIST_LIMIT = 200;
    private static final AudioPlayerManager myManager = new DefaultAudioPlayerManager();
    private static final Map<String, Map.Entry<AudioPlayer, TrackManager>> players = new HashMap<>();

    private static final String CD = "\uD83D\uDCBF";
    private static final String DVD = "\uD83D\uDCC0";
    private static final String MIC = "\uD83C\uDFA4 **|>** ";

    private static final String QUEUE_TITLE = "__%s har tilføjet %d nye sange%s til playlisten:__";
    private static final String QUEUE_DESCRIPTION = "%s **|>**  %s\n%s\n%s %s\n%s";
    private static final String QUEUE_INFO = "Sange på playlisten: (Længde - %d)";
    private static final String ERROR = "Fejl i indlæsning af \"%s\"";

    public MusicCommand() {
        AudioSourceManagers.registerRemoteSources(myManager);
    }

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        Guild guild = e.getGuild();
        switch (args.length) {
            case 0: // Show help message
                sendHelpMessage(chat);
                break;

            case 1:
                switch (args[0].toLowerCase()) {
                    case "help":
                    case "commands":
                    case "kommandoer":
                        sendHelpMessage(chat);
                        break;

                    case "sang":
                    case "info": // Display song info
                        if (!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) { // No song is playing
                            chat.sendMessage("Ingen sang bliver afspillet i øjeblikket! *It's your time to shine..*");
                        } else {
                            AudioTrack track = getPlayer(guild).getPlayingTrack();
                            chat.sendEmbed("Sang information", String.format(QUEUE_DESCRIPTION, CD, getOrNull(track.getInfo().title),
                                    "\n\u23F1 **|>** `[ " + getTimestamp(track.getPosition()) + " / " + getTimestamp(track.getInfo().length) + " ]`",
                                    "\n" + MIC, getOrNull(track.getInfo().author),
                                    "\n\uD83C\uDFA7 **|>**  " + IKajBot.userDiscrimSet(getTrackManager(guild).getTrackInfo(track).getAuthor().getUser())));
                        }
                        break;

                    case "playlist":
                        if (!hasPlayer(guild) || getTrackManager(guild).getQueuedTracks().isEmpty()) {
                            chat.sendMessage("Playlisten er tom! Du kan tilføje en sang med **"
                                    + IKajBot.stripFormatting(Info.PREFIX) + "musik play**!");
                        } else {
                            StringBuilder sb = new StringBuilder();
                            Set<AudioInfo> queue = getTrackManager(guild).getQueuedTracks();
                            queue.forEach(audioInfo -> sb.append(buildQueueMessage(audioInfo)));
                            String embedTitle = String.format(QUEUE_INFO, queue.size());

                            if (sb.length() <= 1960) {
                                chat.sendEmbed(embedTitle, "**>** " + sb.toString());
                            } else {
                                try {
                                    sb.setLength(sb.length() - 1);
                                    HttpResponse response = Unirest.post("https://hastebin.com/documents").body(sb.toString()).asString();
                                    chat.sendEmbed(embedTitle, "[Klik her for den fulde liste](https://hastebin.com/"
                                            + new JSONObject(response.getBody().toString()).getString("key") + ")");
                                } catch (UnirestException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        break;

                    case "skip":
                        if (isIdle(chat, guild)) return;

                        if (isCurrentDj(e.getMember())) {
                            forceSkipTrack(guild, chat);
                        } else {
                            AudioInfo info = getTrackManager(guild).getTrackInfo(getPlayer(guild).getPlayingTrack());
                            if (info.hasVoted(e.getAuthor())) {
                                chat.sendMessage("\u26A0 Du har allerede stemt på at springe den nuværende sang over!");
                            } else {
                                int votes = info.getSkips();
                                if (votes >= 3) { // Skip on 4th vote
                                    getPlayer(guild).stopTrack();
                                    chat.sendMessage("\u23E9 Springer den nuværende sang over.");
                                } else {
                                    info.addSkip(e.getAuthor());
                                    tryToDelete(e.getMessage());
                                    chat.sendMessage("**" + IKajBot.userDiscrimSet(e.getAuthor()) + "** stemte på at springe sangen over! [" + (votes + 1) + "/4]");
                                }
                            }
                        }
                        break;

                    case "forceskip":
                    case "fskip":
                        if (isIdle(chat, guild)) return;

                        if (isCurrentDj(e.getMember()) || IKajBot.isAdmin(e.getMember())) {
                            forceSkipTrack(guild, chat);
                        } else {
                            chat.sendMessage("Du har ikke adgang til at gøre dette!\n"
                                    + "Brug **" + IKajBot.stripFormatting(Info.PREFIX) + "musik skip** for at stemme!");
                        }
                        break;

                    case "reset":
                        if (!IKajBot.isAdmin(e.getMember())) {
                            chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
                            return;
                        }

                        reset(guild);
                        chat.sendMessage("\uD83D\uDD04 Resetter..");
                        break;

                    case "shuffle":
                        if (isIdle(chat, guild)) return;

                        if (!IKajBot.isAdmin(e.getMember())) {
                            chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
                            return;
                        }

                        getTrackManager(guild).shuffleQueue();
                        chat.sendMessage("\u2705 blandede playlisten!");
                        break;
                }
                break;

            default:
                String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                switch (args[0].toLowerCase()) {
                    case "yt": // Query YouTube for a music video
                        if(input.length() >= 100){
                            chat.sendEmbed(ERROR, "\u26A0 Ingen sange blev fundet.");
                        }
                        input = "ytsearch: " + input;
                        // no break;

                    case "afspil": // Play a track
                    case "play":
                        if (args.length <= 1) {
                            chat.sendMessage("Kunne ikke finde noget på det angivet link.");
                        } else {
                            loadTrack(input, e.getMember(), e.getMessage(), chat);
                        }
                        break;
                    case "vol":
                    case "lydstyrke":
                    case "volume":
                        if (!IKajBot.isAdmin(e.getMember())) {
                            chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!");
                            return;
                        }

                        if (IKajBot.isInteger(input)) {
                            Integer VOLUME = Integer.parseInt(input);
                            if(VOLUME < 1 || VOLUME > 200) {
                                chat.sendMessage("\u26D4 Du skal vælge et helt tal mellem 1-200!");
                                return;
                            }
                            try {
                                getPlayer(guild).setVolume(VOLUME);
                                chat.sendMessage("\uD83D\uDCBF Lydstyrken er blevet ændret til " + VOLUME + "%!");
                            } catch (Exception exc) {
                                chat.sendMessage("\u26D4 Fejl i ændring af lydstyrke!");
                            }
                        } else {
                            chat.sendMessage("\u26D4 Du skal vælge et helt tal mellem 1-200!");
                            return;
                        }

                        break;
                }
                break;
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("musik");
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!players.containsKey(event.getGuild().getId()))
            return; //Guild doesn't have a music player

        TrackManager manager = getTrackManager(event.getGuild());
        manager.getQueuedTracks().stream()
                .filter(info -> !info.getTrack().equals(getPlayer(event.getGuild()).getPlayingTrack())
                        && info.getAuthor().getUser().equals(event.getMember().getUser()))
                .forEach(manager::remove);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        reset(event.getGuild());
    }

    private void tryToDelete(Message m) {
        if (m.getGuild().getSelfMember().hasPermission(m.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            m.delete().queue();
        }
    }

    private boolean hasPlayer(Guild guild) {
        return players.containsKey(guild.getId());
    }

    private AudioPlayer getPlayer(Guild guild) {
        AudioPlayer p;
        if (hasPlayer(guild)) {
            p = players.get(guild.getId()).getKey();
        } else {
            p = createPlayer(guild);
        }
        return p;
    }

    private TrackManager getTrackManager(Guild guild) {
        return players.get(guild.getId()).getValue();
    }

    private AudioPlayer createPlayer(Guild guild) {
        AudioPlayer nPlayer = myManager.createPlayer();
        TrackManager manager = new TrackManager(nPlayer);
        nPlayer.addListener(manager);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(nPlayer));
        players.put(guild.getId(), new AbstractMap.SimpleEntry<>(nPlayer, manager));
        return nPlayer;
    }

    private void reset(Guild guild) {
        players.remove(guild.getId());
        getPlayer(guild).destroy();
        getTrackManager(guild).purgeQueue();
        guild.getAudioManager().closeAudioConnection();
    }

    private void loadTrack(String identifier, Member author, Message msg, Command.MessageSender chat) {
        if (author.getVoiceState().getChannel() == null) {
            chat.sendMessage("Du er ikke i en stemmekanal!");
            return;
        }

        Guild guild = author.getGuild();
        getPlayer(guild); // Make sure this guild has a player.

        msg.getTextChannel().sendTyping().queue();
        myManager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                chat.sendEmbed(String.format(QUEUE_TITLE, IKajBot.userDiscrimSet(author.getUser()), 1, ""),
                        String.format(QUEUE_DESCRIPTION, CD, getOrNull(track.getInfo().title), "", MIC, getOrNull(track.getInfo().author), ""));
                getTrackManager(guild).queue(track, author);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (playlist.getSelectedTrack() != null) {
                    trackLoaded(playlist.getSelectedTrack());
                } else if (playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                } else {
                    chat.sendEmbed(String.format(QUEUE_TITLE, IKajBot.userDiscrimSet(author.getUser()), Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT), "s"),
                            String.format(QUEUE_DESCRIPTION, DVD, getOrNull(playlist.getName()), "", "", "", ""));
                    for (int i = 0; i < Math.min(playlist.getTracks().size(), PLAYLIST_LIMIT); i++) {
                        getTrackManager(guild).queue(playlist.getTracks().get(i), author);
                    }
                }
            }

            @Override
            public void noMatches() {
                chat.sendEmbed(String.format(ERROR, identifier), "\u26A0 Ingen sange blev fundet.");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                chat.sendEmbed(String.format(ERROR, identifier), "\u26D4 " + exception.getLocalizedMessage());
            }
        });
        tryToDelete(msg);
    }

    private boolean isCurrentDj(Member member) {
        return getTrackManager(member.getGuild()).getTrackInfo(getPlayer(member.getGuild()).getPlayingTrack()).getAuthor().equals(member);
    }

    private boolean isIdle(MessageSender chat, Guild guild) {
        if (!hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null) {
            chat.sendMessage("Ingen sang bliver spillet i øjeblikket!");
            return true;
        }
        return false;
    }

    private void forceSkipTrack(Guild guild, MessageSender chat) {
        getPlayer(guild).stopTrack();
        chat.sendMessage("\u23E9 Springer sangen over!");
    }

    private void sendHelpMessage(MessageSender chat) {
        chat.sendEmbed("Kajbot", "(musik)\n\t"
                + "\n\t> afspil [url]            - Tilføj en sang til playlisten\n"
                + "\n\t> yt [søgning]         - Søg efter en sang på YouTube\n"
                + "\n\t> playlist                  - Viser den nurværende playliste\n"
                + "\n\t> skip                        - Stem på at springen den nurværende sang over\n"
                + "\n\t> fskip**\\***                     - 'Force skip' en sang\n"
                + "\n\t> shuffle**\\***                 - 'Shuffle' playlisten\n"
                + "\n\t> reset**\\***                    - Resetter musik afspilleren\n"
                + "\n\t> lydstyrke**\\***             - Ændre musikkens lydstyrke\n\n"
                + "kommandoer med **\\***  kræver at du er 'Admin'"
                + "\n"

        );
    }

    private String buildQueueMessage(AudioInfo info) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String title = trackInfo.title;
        long length = trackInfo.length;
        return "`[ " + getTimestamp(length) + " ]` " + title + "\n";
    }

    private String getTimestamp(long milis) {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long mins = Math.floorDiv(seconds, 60);
        seconds = seconds - (mins * 60);
        return (hours == 0 ? "" : hours + ":") + String.format("%02d", mins) + ":" + String.format("%02d", seconds);
    }

    private String getOrNull(String s) {
        return s.isEmpty() ? "N/A" : s;
    }
}