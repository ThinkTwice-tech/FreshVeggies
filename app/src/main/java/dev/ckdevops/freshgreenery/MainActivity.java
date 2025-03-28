package dev.ckdevops.freshgreenery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import dev.ckdevops.freshgreenery.Admin.AdminActivity;
import dev.ckdevops.freshgreenery.Auth.AuthActivity;
import dev.ckdevops.freshgreenery.Dashboard.HomeActivity;

import dev.ckdevops.freshgreenery.R;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT = 1000;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String User, AdminUsername, AdminPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (firebaseAuth.getCurrentUser() != null) {
                    User = firebaseAuth.getCurrentUser().getEmail();

                    reference = FirebaseDatabase.getInstance().getReference().child("Admin");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            AdminUsername = snapshot.child("username").getValue(String.class);
                            AdminPassword = snapshot.child("password").getValue(String.class);
                            System.out.println("inside" + AdminUsername);

                            if (User.equals(AdminUsername)) {
                                //progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                                finish();
                                //Toast.makeText(MainActivity.this, "Welcome Admin ", Toast.LENGTH_LONG).show();
                            } else if (!(User.equals(AdminUsername))) {
                                //progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                                //Toast.makeText(MainActivity.this, "Welcome " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    finish();
                    //progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(intent);
                }

            }
        }, SPLASH_SCREEN_TIME_OUT);


    }
}