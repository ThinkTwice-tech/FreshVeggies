package dev.ckdevops.freshgreenery;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;


import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private Context context;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("FCMToken", "New token received: " + token);

        // Save token locally for fallback if needed
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("FCMToken", token);
        editor.apply();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Update FCM token for AppUser
            databaseReference.child("AppUsers").child(userId).child("FCMToken").setValue(token)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("FCMToken", "Token updated successfully for AppUser: " + userId);
                        } else {
                            Log.e("FCMToken", "Failed to update token for AppUser", task.getException());
                        }
                    });
        } else {
            Log.w("FCMToken", "User is not signed in. Token not saved for AppUser.");
        }

       /* // Update FCM token for Admin
        databaseReference.child("Admin").child("FCMToken").setValue(token)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCMToken", "Token updated successfully for Admin.");
                    } else {
                        Log.e("FCMToken", "Failed to update token for Admin", task.getException());
                    }
                });*/
    }


    private void updateTokenInDatabase(String token, String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("AppUsers").child(userId).child("FCMToken").setValue(token)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCMToken", "Token updated successfully for AppUser: " + userId);
                    } else {
                        Log.e("FCMToken", "Failed to update token for AppUser", task.getException());
                    }
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        }

    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.

    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "default_channel";

        // Create Notification Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Default Channel for App");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.rounded_icon) // Replace with your app icon
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }










}
