package com.infineon.cipurse.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PatientPresciprionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_presciprion);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        byte[][] prescBinData = (byte[][]) bundle.getSerializable(MainActivity.prescBinData);

        /*if(rawBinData.length <= 0)
            Toast.makeText(PatientPresciprionActivity.this, "Empty", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(PatientPresciprionActivity.this, "Size: " + " " + rawBinData.length, Toast.LENGTH_LONG).show();*/

        ArrayList<PrescriptionDetails> searchResults = GetSearchResults(prescBinData);

        final ListView lv1 = (ListView) findViewById(R.id.ListView01);
        lv1.setAdapter(new MyCustomBaseAdapter(this, searchResults));

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = lv1.getItemAtPosition(position);
                PrescriptionDetails fullObject = (PrescriptionDetails)o;
                Toast.makeText(PatientPresciprionActivity.this, fullObject.getPrescriptionName() + " should be taken " + (String) fullObject.getPrescriptionDirectionsRaw().toLowerCase(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getStringFromByteArray(byte[] settingsData) {

        StringBuilder sb = new StringBuilder();
        for(byte willBeChar: settingsData) {
            sb.append((char) willBeChar);
        }

        return sb.toString();

    }

    private ArrayList<PrescriptionDetails> GetSearchResults(byte[][] rawPrescData){
        ArrayList<PrescriptionDetails> results = new ArrayList<PrescriptionDetails>();

        for (int i = 0; i < rawPrescData.length; i++) {
            if(rawPrescData[i] == null)
                break;
            /** Parse patient data **/
            String a = getStringFromByteArray(rawPrescData[i]);
            String lines[] = a.split("\\r\\n");


            PrescriptionDetails sr1 = new PrescriptionDetails();
            sr1.setPrescriptionName(lines[2]);
            sr1.setPrescriptionDate(lines[1]);
            sr1.setprescriptionRefills("Refills: " + lines[3]);
            sr1.setPrescriptionDirections("Directions: " + lines[4]);
            sr1.setPrescriptionDirectionsRaw(lines[4]);
            results.add(sr1);
        }

        return results;
    }

}




