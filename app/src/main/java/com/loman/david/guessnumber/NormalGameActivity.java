package com.loman.david.guessnumber;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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


public class NormalGameActivity extends ActionBarActivity {

    private TextView rangeTextView;
    private TextView tipTextView;
    private TextView errorTextView;
    private int targeNumber;
    private int minNumber;
    private int maxNumber;
    private SpeechRecognizer myRecognizer;
    private Button playButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rangeTextView = (TextView) findViewById(R.id.rangeTextView);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        errorTextView = (TextView) findViewById(R.id.errorTextView);
        playButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        //xunfeiyun init
        SpeechUtility.createUtility(NormalGameActivity.this, SpeechConstant.APPID + "=54b7bbff");
        myRecognizer = SpeechRecognizer.createRecognizer(NormalGameActivity.this, initListener);
        setParameter();
        //srcean init
        initSrcean();
    }

    @Override
    protected void onResume() {
        rangeTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                initGame();
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
        initGame();
    }

    public void startPlay(View view) {
        tipTextView.setVisibility(View.VISIBLE);
        tipTextView.setText("处理中......");
        myRecognizer.startListening(recognizerListener);
        errorTextView.setText("");
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
        errorTextView.setText("");
    }

    private RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i) {

        }

        @Override
        public void onBeginOfSpeech() {
//            showTip("BeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
//            showTip("EndOfSpeech");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            tipTextView.setVisibility(View.INVISIBLE);
            ParseJSON parseJSON = new ParseJSON(recognizerResult.getResultString());
            matchNumber(parseJSON.getResultString());
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
                errorTextView.setText("Can not connect to Internet !");
            }
        }
    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_exit) {
//            this.finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void initGame() {
        minNumber = 11;
        maxNumber = 999;
        targeNumber = getRandomNumber();
        showRange();
        playButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        errorTextView.setText(" ");
    }

    private void showRange() {
        rangeTextView.setText("[ " + String.valueOf(minNumber) + " , " + String.valueOf(maxNumber) + " ]");
    }

//    private void showTip(String tip) {
//        Toast.makeText(NormalGameActivity.this, tip, Toast.LENGTH_SHORT);
//    }

    private void showResult(boolean succeed) {
        if (succeed) {
            rangeTextView.setText("恭喜您，正确答案是：" + targeNumber);
        } else {
            rangeTextView.setText("很不幸，正确答案是：" + targeNumber);
        }
        initSrcean();
    }

    private int getRandomNumber() {
        int number = 0;
        Random random = new Random();
        while ((number % 10) == 0) {
            number = (random.nextInt(1000) % 990) + 11;
        }
        return number;
    }

    private boolean isNumber(String numberString) {
        if (numberString.contains("，") || numberString.contains("。")) {
            if (numberString.equals("，") || numberString.equals("。")) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void matchNumber(String numberString) {
        int number = 0;
        if (isNumber(numberString)) {
            String numbers[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
            String bits[] = {"十", "百", "千", "万", "亿"};
            for (int i = 0; i < bits.length; i++) {
                if (numberString.contains(bits[i])) {
                    number = 1;
                    int log = i;
                    while (log >= 0) {
                        number = number * 10;
                        log--;
                    }
                    for (int j = 0; j < numbers.length; j++) {
                        if (numberString.contains(numbers[j])) {
                            number = number * j;
                        }
                    }
                }
            }
            //Normal Number
            if (number == 0) {
                try {
                    number = Integer.parseInt(numberString);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if (number == 0){
                errorTextView.setText("输入格式有错");
                return;
            }
            if (number < minNumber || number > maxNumber) {
                errorTextView.setText("超出范围了，亲");
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

    private void initSrcean() {
        playButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
    }
}
