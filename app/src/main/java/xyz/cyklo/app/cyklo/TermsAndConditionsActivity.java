package xyz.cyklo.app.cyklo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class TermsAndConditionsActivity extends AppCompatActivity {

    SharedPreferences terms;
    SharedPreferences.Editor editor;
    boolean accepted;
    Button accept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        terms = getSharedPreferences("cyklo.details", MODE_PRIVATE);
        editor = terms.edit();

        accepted = terms.getBoolean("tnc", false);
        accept = (Button) findViewById(R.id.tncButton);

        if(accepted) {
            accept.setBackgroundColor(Color.GRAY);
            accept.setText("Accepted");
        } else {
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putBoolean("tnc", true);
                    editor.commit();
                    finish();
                }
            });
        }
    }

}

