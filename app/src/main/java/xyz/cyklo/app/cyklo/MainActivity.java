package xyz.cyklo.app.cyklo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import xyz.cyklo.api.Amount;
import xyz.cyklo.api.Time;

public class MainActivity extends AppCompatActivity {

    State currentState;//intially LOCKED
    boolean success;
    Dialog confirmReturnDialog;
    SharedPreferences details;
    SharedPreferences.Editor editor;
    private static final String TAG = "CYKLO.MAIN";
    private static final String EMPTY = "";
    ConnectivityManager connMan;
    NetworkInfo netInfo;
    GifImageView gifCycles;
    GifDrawable gifDrawable;
    Button btnRequest, btnAmount;
    TextView tvProcessing, tvCycleNumber, tvStartTime, tvRate, tvUserName;
    //Chronometer chronometer;
    Time time;//CYKLO TIME OBJECT
    Amount amount;//CYKLO AMOUNT OBJECT
    final String[] SPECIAL_CYCLE_NUMBERS = {"007", "111", "420", "555", "911", "786"};
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    public void confirmReturnNoClicked(View view) {
        confirmReturnDialog.dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://xyz.cyklo.app.cyklo/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://xyz.cyklo.app.cyklo/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }

    public void confirmReturnYesClicked(View view) {
        confirmReturnDialog.dismiss();
        confirmedRequestLock();
    }

    private enum State {
        LOCKED, UNLOCKED, ACCEPTED_LOCK, ACCEPTED_UNLOCK, REQUESTED_LOCK, REQUESTED_UNLOCK
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialise();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentState) {
                    case LOCKED:
                        requestUnlock();
                        break;

                    case UNLOCKED:
                        requestLock();
                        break;

                    default:
                        break;
                }
            }
        });
        amount = new Amount(time);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Amount:")
                .setMessage("Rs. ".concat(String.valueOf(amount.getAmount())));

        btnAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = new Amount(time);
                alertDialogBuilder.setMessage("Rs. ".concat(String.valueOf(amount.getAmount())));
                //Log.i(TAG, String.valueOf(time.getTimeDifference()));
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //If details not saved
        boolean saved = details.getBoolean("saved", false);
        Log.i(TAG, String.valueOf(saved));
        if (!saved) {
            Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
            i.putExtra("condition", detailsEditable(currentState));
            startActivity(i);
        }

        //If Terms and Conditions not accepted
        boolean tnc = details.getBoolean("tnc", false);
        Log.i(TAG, String.valueOf(tnc));
        if (!tnc) {
            Intent i = new Intent(getApplicationContext(), TermsAndConditionsActivity.class);
            startActivity(i);
        }

        //If not connected to CYKLO ALPHA WIFI
        if (!checkConnection()) {
            Intent i = new Intent(getApplicationContext(), HowToActivity.class);
            startActivity(i);
            finish();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void initialise() {
        success = false;
        details = getSharedPreferences("cyklo.details", Context.MODE_PRIVATE);
        editor = details.edit();
        connMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        time = new Time();

        gifCycles = (GifImageView) findViewById(R.id.gifCycle);
        gifDrawable = (GifDrawable) gifCycles.getDrawable();
        btnRequest = (Button) findViewById(R.id.btnRequest);
        btnAmount = (Button) findViewById(R.id.btnAmount);
        tvProcessing = (TextView) findViewById(R.id.tvProcessing);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvCycleNumber = (TextView) findViewById(R.id.tvCycleNumber);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvRate = (TextView) findViewById(R.id.tvRate);
        // chronometer = (Chronometer) findViewById(R.id.chronometer);

        //Setting previously saved data, if any
        currentState = State.valueOf(details.getString("currentState", State.LOCKED.toString()));
        time.setStartTime(details.getString("startTime", Time.defaultTime));
        time.setEndTime(details.getString("endTime", Time.defaultTime));
        int cycleNumber = details.getInt("cycleNumber", 0);
        if (cycleNumber != 0) {
            tvCycleNumber.setVisibility(View.VISIBLE);
            tvCycleNumber.setText("Cycle Number: ".concat(String.valueOf(cycleNumber)));
            tvUserName.setVisibility(View.VISIBLE);
            tvUserName.setText("Name: ".concat(details.getString("name","")));
        } else {
            tvCycleNumber.setVisibility(View.INVISIBLE);
            tvUserName.setVisibility(View.INVISIBLE);
        }
        amount = new Amount(time);

        setState(currentState);
        resetAnimation();//Shows gif on startup (don't mess with this)
    }

    private boolean checkConnection() {//TODO
        netInfo = connMan.getActiveNetworkInfo();
       /* if (netInfo != null) {
            Log.i(TAG, netInfo.getExtraInfo());
            Log.i(TAG, String.valueOf(netInfo.getExtraInfo().equals("\"CYKLO APLHA\"")));
            if (!netInfo.getExtraInfo().equals("\"CYKLO ALPHA\"")) { // TODO: SSID
                Toast.makeText(getApplicationContext(), "Connect to CYKLO ALPHA", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Connect to WiFi: CYKLO ALPHA", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Connect to WiFi: CYKLO ALPHA", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    //Changed by G Buddies on 17-03-2016
    private boolean requestLock() {
        if (!checkConnection()) return false;
        confirmReturnDialog = new Dialog(this);
        confirmReturnDialog.setContentView(R.layout.dialog_confirm_return);
        confirmReturnDialog.setCancelable(false);
        confirmReturnDialog.setTitle("Confirm Cycle Return");
        ((TextView) confirmReturnDialog.findViewById(R.id.dialog_return_name)).setText("Name : " +
                details.getString
                        ("name", ""));
        ((TextView) confirmReturnDialog.findViewById(R.id.dialog_return_amount)).setText("Amount " +
                ": " +
                String.valueOf(amount.getAmount()));

        //TODO: -------------------------
        ((TextView) confirmReturnDialog.findViewById(R.id.dialog_return_time)).setText("Time: "+
                (amount.minutes));
                confirmReturnDialog.show();
        return true;
    }

    private boolean confirmedRequestLock() {

        currentState = State.REQUESTED_LOCK;
        setState(currentState);

        editor.putString("currentState", currentState.toString());
        editor.commit();

        if (!details.getBoolean("saved", false)) {
            Log.i(TAG, "Details not saved");
            Toast.makeText(getApplicationContext(), "Details not saved", Toast.LENGTH_SHORT).show();
            return false;
        }

        String name = details.getString("name", EMPTY).replace(" ", "%20");
        String college = details.getString("college", EMPTY);
        String number = details.getString("number", EMPTY);
        String email = details.getString("email", EMPTY);
        int cycleNumber = details.getInt("cycleNumber", 0);

        if (name.length() == 0 || college.length() == 0 || number.length() == 0 || email.length() == 0) {
            Log.i(TAG, "Details empty");
            Toast.makeText(getApplicationContext(), "Details empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        String requestURL = "http://192.168.1.100:8080/";
        requestURL += "cyklo/"; // TODO: make cyklo dir changes in server and here
        requestURL += "res/";
        requestURL += "request.php?";
        requestURL += "name=" + name;
        requestURL += "&college=" + college;
        requestURL += "&number=" + number;
        requestURL += "&email=" + email;
        requestURL += "&cycle_number=" + cycleNumber;
        requestURL += "&lock_state=1";

        Log.i(TAG, requestURL);

        new DownloadTask().execute(requestURL);

        return true;
    }

    private boolean requestUnlock() {
        if (!checkConnection()) return false;
        currentState = State.REQUESTED_UNLOCK;
        setState(currentState);

        editor.putString("currentState", currentState.toString());
        editor.commit();

        if (!details.getBoolean("saved", false)) {
            Log.i(TAG, "Details not saved");
            Toast.makeText(getApplicationContext(), "Details not saved", Toast.LENGTH_SHORT).show();
            return false;
        }

        String name = details.getString("name", EMPTY).replace(" ", "%20");
        String college = details.getString("college", EMPTY);
        String number = details.getString("number", EMPTY);
        String email = details.getString("email", EMPTY);

        if (name.length() == 0 || college.length() == 0 || number.length() == 0 || email.length() == 0) {
            Log.i(TAG, "Details empty");
            return false;
        }
        String requestURL = "http://192.168.1.100:8080/";
        requestURL += "cyklo/"; // TODO: make cyklo dir changes in server and here
        requestURL += "res/";
        requestURL += "request.php?";
        requestURL += "name=" + name;
        requestURL += "&college=" + college;
        requestURL += "&number=" + number;
        requestURL += "&email=" + email;
        requestURL += "&lock_state=0";

        Log.i(TAG, requestURL);

        new DownloadTask().execute(requestURL);

        return true;
    }

    private boolean acceptUnlock() {
        if (!checkConnection()) return false;
        currentState = State.ACCEPTED_UNLOCK;
        setState(currentState);

        editor.putString("currentState", currentState.toString());
        editor.commit();

        return true;
    }

    private boolean acceptLock() {
        if (!checkConnection()) return false;
        currentState = State.ACCEPTED_LOCK;
        setState(currentState);

        editor.putString("currentState", currentState.toString());
        editor.commit();

        return true;
    }

    private boolean unlock() {
        if (!checkConnection()) return false;
        currentState = State.UNLOCKED;
        setState(currentState);

        editor.putString("currentState", currentState.toString());
        editor.commit();

        return true;
    }

    private boolean lock() {
        if (!checkConnection()) return false;
        currentState = State.LOCKED;
        setState(currentState);

        editor.putString("currentState", currentState.toString());
        editor.commit();

        return true;
    }

    private void setState(State now) {
        Log.i(TAG, now.toString());
        currentState = now;
        editor.putString("currentState", currentState.toString());
        editor.commit();

        switch (now) {
            case LOCKED:
                //hide this
                tvProcessing.setVisibility(View.INVISIBLE);
                tvStartTime.setVisibility(View.INVISIBLE);
                btnAmount.setVisibility(View.INVISIBLE);
                tvCycleNumber.setVisibility(View.INVISIBLE);
                tvUserName.setVisibility(View.INVISIBLE);
                tvRate.setVisibility(View.INVISIBLE);

                //show this
                btnRequest.setVisibility(View.VISIBLE);
                gifCycles.setVisibility(View.VISIBLE);

                btnRequest.setText(getString(R.string.requestUnlock));
                stopGif();
                break;

            case UNLOCKED:
                //hide this
                gifCycles.setVisibility(View.INVISIBLE);
                tvProcessing.setVisibility(View.INVISIBLE);

                //show this
                tvStartTime.setVisibility(View.VISIBLE);
                btnRequest.setVisibility(View.VISIBLE);
                btnAmount.setVisibility(View.VISIBLE);
                tvRate.setVisibility(View.VISIBLE);
                //chronometer.setVisibility(View.VISIBLE);

                btnRequest.setText(getString(R.string.requestLock));
                tvStartTime.setText("Start Time: ".concat(Time.TIME_FORMAT.format(time.getStartTime())));

                // long localTime = details.getLong("localTime", 0);
                //Log.i(TAG, String.valueOf(localTime));
               /* if(localTime == 0) {
                    localTime = System.currentTimeMillis();
                    Log.i(TAG, String.valueOf(localTime));
                    editor.putLong("localTime", localTime);
                    editor.commit();
                }

                long elapsedRealtimeOffset = System.currentTimeMillis() - SystemClock.elapsedRealtime();*/
                // chronometer.setBase(localTime-elapsedRealtimeOffset);
                //chronometer.start();
                stopGif();

                break;

            case ACCEPTED_LOCK:
            case ACCEPTED_UNLOCK:
                AcceptedListener acceptedListener = new AcceptedListener(now.toString());
                acceptedListener.start();

            case REQUESTED_LOCK:
            case REQUESTED_UNLOCK:
                //hide this
                btnRequest.setVisibility(View.INVISIBLE);
                btnAmount.setVisibility(View.INVISIBLE);
                tvStartTime.setVisibility(View.INVISIBLE);
                tvRate.setVisibility(View.INVISIBLE);
                //  chronometer.setVisibility(View.INVISIBLE);

                //show this
                tvProcessing.setVisibility(View.VISIBLE);
                gifCycles.setVisibility(View.VISIBLE);
                tvCycleNumber.setVisibility(View.VISIBLE);
                tvUserName.setVisibility(View.VISIBLE);
                tvProcessing.setText(getString(R.string.processing));
                startGif();
                break;
        }
    }

    private void startGif() {
        if (!gifDrawable.isPlaying()) {
            gifDrawable.start();
        }
    }

    private void stopGif() {
        if (gifDrawable.isPlaying()) {
            gifDrawable.stop();
        }
    }

    private void resetAnimation() {
        gifDrawable.stop();
        gifDrawable.seekToFrameAndGet(0);
    }

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
        switch (id) {
            case R.id.action_details:
                // if(currentState == State.LOCKED){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Edit Details: ")
                        .setMessage("Are you sure, you want to edit your details?");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), DetailsActivity.class).putExtra("condition", detailsEditable(currentState)));
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //else
                // Toast.makeText(getApplicationContext(), "Details cannot be changed! :(", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_tnc:
                startActivity(new Intent(getApplicationContext(), TermsAndConditionsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                return "{\"conn_error\": 1}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);
            try {
                JSONObject data = new JSONObject(result);

                //Connection Error
                int conn_error = 0;
                if (data.has("conn_error")) conn_error = data.getInt("conn_error");
                if (conn_error == 1) {
                    Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                    switch (currentState) {
                        case REQUESTED_LOCK:
                            unlock();
                            break;

                        case REQUESTED_UNLOCK:
                            lock();
                            break;
                    }
                    return;
                }

                //Manage request data
                if (currentState == State.REQUESTED_UNLOCK || currentState == State.REQUESTED_LOCK) {
                    int error = 0;
                    if (data.has("error")) error = data.getInt("error");
                    String errorMsg;
                    switch (error) {
                        case 0:
                            errorMsg = "";
                            break;
                        case 1:
                            errorMsg = "Enter details";
                            setState(State.LOCKED);
                            break;
                        case 2:
                            errorMsg = "Sorry! We couldn't find you a cycle.";
                            setState(State.LOCKED);
                            break;
                        case 3:
                            errorMsg = "Sorry! All cycles already present";
                            setState(State.UNLOCKED);
                            break;
                        default:
                            //Aarey baap re! Yeh error kab likha meine?
                            errorMsg = "No idea what error!";
                    }
                    if (errorMsg.length() != 0) {
                        Log.i(TAG, errorMsg);
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        int cycleNumber = (data.has("cycle_number")) ? data.getInt("cycle_number") : 0;
                        editor.putInt("cycleNumber", cycleNumber);
                        editor.commit();

                        tvCycleNumber.setText("Cycle Number: ".concat(SPECIAL_CYCLE_NUMBERS[cycleNumber - 1]));
                        tvUserName.setText("Name: ".concat(details.getString("name","")));
                        switch (currentState) {
                            case REQUESTED_UNLOCK:
                                acceptUnlock();
                                break;

                            case REQUESTED_LOCK:
                                acceptLock();
                                break;

                            default:
                        }
                    }
                }

                //Manage accept data
                else if (currentState == State.ACCEPTED_UNLOCK || currentState == State.ACCEPTED_LOCK) {
                    int accepted = 0;
                    if (data.has("accepted")) accepted = data.getInt("accepted");
                    if (accepted == 1/*Request accepted*/) {
                        int cycleNumber = details.getInt("cycleNumber", 0);
                        if (cycleNumber != 0) {
                            tvCycleNumber.setText("Cycle Number: ".concat(SPECIAL_CYCLE_NUMBERS[cycleNumber - 1]));
                            tvUserName.setText("Name: ".concat(details.getString("name","")));
                        }
                        switch (currentState) {
                            case ACCEPTED_LOCK:
                                String endTime = "";
                                if (data.has("end")) endTime = data.getString("end");
                                time.setEndTime(endTime);
                                editor.putString("endTime", endTime);
                                editor.commit();
                                lock();
                                break;

                            case ACCEPTED_UNLOCK:
                                String startTime = "";
                                if (data.has("start")) startTime = data.getString("start");
                                time.setStartTime(startTime);
                                editor.putString("startTime", startTime);
                                editor.commit();
                                unlock();
                                break;
                        }
                    } else if (accepted == -1/*Request denied*/) {
                        Toast.makeText(getApplicationContext(), "Request Denied", Toast.LENGTH_SHORT).show();
                        switch (currentState) {
                            case ACCEPTED_LOCK:
                                setState(State.UNLOCKED);
                                break;

                            case ACCEPTED_UNLOCK:
                                setState(State.LOCKED);
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initiates the fetch operation.
     */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream, 500);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     *
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        Log.i(TAG, conn.getResponseMessage());
        // Start the query
        conn.connect();
        return conn.getInputStream();
        // END_INCLUDE(get_inputstream)
    }

    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream InputStream containing HTML from targeted site.
     * @param len    Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    private class AcceptedListener extends Thread {
        private String threadName;
        private Thread t;

        public AcceptedListener(String threadName) {
            this.threadName = threadName;
        }

        public void run() {
            try {
                while (currentState == MainActivity.State.ACCEPTED_UNLOCK || currentState == MainActivity.State.ACCEPTED_LOCK) {
                    String name = details.getString("name", EMPTY);
                    String college = details.getString("college", EMPTY);
                    String number = details.getString("number", EMPTY);
                    String email = details.getString("email", EMPTY);

                    String requestURL = "http://192.168.1.100:8080/";
                    requestURL += "cyklo/"; // TODO: make cyklo dir changes in server and here
                    requestURL += "res/";
                    requestURL += "accepted.php?";
                    requestURL += "name=" + name;
                    requestURL += "&college=" + college;
                    requestURL += "&number=" + number;
                    requestURL += "&email=" + email;
                    requestURL += "&lock_state=";
                    if (currentState == MainActivity.State.ACCEPTED_UNLOCK)
                        requestURL += "0";
                    else
                        requestURL += "1";

                    Log.i(TAG, requestURL);

                    new DownloadTask().execute(requestURL);

                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void start() {
            if (t == null) {
                t = new Thread(this, threadName);
                t.start();
            }
        }

    }

    // to determine when the user can edit DETAILS
    public boolean detailsEditable(State s) {
        switch (s) {
            case LOCKED:
                return true;
            case UNLOCKED:
            case ACCEPTED_LOCK:
            case REQUESTED_LOCK:
            case ACCEPTED_UNLOCK:
            case REQUESTED_UNLOCK:
                return false;
        }
        return false;
    }

}


