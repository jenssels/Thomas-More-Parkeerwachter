package be.thomasmore.tm_parkeerwachter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import be.thomasmore.tm_parkeerwachter.Session.Session;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        toonParkeerwachter();
    }

    public void openNieuweOvertredingActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), NieuweOvertredingActivity.class);
        startActivity(intent);
    }

    public void openOvertredingLijst(View view) {
        Intent intent = new Intent(getApplicationContext(), OvertredingActivity.class);
        startActivity(intent);
    }

    private void toonParkeerwachter(){
        Session session = new Session(getApplicationContext());
        String parkeerwachter = getString(R.string.menu_welkom_label) + " " + session.getNaam();
        TextView textView = (TextView) findViewById(R.id.menu_parkeerwachter);
        textView.setText(parkeerwachter);
    }
}