package support.kajstech.KajBot.command;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import support.kajstech.KajBot.utils.IKajBot;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AsciiCommand extends Command {

    @Override
    public void executeCommand(String[] args, MessageReceivedEvent e, MessageSender chat) {
        if (!IKajBot.isAdmin(e.getMember())) { chat.sendMessage("\u26D4 Du har ikke adgang til at gøre dette!"); return; }
        if (args.length == 0) {
            chat.sendEmbed("\n", "Her er en **[fuld liste over Ascii fontsne]("+asciiArtUrl+"fonts_list)**. De bliver tilfældigt valgt.");
        } else {
            String input = "";
            for (int i = 0; i < args.length; i++) { input += i==args.length-1?args[i]:args[i]+" "; }

            List<String> fonts = getAsciiFonts();
            String font = fonts.get(randomNum(0, fonts.size() - 1));

            try {
                String ascii = getAsciiArt(input, font);

                if (ascii.length()>1900) {
                    chat.sendMessage("```fix\n\nAscii teksten er for stor```");
                    return;
                }

                chat.sendMessage("**Font:** " + font + "\n```fix\n\n" + ascii + "```");
            } catch (IllegalArgumentException iae) {
                chat.sendMessage("```fix\n\nDin tekst indeholder ugyldige tegn!```");
            }
        }
    }

    @Override
    public List<String> getAlias() {
        return Collections.singletonList("ascii");
    }


    private static int randomNum(int start, int end) {

        if (end < start) {
            int temp = end;
            end = start;
            start = temp;
        }
        return (int) Math.floor(Math.random() * (end - start + 1) + start);
    }


    private final static String asciiArtUrl = "http://artii.herokuapp.com/";


    private static String getAsciiArt(String ascii, String font) {
        try {
            StringBuilder url = new StringBuilder(asciiArtUrl).append("make").append("?text=").append(ascii.replaceAll(" ", "+"))
                    .append(font==null||font.isEmpty()?"":"&font="+font);
            return Unirest.get(url.toString()).asString().getBody();
        } catch (UnirestException e) {
            return "Fail to get the ascii art.";
        }
    }


    private static List<String> getAsciiFonts() {
        String url = asciiArtUrl + "fonts_list";
        List<String> fontList = new ArrayList<>();
        try {
            String list = Unirest.get(url).asString().getBody();

            fontList = Arrays.stream(list.split("\n")).collect(Collectors.toList());

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        //fontList.forEach(System.out::println);
        return fontList;
    }

}