package xyz.cyklo.app.cyklo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailsActivity extends AppCompatActivity {

    private static final String EMPTY = "";
    private static final String TAG = "CYKLO.DETAILS";
    SharedPreferences details;
    EditText etName, etNumber, etEmail;
    Spinner spinCollege;
    Button btnEdit;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        intialise();

        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etName.setTextColor(Color.BLACK);
                }
            }
        });
        etNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etName.setTextColor(Color.BLACK);
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getBooleanExtra("condition", false)) {

                    if (btnEdit.getText().toString().equals("Edit")) {
                        etName.setEnabled(true);
                        etNumber.setEnabled(true);
                        etEmail.setEnabled(true);
                        btnEdit.setText("Save");
                    } else if (btnEdit.getText().toString().equals("Save")) {
                        boolean save = onSave();
                        Log.i(TAG, "Saved: " + String.valueOf(save));
                        if (save) {
                            btnEdit.setText("Edit");
                            etName.setEnabled(false);
                            etNumber.setEnabled(false);
                            etEmail.setEnabled(false);
                            editor.putBoolean("saved", true);
                            editor.commit();
                            finish();
                        } else {
                            editor.putBoolean("saved", false);
                            editor.commit();
//                     Toast.makeText(getApplicationContext(), "Enter Details", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "Not allowed to edit", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void intialise() {
        etName = (EditText) findViewById(R.id.etName);
        etNumber = (EditText) findViewById(R.id.etNumber);
        etEmail = (EditText) findViewById(R.id.etEmail);
//        etName.setEnabled(false);
//        etNumber.setEnabled(false);
//        etEmail.setEnabled(false);
        spinCollege = (Spinner) findViewById(R.id.spinCollege);
        btnEdit = (Button) findViewById(R.id.btnEdit);

        details = getSharedPreferences("cyklo.details", Context.MODE_PRIVATE);
        editor = details.edit();
        boolean saved = setSaved();
    }

    /**
     * Saves data to SharedPreferences(cyklo.details)
     *
     * @return true - if no field is empty
     */
    private boolean onSave() {
        String name = etName.getText().toString();
        String college = spinCollege.getSelectedItem().toString();
        String number = etNumber.getText().toString();
        String email = etEmail.getText().toString();

        Log.i(TAG, "Name: " + name);
        Log.i(TAG, "College: " + college);
        Log.i(TAG, "Number: " + number);
        Log.i(TAG, "Email: " + email);

        String REGEX = "^[a-zA-Z\\s]*$";
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(name);

        if (!m.matches()) {
            Toast.makeText(getApplicationContext(), "Name can only contain a-z, A-Z, SPACE.", Toast.LENGTH_SHORT).show();
            etName.setTextColor(Color.RED);

            return false;
        } else if (number.length() != 10) {
            Toast.makeText(getApplicationContext(), "Enter valid mobile number", Toast.LENGTH_SHORT).show();
            etName.setTextColor(Color.BLACK);
            etNumber.setTextColor(Color.RED);

            return false;
        } else if (name.length() != 0 && college.length() != 0 && email.length() != 0 && number.length() == 10) {
            SharedPreferences.Editor editor = details.edit();
            etNumber.setTextColor(Color.BLACK);
            etName.setTextColor(Color.BLACK);
            editor.putString("name", name);
            editor.putString("college", college);
            editor.putString("number", number);
            editor.putString("email", email);
            editor.commit();
            return true;
        }
        return false;
    }

    /**
     * Sets already saved data to fields
     *
     * @return true
     */
    private boolean setSaved() {
        String name = details.getString("name", EMPTY);
        String college = details.getString("college", EMPTY);
        String number = details.getString("number", EMPTY);
        String email = details.getString("email", EMPTY);

        etName.setText(name);
        spinCollege.setSelection(((ArrayAdapter<String>) spinCollege.getAdapter()).getPosition(college));
        etNumber.setText(number);
        etEmail.setText(email);

        return true;
    }
}
