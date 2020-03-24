package com.example.parma.whodis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    int profileCounter;
    String celebData = "";
    String profileArray[][] = new String[21][2];
    int alreadyDone[] = new int[21];
    int doneCounter;
    String answer;

    ImageView celebImage;
    TextView celeb1, celeb2, celeb3, celeb4;

    // Who dis? brief algorithm:
    // Design the basic UI first
    // Download the target remote data
    // Pick out the specific data from the remote downloaded data and store them
    // check the correction module so that guess matches with the actual answer
    // Randomize the guess functionality
    // Clean up the algorithm and finalize the animations
    // Lastly if possible, check the internet available condition
    // No play again functionality for this app

    // Downloading the image brief algorithm:
    // Randomly picking an image URL
    // Downloading the bitmap and assigning it to the celebImage
    // Randomly picking any three names from the list and assigning them to the celeb TextViews
    // Checking the answer and showing a green outline somewhere along the UI

    // Getting random Data Index:


    public int getRandomCelebrity() {
        int randomDataIndex = 1;
        Random random = new Random();
        while (true) {
            randomDataIndex = random.nextInt(profileArray.length);
            if(alreadyDone[randomDataIndex] == 0) {
                alreadyDone[doneCounter] = 1;
                doneCounter++;
                return randomDataIndex;
            }else if(doneCounter>=21) { return randomDataIndex; }
        }
    }

    public class DownloadCelebImage extends AsyncTask<String, Void, Bitmap> {
        Bitmap resultImage;

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                // The URL that should be passed below should not contain "", it should be plain string
                // for eg: "http://cdn.posh24.se/images/:profile/c/50755" won't work but http://cdn.posh24.se/images/:profile/c/50755 will work
                // Both of them is a string tho!  The issue arising was MalformedURL Exception and this was due to the starting and trailing ""
                URL imageURL = new URL(urls[0].substring(1, urls[0].length() - 1));
                HttpURLConnection httpURLConnection = (HttpURLConnection) imageURL.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                resultImage = BitmapFactory.decodeStream(inputStream);
                return resultImage;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Image Exception", "Something went wrong the image URL");
                return resultImage;
            }
        }
    }

    public void nextQuestion() {
        DownloadCelebImage downloadImage = new DownloadCelebImage();
        Bitmap fetchedCelebMugshot = null;
        int randomCelebIndex = getRandomCelebrity();
        try {
            String url = profileArray[randomCelebIndex][0];
            fetchedCelebMugshot = downloadImage.execute(url).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        celebImage.setImageBitmap(fetchedCelebMugshot);
        answer = profileArray[randomCelebIndex][1];
        // Binding the option with the TextViews
        // First for randomizing the answer
        int choice = new Random().nextInt(4);
        if (choice == 0) { celeb1.setText(answer); }
        else if (choice == 1) { celeb2.setText(answer); }
        else if (choice == 2) { celeb3.setText(answer); }
        else if (choice == 3) { celeb4.setText(answer); }
        // Setting the rest of the options
        Log.i("Here", "2");
        if(!celeb1.getText().equals(answer)) { celeb1.setText(profileArray[new Random().nextInt(profileArray.length)][1]); }
        if(!celeb2.getText().equals(answer)) { celeb2.setText(profileArray[new Random().nextInt(profileArray.length)][1]); }
        if(!celeb3.getText().equals(answer)) { celeb3.setText(profileArray[new Random().nextInt(profileArray.length)][1]); }
        if(!celeb4.getText().equals(answer)) { celeb4.setText(profileArray[new Random().nextInt(profileArray.length)][1]); }
        Log.i("Here", "3");
    }

    public void bindingCelebDataWithUI(View view) {
        TextView selectedAnswer = (TextView) view;
        if(selectedAnswer.getText().equals(answer)) {
            Toast.makeText(this, "Correct Answer", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "EHH!!! Correct answer is: " + answer, Toast.LENGTH_SHORT).show();
        }
        nextQuestion();
    }

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
        Pattern namePattern = Pattern.compile("alt=\"(.*?)\"/>");
        matcher = namePattern.matcher(celebData);
        while (matcher.find()) {
            profileArray[profileCounter][1] = matcher.group(1);
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

        // Filling up the content array
        generateTheContent();
    }

    public void initializeTheUI() {
        celebImage = findViewById(R.id.celebImage);
        celeb1 = findViewById(R.id.celeb1);
        celeb2 = findViewById(R.id.celeb2);
        celeb3 = findViewById(R.id.celeb3);
        celeb4 = findViewById(R.id.celeb4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeTheUI();
        downloadCelebData();
        nextQuestion();
    }
}
