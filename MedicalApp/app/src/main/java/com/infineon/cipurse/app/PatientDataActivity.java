package com.infineon.cipurse.app;

import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
// import android.widget.ImageView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.api.client.http.HttpResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
// import java.nio.charset.Charset;

public class PatientDataActivity extends AppCompatActivity {

    private Button btnchkelg;

    private RequestQueue mRequestQueue;

    private StringRequest stringRequest;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_data);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        btnchkelg = (Button) findViewById(R.id.butttonchkelg);



        Intent intent = getIntent();
        byte[] rawBinData = intent.getByteArrayExtra(MainActivity.extBinString);
        String a = getStringFromByteArray(rawBinData);
        String lines[] = a.split("\\r\\n");
        final String linesreadonly[] = lines;
        int index = 0;

        // TODO: consider different arrangement of data: TBD
        // current definition:
        // Name: Mark, Henderson
        // DoB: 03/03/1974
        // Member ID: 567234
        TextView patFNameTxt = (TextView) findViewById(R.id.NameText);
        patFNameTxt.setText(lines[index].substring(lines[index].indexOf(":") + 2));
        index += 1;

        TextView patDOBTxt = (TextView) findViewById(R.id.dobText);
        patDOBTxt.setText(lines[index].substring(lines[index].indexOf(":") + 2));

        index += 1;

        TextView patNIdTxt = (TextView) findViewById(R.id.patientIDText);
        patNIdTxt.setText(lines[index].substring(lines[index].indexOf(":") + 2));
        //





        btnchkelg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView)findViewById(R.id.textView);
                URL url;
                URL urlmain;
                HttpsURLConnection conn;
                HttpsURLConnection connmain;
                try {

                    url = new URL("https://api-gateway-stage.linkhealth.com/oauth/token");
                    String params = "client_id=" + "sc-link-nprod" + "&client_secret=" + "2d18b1d2-13d2-447c-bf61-4c4071034c53" + "&grant_type=client_credentials";
                    conn = (HttpsURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    //conn.setDoInput(true);
                    //conn.setDoOutput(true);
                    conn.setFixedLengthStreamingMode(params.getBytes().length);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    PrintWriter out = new PrintWriter(conn.getOutputStream());

                    out.print(params);
                    out.close();
                    String response= "";
                    Scanner inStream = new Scanner(conn.getInputStream());
                    while(inStream.hasNextLine())
                        response+=(inStream.nextLine());
                    int start = response.indexOf(':');
                    int stop = response.indexOf(',');
                    String tokenval = response.substring(start+2,stop-1);
                    tokenval = tokenval.trim();
                    tv.setText(tokenval);

                    String memId = linesreadonly[2].substring(linesreadonly[2].indexOf(":") + 2);
                    String memdob = linesreadonly[1].substring(linesreadonly[1].indexOf(":") + 2);

                    String newmemid ="",newmemdob = "";
                    String nums[] = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
                    for(int i = 0;i< memId.length();i++)
                    {
                        char c = memId.charAt(i);
                        String check = String.valueOf(c);
                        for(int j = 0; j < nums.length;j++)
                        {
                            if(check.equals(nums[j]))
                            {
                                newmemid += nums[j];
                                break;
                            }
                        }
                    }

                    for(int i = 0;i<memdob.length();i++)
                    {
                        char c = memdob.charAt(i);
                        String check = String.valueOf(c);
                        if(check.equals("-"))
                        {
                            newmemdob += "-";

                        }
                        else
                        {
                            for(int j = 0; j < nums.length;j++)
                            {
                                if(check.equals(nums[j]))
                                {
                                    newmemdob += nums[j];
                                    break;
                                }
                            }
                        }
                    }

                    //tv.setText(newmemid + ":" + newmemdob);
                    String paramsmain = "payerID=87726&providerLastName=PHA-UNIVERSITY&npi=910683&searchOption=MemberIDDateOfBirth&memberId=" + newmemid+ "&dateOfBirth=" + newmemdob;
                    urlmain = new URL("https://api-gateway-stage.linkhealth.com/elink-services/api/eligibility/v2.0?" +paramsmain);
                    connmain = (HttpsURLConnection)urlmain.openConnection();
                    connmain.setRequestMethod("GET");
                    connmain.setRequestProperty("Authorization" ,"Bearer "+ tokenval );

                    response= "";
                    Scanner inStreammain;

                    if(connmain.getResponseCode()< 400)
                        inStreammain= new Scanner(connmain.getInputStream());
                    else
                        inStreammain= new Scanner(connmain.getErrorStream());


                    while(inStreammain.hasNextLine())
                        response+=(inStreammain.nextLine());




                    if(response.contains("Active Policy"))
                    {
                        int index = response.indexOf("\"planDescription\"");
                        char c = response.charAt(index);

                        do
                        {
                            c = response.charAt(index);
                            index++;
                        }while (c!=',');
                        String planLine = response.substring(response.indexOf("\"planDescription\""),index);
                        String planType = planLine.substring(planLine.lastIndexOf(':')+ 2,planLine.length()-2);

                        //Toast.makeText(getApplicationContext(),String.valueOf(connmain.getResponseCode()),Toast.LENGTH_LONG).show();
                        tv.setText("Member Eligible with Plan : " + planType );
                    }
                    else
                    {
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();
                        if(connmain.getResponseCode()<400)
                            tv.setText("Policy not Active");
                        else
                            tv.setText("Policy not Active - "+String.valueOf(connmain.getResponseCode()));
                    }




                } catch (Exception e) {
                    e.printStackTrace();

                    tv.setText(e.toString());

                    System.out.println(e.getMessage());
                }
            }
        });
    }






    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.patientdatamenu, menu);
        return true;
    }

    public String getStringFromByteArray(byte[] settingsData) {

        StringBuilder sb = new StringBuilder();
        for(byte willBeChar: settingsData) {
            sb.append((char) willBeChar);
        }

        return sb.toString();

    }

    private void saveImage(Bitmap bitmap, String filename) {

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(filename);
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename);
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            bitmap = BitmapFactory.decodeFile(file.getName());

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            System.out.println("closed");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
