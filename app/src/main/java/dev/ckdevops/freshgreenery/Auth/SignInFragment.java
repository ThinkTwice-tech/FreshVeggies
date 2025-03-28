package dev.ckdevops.freshgreenery.Auth;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import dev.ckdevops.freshgreenery.Admin.AdminActivity;
import dev.ckdevops.freshgreenery.Dashboard.HomeActivity;
import dev.ckdevops.freshgreenery.R;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String FCMtoken="";
    private EditText editTextEmail, editTextPassword;
    private Button button;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private String email, pass, AdminUsername, AdminPassword;
    private TextView textViewForPass;
    public View view, viewSignin;
    private InputMethodManager inputMethodManager;
    private String mParam1;
    private String mParam2;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = view.findViewById(R.id.progressBar_login);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        textViewForPass = view.findViewById(R.id.TextForgetPassword);
        viewSignin = view.findViewById(R.id.viewFragSignIn);
        button = view.findViewById(R.id.button_sign_in);

        reference = FirebaseDatabase.getInstance().getReference().child("Admin");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AdminUsername = snapshot.child("username").getValue(String.class);
                AdminPassword = snapshot.child("password").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        viewSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideKeyboard(getActivity());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = editTextEmail.getText().toString();
                pass = editTextPassword.getText().toString();

                if (email.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    editTextEmail.setError("Please Enter Email");
                    editTextEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    editTextPassword.setError("Please Enter Password");
                    editTextPassword.requestFocus();
                } else if (!(email.isEmpty() && pass.isEmpty())) {
                    progressBar.setVisibility(View.VISIBLE);
                    System.out.println(AdminUsername + AdminPassword);
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                if (email.equals(AdminUsername)) {

                                    FirebaseMessaging.getInstance().deleteToken()
                                            .addOnCompleteListener(taskT -> {
                                                if (taskT.isSuccessful()) {
                                                    FirebaseMessaging.getInstance().getToken()
                                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<String> taskT) {
                                                                    if (!taskT.isSuccessful()) {
                                                                        Log.w(TAG, "Fetching FCM registration token failed", taskT.getException());
                                                                        return;
                                                                    }

                                                                    // Get new FCM registration token
                                                                    String newToken = taskT.getResult();
                                                                    FCMtoken = newToken;

                                                                    // Log and store the token
                                                                    Log.d(TAG, "New FCM Token: " + FCMtoken);

                                                                    // Save to SharedPreferences or backend
                                                                    if (getActivity() != null) {
                                                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                                        SharedPreferences.Editor editor = preferences.edit();
                                                                        editor.putString("FCMToken", FCMtoken);
                                                                        editor.apply();
                                                                    } else {
                                                                        Log.e("Error in SP", "getActivity() is null");
                                                                    }

                                                                    // Send token to server
                                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                                    databaseReference.child("Admin").child("FCMToken").setValue(FCMtoken)
                                                                            .addOnCompleteListener(task -> {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.d("FCMToken", "Token updated successfully for Admin.");
                                                                                } else {
                                                                                    Log.e("FCMToken", "Failed to update token for Admin", task.getException());
                                                                                }
                                                                            });


                                                                }
                                                            });
                                                } else {
                                                    Log.e(TAG, "Failed to delete FCM token", taskT.getException());
                                                }
                                            });

                                    progressBar.setVisibility(View.GONE);
                                    //Toast.makeText(getContext(), "Admin Login successful!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getContext(), AdminActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else if (!(email.equals(AdminUsername))) {

                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                    String userId = currentUser.getUid();

                                    FirebaseMessaging.getInstance().deleteToken()
                                            .addOnCompleteListener(taskT -> {
                                                if (taskT.isSuccessful()) {
                                                    FirebaseMessaging.getInstance().getToken()
                                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<String> taskT) {
                                                                    if (!taskT.isSuccessful()) {
                                                                        Log.w(TAG, "Fetching FCM registration token failed", taskT.getException());
                                                                        return;
                                                                    }

                                                                    // Get new FCM registration token
                                                                    String newToken = taskT.getResult();
                                                                    FCMtoken = newToken;

                                                                    // Log and store the token
                                                                    Log.d(TAG, "New FCM Token: " + FCMtoken);

                                                                    // Save to SharedPreferences or backend
                                                                    if (getActivity() != null) {
                                                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                                                        SharedPreferences.Editor editor = preferences.edit();
                                                                        editor.putString("FCMToken", FCMtoken);
                                                                        editor.apply();
                                                                    } else {
                                                                        Log.e("Error in SP", "getActivity() is null");
                                                                    }

                                                                    // Send token to server
                                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                                    databaseReference.child("AppUsers").child(userId).child("FCMToken").setValue(FCMtoken)
                                                                            .addOnCompleteListener(task -> {
                                                                                if (task.isSuccessful()) {
                                                                                    Log.d("FCMToken", "Token updated successfully for Admin.");
                                                                                } else {
                                                                                    Log.e("FCMToken", "Failed to update token for Admin", task.getException());
                                                                                }
                                                                            });


                                                                }
                                                            });
                                                } else {
                                                    Log.e(TAG, "Failed to delete FCM token", taskT.getException());
                                                }
                                            });

                                    progressBar.setVisibility(View.GONE);
                                    //Toast.makeText(getContext(), "Users Login successful!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getContext(), HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                editTextEmail.setText("");
                                editTextPassword.setText("");

                            } else {
                                Toast.makeText(getContext(), "Incorrect Credentials!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }

                        }
                    });

                } else {
                    Toast.makeText(getContext(), " Network Error", Toast.LENGTH_SHORT).show();
                }

            }
        });

        textViewForPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Please wait...");
                progressDialog.show();

                email = editTextEmail.getText().toString();
                if (email.isEmpty()) {
                    progressDialog.dismiss();
                    editTextEmail.setError("Please Enter Email");
                    editTextEmail.requestFocus();
                } else if (!(isValidEmail(email))) {
                    editTextEmail.setError("Please Enter Valid Email..!!!");
                } else if (!(email.isEmpty())) {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "An email has been sent!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });


        return view;

    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}