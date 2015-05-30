package speech.recognizer;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import javax.speech.Central;
import javax.speech.recognition.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import xadrezautomato.Global;

public class SpeechToTextConverter extends Thread {

    private static final String ACOUSTIC_MODEL
            = "resource:/speech/recognizer/en-us";
    private static final String DICTIONARY_PATH
            = "resource:/speech/recognizer/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH
            = "resource:/speech/recognizer/";

    public LiveSpeechRecognizer recog;

    private static final Map<Integer, String> ROW
            = new HashMap<Integer, String>();

    public String text = "";

    static {
        ROW.put(1, "one");
        ROW.put(2, "two");
        ROW.put(3, "three");
        ROW.put(4, "four");
        ROW.put(5, "five");
        ROW.put(6, "six");
        ROW.put(7, "seven");
        ROW.put(8, "eight");
    }

    private static String parseRow(String comm) {
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= 8; ++i) {
            comm = comm.replaceAll(ROW.get(i), "" + i);
        }

        return comm;
    }

    public static void main(String[] args) throws Exception {
       SpeechToTextConverter speech =  new SpeechToTextConverter();
       speech.createRecog();
       speech.startRecog();
    }

    public void createRecog() {
        try {
            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath(ACOUSTIC_MODEL);
            configuration.setDictionaryPath(DICTIONARY_PATH);
            configuration.setGrammarPath(GRAMMAR_PATH);
            configuration.setUseGrammar(true);

            configuration.setGrammarName("chess");

            recog = new LiveSpeechRecognizer(configuration);

        } catch (IOException ex) {
            Logger.getLogger(SpeechToTextConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void startRecog() {

        recog.startRecognition(true);
        while (true) {

            text = parseRow(recog.getResult().getHypothesis().replaceAll(" ", ""));
            System.out.println(text);

            if (!text.equals("") && !text.equals("<unk>")) {
                Global.src = getSource();
                Global.dest = getDest();
                recog.stopRecognition();
                break;

            }
        }
    }

    public String getSource() {
        if (!text.equals("")) {
            return text.replace("move", "").split("to")[0];
        }
        return null;
    }

    public String getDest() {
        if (!text.equals("")) {
            return text.replace("move", "").split("to")[1];
        }
        return null;
    }

    @Override
    public void run() {
        //createRecog();
        startRecog();
        //notify();
    }

}
