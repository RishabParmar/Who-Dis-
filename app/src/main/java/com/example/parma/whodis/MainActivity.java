package com.example.parma.whodis;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    int profileCounter;
    String celebData = "";
    String profileArray[][] = new String[100][2];

    // Who dis? brief algorithm
    // Design the basic UI first
    // Download the target remote data
    // Pick out the specific data from the remote downloaded data and store them
    // check the correction module so that guess matches with the actual answer
    // Randomize the guess functionality
    // Clean up the algorithm and finalize the animations
    // Lastly if possible, check the internet available condition

    public void generateTheContent() {
        // Extracting the image URLS
        Pattern imagePattern = Pattern.compile("img src=(.*?) ");
        Matcher matcher = imagePattern.matcher(celebData);
        while (matcher.find()) {
            profileArray[profileCounter][0] = matcher.group(1);
            profileCounter++;
        }

        // Extracting the names
        profileCounter = 0;
        Pattern namePattern = Pattern.compile("alt=(.*?)/>");
        matcher = namePattern.matcher(celebData);
        while (matcher.find()) {
            profileArray[profileCounter][1] = matcher.group(1);
            Log.i("name ", profileArray[profileCounter][1]);
            profileCounter++;
        }
    }

    public class DownloadCelebList extends AsyncTask <String, Void, String> {

        String celebList = null;
        URL url;
        HttpURLConnection httpURLConnection = null;

        @Override
        protected String doInBackground(String... urls) {
            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int downloadedData = reader.read();
                while (downloadedData != -1) {
                    celebList += (char) downloadedData;
                    downloadedData = reader.read();
                }
                return celebList;
            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }
    }

    public void downloadCelebData() {
        DownloadCelebList downloadData = new DownloadCelebList();
        try {
            celebData = downloadData.execute("http://www.posh24.se/kandisar").get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void initializeTheUI() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeTheUI();
        downloadCelebData();
        generateTheContent();
    }
}
