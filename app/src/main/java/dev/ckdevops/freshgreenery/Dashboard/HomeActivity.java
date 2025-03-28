package dev.ckdevops.freshgreenery.Dashboard;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import dev.ckdevops.freshgreenery.Auth.AuthActivity;
import dev.ckdevops.freshgreenery.PrivacyPolicy;
import dev.ckdevops.freshgreenery.R;
import dev.ckdevops.freshgreenery.User.FragmentCartUser;
import dev.ckdevops.freshgreenery.User.FragmentHistoryUser;
import dev.ckdevops.freshgreenery.User.FragmentHomeUser;
import dev.ckdevops.freshgreenery.User.FragmentLocationUser;

public class HomeActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private AlertDialog.Builder alertDialogBuilder;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbarAdmin);
        bottomNavigationView = findViewById(R.id.BotnavViewUserHome);
        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(uid);
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                toolbar.setTitle(snapshot.child("Name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
            else {
                // repeat the permission or open app details
            }
        }

        setSupportActionBar(toolbar);
        setUpNavView();

        if (isConnected()) {
            //Toast.makeText(this, "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection Alert")
                    .setMessage("GO to Setting ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            Toast.makeText(HomeActivity.this, "Go Back TO HomePage!", Toast.LENGTH_SHORT).show();
                        }
                    });
            //Creating dialog box
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, AuthActivity.class));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });


        initFab();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_Logout_user:

                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Logout...");
                alertDialogBuilder.setMessage("Do You Want To Logout ?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
        return true;
    }


    private void setUpNavView() {
        if (bottomNavigationView != null) {
            Menu menu = bottomNavigationView.getMenu();
            selectFragment(menu.getItem(0));

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    selectFragment(item);
                    return false;
                }
            });
        }
    }


    protected void selectFragment(MenuItem item) {

        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.menu_home_user:
                pushFragment(new FragmentHomeUser());
                break;

            case R.id.menu_cart_user:
                pushFragment(new FragmentCartUser());
                break;

            case R.id.menu_address_user:
                pushFragment(new FragmentLocationUser());
                break;

            case R.id.menu_history_user:
                pushFragment(new FragmentHistoryUser());
                break;

        }


    }

    protected void pushFragment(Fragment fragment) {

        if (fragment == null) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {

                fragmentTransaction.replace(R.id.containerHomeUser, fragment).commit();

            }

        }

    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Toast.makeText(this, "Connection Issue" + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


    private void initFab() {
        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, PrivacyPolicy.class);
                startActivity(i);
                //setSnackBar(contentView,"The Calender Features is Under Development, You can use this in next Update");
               /* Snackbar snackbar
                        = Snackbar
                        .make(root,
                                "The Calender Features is Under Development, You can use this in next Update",
                                Snackbar.LENGTH_LONG);

                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, R.color.black));
                snackbar.show();*/

            }
        });
    }

}