/*
package dev.ckdevops.freshgreenery;

import android.annotation.SuppressLint;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.FileInputStream;

public class FCMNotificationSender {

    private static final String CREDENTIALS_FILE_PATH = "C:\\Android Projects\\FreshGreeneryApp-master\\FreshGreeneryApp-master\\FreshGreenery\\service-account-file.json";


    private static final String PROJECT_ID = "healthyveg-df4a7";
    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";
    @SuppressLint("NewApi")
    public static void sendNotification(Context context, String token, String title, String body) {
//final code.//
        */
/*try {
            // Check if the default FirebaseApp is already initialized
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setApplicationId("1:629403527543:android:c6dda4f64a257d510ad972") // Found in google-services.json
                        .setApiKey("AIzaSyCXHZArIvr3odLmlhzS8gAqw1WNSUQsMIA") // Found in google-services.json
                        .setProjectId("healthyveg-df4a7")
                        .build();

                FirebaseApp.initializeApp(context, options);
            }

            // Get the default FirebaseMessaging instance
            FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

            // This registration token comes from the client FCM SDK
            String registrationToken = token;

            // Build the message with the notification content
            RemoteMessage.Builder messageBuilder = new RemoteMessage.Builder(registrationToken)
                    .setMessageId(Integer.toString((int) System.currentTimeMillis()))
                    .addData("title", title)
                    .addData("body", body);


            // Send the message to the device
            try {
                // Send the message
                firebaseMessaging.send(messageBuilder.build());
                Log.d("FinalFB", "Message sent successfully!");
                System.out.println("Message sent successfully!");
            } catch (Exception e) {
                // Handle failure
                Log.d("FinalFB", "Failed to send message:" + e.getMessage());
                System.err.println("Failed to send message: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*//*

//final code end.//

        try {
            // Initialize Firebase Admin SDK
            FileInputStream serviceAccount = new FileInputStream("path-to-service-account-file.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);

            // Build the message
            Message message = Message.builder()
                    .setToken("target-device-token")
                    .setNotification(Notification.builder()
                            .setTitle("Test Notification")
                            .setBody("This is a test message")
                            .build())
                    .build();

            // Send the message
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    }

    }



*/

package dev.ckdevops.freshgreenery;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
public class FCMNotificationSender {
    public static void sendNotification(String token, String title, String msgBody) {
        // OkHttpClient setup
        OkHttpClient client = new OkHttpClient();

        // Set the media type and prepare the JSON payload
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonBody = new JSONObject();
        try {
            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", msgBody);

            // Set the message object with the notification and target token
            message.put("notification", notification);
            message.put("token", token);

            // Create the request body
            RequestBody body = RequestBody.create(mediaType, message.toString());

            // Create the request with necessary headers
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/v1/projects/healthyveg-df4a7/messages:send")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer ya29.a0ARW5m74IOOi6Oi3LiDqE210NIl7crf4UV-OhWFB-YJzfJGCG7XrmKl_3FVyTY2nC-lWz3OhfPeTZjhdNgVoIusooo3FmTfDKDf849l4txiugTAgM1eo7WYnS_KwfH30W4mDFyQQq15jjfcz7_nMRmZufG_cLHh7mEjBDMmPfaCgYKAXcSARESFQHGX2MiocCk7IEhwsgOUrBniHvyGA0175") // Replace with your actual access token
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Execute the request
            Response response = client.newCall(request).execute();

            // Handle the response
            if (response.isSuccessful()) {
                System.out.println("Notification sent successfully");
            } else {
                System.err.println("Failed to send notification. Response Code: " + response.code());
                System.err.println("Response Body: " + response.body().string());
            }

            response.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error occurred: " + e.getMessage());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}*/

public class FCMNotificationSender {

    String YOUR_ACCESS_TOKEN = "ya29.a0ARW5m74IOOi6Oi3LiDqE210NIl7crf4UV-OhWFB-YJzfJGCG7XrmKl_3FVyTY2nC-lWz3OhfPeTZjhdNgVoIusooo3FmTfDKDf849l4txiugTAgM1eo7WYnS_KwfH30W4mDFyQQq15jjfcz7_nMRmZufG_cLHh7mEjBDMmPfaCgYKAXcSARESFQHGX2MiocCk7IEhwsgOUrBniHvyGA0175";
    String token;
    String title;

    String bodyFB;
    public FCMNotificationSender(String token, String title, String bodyFB) {
        this.token = token;
        this.title = title;
        this.bodyFB = bodyFB;
    }

    public void sendNotification(String token, String title, String body) {
        // Execute network task in background thread using AsyncTask
        new SendNotificationTask().execute(token, title, body);
    }

    private class SendNotificationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // Perform the network request on a background thread
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonBody = new JSONObject();

            try {
                // Set up the message and notification payload
                JSONObject message = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("title", params[1]);
                notification.put("body", params[2]);

                message.put("notification", notification);
                message.put("token", params[0]);

                // Create the request body
                RequestBody body = RequestBody.create(mediaType, message.toString());

                // Create the request
                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/v1/projects/healthyveg-df4a7/messages:send")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer" + YOUR_ACCESS_TOKEN) // Replace with actual token
                        .addHeader("Content-Type", "application/json")
                        .build();

                // Execute the request and handle the response
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    return "Notification sent successfully";
                } else {
                    return "Failed to send notification. Response Code: " + response.code();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Error occurred: " + e.getMessage();
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
