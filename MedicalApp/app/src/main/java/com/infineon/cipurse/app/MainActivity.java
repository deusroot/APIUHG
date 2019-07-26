package com.infineon.cipurse.app;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.infineon.cipurse.terminallibrary.presentation.ISessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView statusLabel;
    private ListView historyListView;
    private Switch hardwareSAMBtn;
    public final static String extBinString = "binaryData";
    public final static String prescBinData = "prescBinData";
    private static final String logTAG = "TERMLIB";


    private NfcAdapter mNfcAdapter;
    private static String[][] mTechLists = new String[][] {
            new String[] { IsoDep.class.getName() },
            new String[] { NfcA.class.getName() },
            new String[] { NfcB.class.getName() },
            new String[] { NfcF.class.getName() },
            new String[] { NfcV.class.getName() },
            new String[] { Ndef.class.getName() }
    };

    private ArrayAdapter<String> adapter;
    private ArrayList<String> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusLabel = (TextView) findViewById(R.id.status_label);
        historyListView = (ListView) findViewById(R.id.history_list);

        historyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_list_item, R.id.text, historyList);
        historyListView.setAdapter(adapter);

        initializeNFC();
    }

    private void initializeNFC()
    {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            statusLabel.setText("Sorry! This device doesn't support NFC.");
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            statusLabel.setText("NFC is turned off.");
        } else {
            statusLabel.setText("Please scan your CIPURSE Medical ID card");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent == null) { return; }

        Log.i(logTAG, "Handle Intent");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            IsoDep isoDep = IsoDep.get(tag);

            boolean hwSAM = false;
            ISessionManager.CRYPTO_MODE crypto_mode = ISessionManager.CRYPTO_MODE.SW_SAM;


            IFXCTL_ExampleUtils exampleUtils = new IFXCTL_ExampleUtils();

            writeLog("*** Execution Started ***");
            IFXCTL_BinaryFile_Example binaryFile_example;

            try {
                binaryFile_example = new IFXCTL_BinaryFile_Example(crypto_mode.ordinal(), isoDep, hwSAM);
            } catch (Exception e) {
                writeLog("Binary file perso failed");
                e.printStackTrace();
                return;
            }

            try {
                byte[] rawFileData = binaryFile_example.readBinaryfileOp();
                writeLog("Binary file read success: " + rawFileData.length);
                openPatientDataActivity(rawFileData); // , prescriptionData);

            } catch (Exception e) {
                e.printStackTrace();
                writeLog("Binary file read failed");
                return;
            }
            writeLog("*** Execution Completed ***");
        }
    }

    public void openPatientDataActivity(byte[] rawFileData) // , byte[][] prescriptionData)
    {
        Intent intent = new Intent(this, PatientDataActivity.class);
        intent.putExtra(extBinString, rawFileData);
        startActivity(intent);
    }

    public void writeLog(String message)
    {
        String date = new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        Log.i(logTAG, message);
        historyList.add(date + " : " + message);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(mNfcAdapter != null)
        {
            setupForegroundDispatch(this, mNfcAdapter);
        }
    }

    @Override
    protected void  onPause()
    {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        Log.i(logTAG, "setupForegroundDispatch");
        adapter.enableForegroundDispatch(activity, pendingIntent, null, mTechLists);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        Log.i(logTAG, "stopForegroundDispatch");
        adapter.disableForegroundDispatch(activity);
    }
}
