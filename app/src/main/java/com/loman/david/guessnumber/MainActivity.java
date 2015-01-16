package com.loman.david.guessnumber;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends ActionBarActivity {

    private TextView rangeTextView;
    private TextView tipTextView;
    private int targeNumber;
    private int minNumber;
    private int maxNumber;
    private SpeechRecognizer myRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rangeTextView = (TextView) findViewById(R.id.rangeTextView);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=54b7bbff");
        myRecognizer = SpeechRecognizer.createRecognizer(MainActivity.this, initListener);
        setParameter();
    }

    @Override
    protected void onResume() {
        rangeTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                gameInit();
                return true;
            }
        });

        tipTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tipTextView.setText(String.valueOf(targeNumber));
                return true;
            }
        });
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRecognizer.cancel();
        myRecognizer.destroy();
    }

    public void startGame(View view) {
        gameInit();
    }

    public void startPlay(View view) {
        tipTextView.setVisibility(View.VISIBLE);
        tipTextView.setText("dealing...");
        myRecognizer.startListening(recognizerListener);
    }

    public void stopPlay(View view) {
        if (myRecognizer.isListening()) {
            myRecognizer.stopListening();
        }
        if (tipTextView.getVisibility() == View.VISIBLE) {
            tipTextView.setVisibility(View.INVISIBLE);
        }
        if (targeNumber != 0) {
            showResult(false);
        }
    }

    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i) {

        }

        @Override
        public void onBeginOfSpeech() {
            showTip("BeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            showTip("EndOfSpeech");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            tipTextView.setVisibility(View.INVISIBLE);
            ParseJSON parseJSON = new ParseJSON(recognizerResult.getResultString());
            if (parseJSON.getResultString().contains("，") || parseJSON.getResultString().contains("。") || parseJSON.getResultString().contains("十") || parseJSON.getResultString().contains("百")) {
                showTip("Wrong Number Input");
            } else {
                matchNumber(parseJSON.getResultString());
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            speechError.getPlainDescription(true);
        }

        @Override
        public void onEvent(int i, int i2, int i3, Bundle bundle) {

        }
    };

    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                showTip("Can not connect to Internet !");
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void gameInit() {
        minNumber = 11;
        maxNumber = 999;
        targeNumber = getRandomNumber();
        showRange();
        Log.e("Number", String.valueOf(targeNumber));
    }

    private void showRange() {
        rangeTextView.setText("[ " + String.valueOf(minNumber) + " , " + String.valueOf(maxNumber) + " ]");
    }

    private void showTip(String tip) {
        Toast.makeText(MainActivity.this, tip, Toast.LENGTH_SHORT);
    }

    private void showResult(boolean succeed) {
        if (succeed) {
            rangeTextView.setText("Congratulation ,the result is:：" + targeNumber);
        } else {
            rangeTextView.setText("Unfortunately ,the result is：" + targeNumber);
        }

    }

    private int getRandomNumber() {
        int number = 0;
        Random random = new Random();
        while ((number % 10) == 0) {
            number = (random.nextInt(1000) % 990) + 11;
        }
        return number;
    }

    private void matchNumber(String numberString) {
        try {
            int number = Integer.parseInt(numberString);
            if (number < minNumber || number > maxNumber) {
                showTip("Wrong Number Input");
            } else if (number == targeNumber) {
                showResult(true);
                targeNumber = 0;
            } else {
                if (number < targeNumber) {
                    minNumber = number;
                } else if (number > targeNumber) {
                    maxNumber = number;
                }
                showRange();
            }
        }catch (Exception e){
            e.printStackTrace();
            showTip("Data is wrong !");
        }
    }

    private void setParameter() {
        myRecognizer.setParameter(SpeechConstant.PARAMS, null);
        myRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        myRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        myRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        myRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        myRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");
        myRecognizer.setParameter(SpeechConstant.VAD_EOS, "1000");
    }
}
