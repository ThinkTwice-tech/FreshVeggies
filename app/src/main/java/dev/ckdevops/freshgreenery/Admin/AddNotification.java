package dev.ckdevops.freshgreenery.Admin;




import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.auth.oauth2.GoogleCredentials;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import dev.ckdevops.freshgreenery.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNotification extends AppCompatActivity {
    ImageView close;
    private String uid = "";
    private FirebaseAuth firebaseAuth;
    String titleFB ="";
    String bodyFB ="";
    String YOUR_ACCESS_TOKEN = "ya29.a0ARW5m74IOOi6Oi3LiDqE210NIl7crf4UV-OhWFB-YJzfJGCG7XrmKl_3FVyTY2nC-lWz3OhfPeTZjhdNgVoIusooo3FmTfDKDf849l4txiugTAgM1eo7WYnS_KwfH30W4mDFyQQq15jjfcz7_nMRmZufG_cLHh7mEjBDMmPfaCgYKAXcSARESFQHGX2MiocCk7IEhwsgOUrBniHvyGA0175";
    private static final String TAG = "SendNotificationActivity";
    private DatabaseReference databaseReference;
    private EditText etNotificationTitle, etNotificationBody;
    private Button btnSendNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        setContentView(R.layout.add_notification);
        close = findViewById(R.id.close);
        etNotificationTitle = findViewById(R.id.etNotificationTitle);
        etNotificationBody = findViewById(R.id.etNotificationBody);
        btnSendNotification = findViewById(R.id.btnSendNotification);
        close.setOnClickListener(view -> finish());

        btnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleFB = etNotificationTitle.getText().toString().trim();
                bodyFB = etNotificationBody.getText().toString().trim();
                //uid = firebaseAuth.getCurrentUser().getUid();
                databaseReference = FirebaseDatabase.getInstance().getReference("AppUsers");

                fetchFCMTokensAndSendNotification();

            }
        });
    }


    private void fetchFCMTokensAndSendNotification() {
        List<String> fcmTokens = new ArrayList<>();



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("FirebaseDebug", "DataSnapshot exists: " + dataSnapshot.exists());
                Log.d("FirebaseDebug", "Children count: " + dataSnapshot.getChildrenCount());

                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        String fcmToken = userSnapshot.child("FCMToken").getValue(String.class);

                        Log.d("FirebaseDebug", "User ID: " + userId);
                        Log.d("FirebaseDebug", "FCM Token: " + fcmToken);

                        if (fcmToken != null && !fcmToken.isEmpty()) {
                            fcmTokens.add(fcmToken);
                        }
                    }

                    if (!fcmTokens.isEmpty()) {
                        sendNotificationToUsers(fcmTokens);
                    } else {
                        Log.d("FirebaseDebug", "No FCM tokens found.");
                        Toast.makeText(AddNotification.this, "No FCM tokens found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("FirebaseDebug", "No data found in AppUsers.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseDebug", "Database error: " + databaseError.getMessage(), databaseError.toException());
            }
        });

    }

    private void sendNotificationToUsers(List<String> fcmTokens) {
        for (String token : fcmTokens) {
            sendNotification(token, titleFB, bodyFB);
        }
    }

    private void sendNotification(String token, String title, String body) {
        // Sending notification using FCM. Replace with actual server-side or FCM logic.

        //String authToken = getAuthToken();
        SendNotificationTask sendNotificationTask = new SendNotificationTask();
        sendNotificationTask.execute(token, title, body);
    }


    private class SendNotificationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Perform the network request on a background thread
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonBody = new JSONObject();

            try {

                AssetManager assetManager = getAssets();
                InputStream inputStream = assetManager.open("service-account-file.json");

                GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                        .createScoped("https://www.googleapis.com/auth/firebase.messaging");

                credentials.refreshIfExpired();
                String accessToken = credentials.getAccessToken().getTokenValue();


                // Set up the message and notification payload
                JSONObject notification = new JSONObject();
                notification.put("title", params[1]);
                notification.put("body", params[2]);

                JSONObject message = new JSONObject();
                message.put("notification", notification);
                message.put("token", params[0]);

                JSONObject mainPayload = new JSONObject();
                mainPayload.put("message", message);

                // Request Body

                RequestBody body = RequestBody.create(mediaType, mainPayload.toString());

                /*String jsonPayload = "{"
                        + "message: {"
                        + "    \"token\": \"cy3KhEj9TYezzHo_NozqZv:APA91bHLf0qsYfYa4vZybfXnvoFm7fKBtyYWOKjZ_nzng_KDqOReEPn9y6aiujGQKVQWvwhdutsrrGjb_mzwPn400dLwBm-DDYFiHfc2MHuyElExsilxQRo\","
                        + "    \"notification\": {"
                        + "        \"title\": \"Sample Title\","
                        + "        \"body\": \"This is a sample notification body.\""
                        + "    }"
                        + "}}";*/


                // Create the request
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/v1/projects/healthyveg-df4a7/messages:send")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + accessToken) // Replace with actual token
                        .addHeader("Content-Type", "application/json")
                        .build();

                // Execute the request and handle the response
                Response response = client.newCall(request).execute();
                Log.d("msg", "onMessageReceived: " + response);
                if (response.isSuccessful()) {
                    return "Notification sent successfully";
                } else {
                    return "Failed to send notification. Response Code: " + response.code();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error occurred: " + e.getMessage();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // This method will be called on the main thread after the task is complete
            // You can update the UI with the result here
            System.out.println(result);
        }
    }


}
