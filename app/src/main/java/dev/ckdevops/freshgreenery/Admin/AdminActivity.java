package dev.ckdevops.freshgreenery.Admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import dev.ckdevops.freshgreenery.Admin.Fragments.AddFragment;
import dev.ckdevops.freshgreenery.Admin.Fragments.HistoryFragment;
import dev.ckdevops.freshgreenery.Admin.Fragments.HomeFragment;
import dev.ckdevops.freshgreenery.Admin.Fragments.NotificationFragment;
import dev.ckdevops.freshgreenery.Admin.Fragments.ReportFragment;
import dev.ckdevops.freshgreenery.Auth.AuthActivity;
import dev.ckdevops.freshgreenery.R;


public class AdminActivity extends AppCompatActivity {
    private FloatingActionButton fabHome;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private AlertDialog.Builder alertDialogBuilder;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbarAdmin);
        toolbar.setTitle("Admin");
        fabHome = findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.navigationView);
        setUpNavView();
        loadFragment(new HomeFragment());
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
                            Toast.makeText(AdminActivity.this, "Go Back TO HomePage!", Toast.LENGTH_SHORT).show();
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

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment());
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // Replace the container with the new fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null); // Optional: Add to back stack for navigation
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_Logout:

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
            selectFragment(menu.getItem(2));

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
            case R.id.notification:
                pushFragment(new NotificationFragment());
                break;

            case R.id.add:
                pushFragment(new AddFragment());
                break;

            case R.id.history:
                pushFragment(new HistoryFragment());
                break;

            case R.id.report:
                pushFragment(new ReportFragment());
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

                fragmentTransaction.replace(R.id.container, fragment).commit();

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


}