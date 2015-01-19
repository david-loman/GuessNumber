package com.loman.david.guessnumber;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


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

    public void setSetting(View view){

    }

    public void onExit(View view){
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guide, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
