package com.loman.david.guessnumber;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.Vector;


public class GuideActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
    }

    public void playGame(View view){
        Intent intent =new Intent(GuideActivity.this,NormalGameActivity.class);
        startActivity(intent);
    }

    public void playClassicGame(View view){
        Intent intent=new Intent(GuideActivity.this,ClassicGameActivity.class);
        startActivity(intent);
    }

    public void sendShare(View view){
        Intent sendIntent =new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "语音猜数字，你还在等什么!应用下载：http://davidloman.net/project/GuessNumber/");
        startActivity(Intent.createChooser(sendIntent, "分享给我的朋友"));
    }

    public void getHelp (View view){
        Intent intent =new Intent(this,HelpActivity.class);
        startActivity(intent);
    }

    public void onExit(View view){
        this.finish();
    }

}
