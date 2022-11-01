package client.halouhuandian.app15.service.logic.logicTTL;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TextToSpeechController {

    private static volatile TextToSpeechController textToSpeechController;
    private TextToSpeechController(){};
    public static TextToSpeechController getInstance(){
        if(textToSpeechController == null){
            synchronized (TextToSpeechController.class){
                if(textToSpeechController == null){
                    textToSpeechController = new TextToSpeechController();
                }
            }
        }
        return textToSpeechController;
    }

    //ttl语音朗读
    protected TextToSpeech textToSpeech;

    public void init(Context context){
        if(textToSpeech == null){
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == textToSpeech.SUCCESS) {
                        int result = textToSpeech.setLanguage(Locale.CHINA);
                        if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE && result != TextToSpeech.LANG_AVAILABLE) {
                            System.out.println("TTS暂时不支持这种语音的朗读！");
                        }
                    }
                }
            });
        }
    }

    public void onSpeak(String info){
        textToSpeech.speak(info, TextToSpeech.QUEUE_ADD, null);
    }
}
