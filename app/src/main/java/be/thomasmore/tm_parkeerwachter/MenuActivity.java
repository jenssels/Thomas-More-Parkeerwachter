package be.thomasmore.tm_parkeerwachter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void openNieuweOvertredingActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), NieuweOvertredingActivity.class);
        startActivity(intent);
    }

    public void openOvertredingLijst(View view) {
        Intent intent = new Intent(getApplicationContext(), OvertredingActivity.class);
        startActivity(intent);
    }
}