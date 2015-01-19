package com.loman.david.guessnumber;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.loman.david.json.ParseJSON;

import java.util.Random;


public class ClassicGameActivity extends ActionBarActivity {

    private int targe[] = new int[5];
    private int value[] = new int[10];
    private StringBuilder reslutStringBuilder;
    private TextView reslutTextView;
    private TextView guessTextView;
    private TextView errorTextView;
    private Button classicStartButton, classicStopButton;
    private SpeechRecognizer mySpeechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_game);

        reslutTextView = (TextView) findViewById(R.id.resultTextView);
        guessTextView = (TextView) findViewById(R.id.guessTextView);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        classicStartButton = (Button) findViewById(R.id.classicStartButton);
        classicStopButton = (Button) findViewById(R.id.classicStopButton);
        //
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=54b7bbff");
        //
        mySpeechRecognizer = SpeechRecognizer.createRecognizer(this, initListener);
        setParameter();

        guessTextView.setText("来一局");
        classicStartButton.setVisibility(View.INVISIBLE);
        classicStopButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {

        reslutTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                reslutTextView.setText(""+String.valueOf(targe[0]) + String.valueOf(targe[1]) + String.valueOf(targe[2]) + String.valueOf(targe[3]));
                return true;
            }
        });
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mySpeechRecognizer.cancel();
        mySpeechRecognizer.destroy();
        super.onDestroy();
    }

    public void initGame(View view) {
        guessTextView.setText(" ? ");
        errorTextView.setText(" ");
        reslutTextView.setText(" ");
        classicStartButton.setVisibility(View.VISIBLE);
        classicStopButton.setVisibility(View.VISIBLE);
        for (int i = 0; i < 10; i++) {
            value[i] = 0;
        }
        getNumber();
        reslutStringBuilder = new StringBuilder();
    }

    public void startGame(View view) {
        errorTextView.setText(" ");
        guessTextView.setText("处理中......");
        mySpeechRecognizer.startListening(recognizerListener);
    }

    public void stopGame(View view) {
        if (mySpeechRecognizer.isListening()) {
            mySpeechRecognizer.stopListening();
        }

        showResult(false);
    }

    public void getNumber() {
        Random random = new Random();
        int i = 3;
        while (i >= 0) {
            int v = (random.nextInt(10) % 9);
            if (value[v] == 0) {
                value[v]++;
                targe[i--] = v;
            }
        }
    }

    private void setParameter() {
        mySpeechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        mySpeechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mySpeechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        mySpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mySpeechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        mySpeechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
        mySpeechRecognizer.setParameter(SpeechConstant.VAD_EOS, "1000");
    }

    private void matchNumber(String resultString) {
        if (resultString.contains("，") && resultString.equals("，")) {
            return;
        } else if (resultString.contains("。") && resultString.equals("。")) {
            return;
        }else if (resultString.length() < 4){
            errorTextView.setText("输入格式有错");
            guessTextView.setText(" ? ");
            return;
        }else {
            int number = 0;
            int a, b;
            a = b = 0;
            int resluts[] = new int[5];
            int val[] = new int[10];
            for (int i = 0; i < 10; i++) {
                val[i] = value[i];
            }

            try {
                number = Integer.parseInt(resultString);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (number == 0 ) {
                errorTextView.setText("输入格式有错");
                guessTextView.setText(" ? ");
                return;
            } else {
                int i = 3;
                while (i >= 0) {
                    int v = number % 10;
                    if (val[v] > 1) {
                        errorTextView.setText("输入格式有错");
                        guessTextView.setText(" ? ");
                        Log.e("TEST", "Q:" + val[v]);
                        return;
                    } else {
                        if (v == targe[i]) {
                            a++;
                        }
                        if (val[v] == 1) {
                            b++;

                        }
                        val[v]++;
                        resluts[i--] = v;
                        number = number / 10;
                        if ((val[v] - value[v])>1){
                            errorTextView.setText("输入格式有错");
                            guessTextView.setText(" ? ");
                            return;
                        }
                    }
                }

            }

            if (a == 4) {
                showResult(true);
            } else {
                if (b == 4) {
                    guessTextView.setText("你很棒，加油啊");
                } else if (b >= 2) {
                    guessTextView.setText("很好，加油啊");
                } else {
                    guessTextView.setText("别气馁，加油啊");
                }
                reslutStringBuilder.append("\t");
                reslutStringBuilder.append(String.valueOf(resluts[0]));
                reslutStringBuilder.append(String.valueOf(resluts[1]));
                reslutStringBuilder.append(String.valueOf(resluts[2]));
                reslutStringBuilder.append(String.valueOf(resluts[3]));
                reslutStringBuilder.append("\t");
                reslutStringBuilder.append(String.valueOf(a));
                reslutStringBuilder.append("A");
                reslutStringBuilder.append(String.valueOf(b));
                reslutStringBuilder.append("B");
                reslutStringBuilder.append("\n");

                reslutTextView.setText(reslutStringBuilder.toString());
                reslutTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
        }
    }

    private void showResult(boolean isSuccuse) {
        if (isSuccuse) {
            guessTextView.setText("恭喜您，正确答案是：" + String.valueOf(targe[0]) + String.valueOf(targe[1]) + String.valueOf(targe[2]) + String.valueOf(targe[3]));
        } else {
            guessTextView.setText("很不幸，正确答案是：" + String.valueOf(targe[0]) + String.valueOf(targe[1]) + String.valueOf(targe[2]) + String.valueOf(targe[3]));
        }
        reslutTextView.setText(" ");
        errorTextView.setText(" ");
        classicStartButton.setVisibility(View.INVISIBLE);
        classicStopButton.setVisibility(View.INVISIBLE);
    }

    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Log.e("Init", "Error");
            }
        }
    };

    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            ParseJSON parseJSON = new ParseJSON(recognizerResult.getResultString());
            String resultString = parseJSON.getResultString();
            matchNumber(resultString);
        }

        @Override
        public void onError(SpeechError speechError) {
            speechError.getPlainDescription(true);
        }

        @Override
        public void onEvent(int i, int i2, int i3, Bundle bundle) {

        }
    };


}
