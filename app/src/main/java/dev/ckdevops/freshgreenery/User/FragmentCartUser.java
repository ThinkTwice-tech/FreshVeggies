package dev.ckdevops.freshgreenery.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import dev.ckdevops.freshgreenery.Admin.AddNotification;
import dev.ckdevops.freshgreenery.R;
import dev.ckdevops.freshgreenery.Utilities.AdminHome.CartItems;
import dev.ckdevops.freshgreenery.Utilities.AdminHome.Holder;
import dev.ckdevops.freshgreenery.Utilities.AdminHome.HomeModel;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class FragmentCartUser extends Fragment   {
    ScrollView layout;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ImageButton arrowItem, arrowAddress, arrowBill;
    private LinearLayout hiddenViewItem, hiddenViewAddress, hiddenViewBill;
    private LinearLayout linearLayoutItem, linearLayoutAddress, linearLayoutBill;

    private View view;
    private Thread threadAddress = null;
    private String mobile, address, city, society, flat, key, orderBy, date, NoOfItems, cartTotChrg, time, paymentMethod;
    private MaterialButton materialButton;
    private TextView textViewMobile, textViewCity, textViewAddress, textViewSociety, textViewFlatNo;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private RadioGroup radioGroup;
    private RadioButton radioButton, radioButtonPay;
    private AlertDialog.Builder builderDelete;
    private FirebaseRecyclerOptions<HomeModel> options;
    private FirebaseRecyclerAdapter<HomeModel, Holder> adapter;
    private SimpleDateFormat simpleDateFormat;
    private TextView cart_date, cartNoItems, cartTotalCharge, cartOrderMethod, cartPaymentMethod;
    private String value, Weight;
    private double val;


    String accessToken = "";
    private RecyclerView recyclerView;
    public TextView TotalPriceCart;
    private AlertDialog.Builder builder;
    private TextView OrderIdDialoge, orderPriceDialoge;
    ArrayList<CartItems> list = new ArrayList<CartItems>();
    private RadioGroup radioGroupPay;
    public FragmentCartUser() {
        // Required empty public constructor
    }

    public static FragmentCartUser newInstance(String param1, String param2) {
        FragmentCartUser fragment = new FragmentCartUser();
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

        view = inflater.inflate(R.layout.fragment_cart_user, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        final Date data = new Date();
        simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        date = simpleDateFormat.format(data);
        layout = view.findViewById(R.id.layout);
        //arrowItem = view.findViewById(R.id.arrow_button_Items);
        hiddenViewItem = view.findViewById(R.id.hidden_view_Items);
        linearLayoutItem = view.findViewById(R.id.linearItem);
        materialButton = view.findViewById(R.id.button_order_Cart);

        hiddenViewBill = view.findViewById(R.id.hidden_view_Bill);
        linearLayoutBill = view.findViewById(R.id.linearBill);
        //arrowBill = view.findViewById(R.id.arrow_button_Bill);

        recyclerView = view.findViewById(R.id.recyItemCart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        textViewMobile = view.findViewById(R.id.TextMobileCart);
        textViewAddress = view.findViewById(R.id.TextAddressCart);
        textViewCity = view.findViewById(R.id.TextCityCard);
        textViewSociety = view.findViewById(R.id.TextSocietyNameCart);
        textViewFlatNo = view.findViewById(R.id.TextFlatNoCart);

        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        radioGroupPay = view.findViewById(R.id.radioGroupPayment);
        radioGroupPay.clearCheck();

        //arrowAddress = view.findViewById(R.id.arrow_button_Address);
        hiddenViewAddress = view.findViewById(R.id.hidden_view_Address);
        linearLayoutAddress = view.findViewById(R.id.linearAddress);

        // billing Section id
        cart_date = view.findViewById(R.id.cart_date);
        cartNoItems = view.findViewById(R.id.cartNoItems);
        cartTotalCharge = view.findViewById(R.id.cartTotalCharge);
        cartOrderMethod = view.findViewById(R.id.cartOrderMethod);
        cartPaymentMethod = view.findViewById(R.id.cartPaymentMethod);
        TotalPriceCart = view.findViewById(R.id.TotalPriceCart);

        builder = new AlertDialog.Builder(getContext());

        cart_date.setText(date);

        radioGroup.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {

                    @SuppressLint("ResourceType")
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        radioButton = group.findViewById(checkedId);
                        if (null != radioButton && checkedId > -1) {
                            orderBy = radioButton.getText().toString();
                            cartOrderMethod.setText(orderBy);

                        }

                    }
                });

        radioGroupPay.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {

                    @SuppressLint("ResourceType")
                    @Override
                    public void onCheckedChanged(RadioGroup groupPay, int checkedIdPay) {
                        radioButtonPay = groupPay.findViewById(checkedIdPay);
                        if (null != radioButtonPay && checkedIdPay > -1) {
                            paymentMethod = radioButtonPay.getText().toString();
                            cartPaymentMethod.setText(paymentMethod);

                            if(paymentMethod.equals("Payment through UPI")){
                                materialButton.setText("Pay and Order");
                            }else{
                                materialButton.setText("Order");
                            }

                        }

                    }
                });

        /*arrowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenViewItem.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(linearLayoutItem,
                            new AutoTransition());
                    hiddenViewItem.setVisibility(View.GONE);
                    arrowItem.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(linearLayoutItem,
                            new AutoTransition());
                    hiddenViewItem.setVisibility(View.VISIBLE);
                    arrowItem.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });

        arrowBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenViewBill.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(linearLayoutBill,
                            new AutoTransition());
                    hiddenViewBill.setVisibility(View.GONE);
                    arrowBill.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(linearLayoutBill,
                            new AutoTransition());
                    hiddenViewBill.setVisibility(View.VISIBLE);
                    arrowBill.setImageResource(R.drawable.ic_baseline_expand_less_24);


                }
            }
        });

        arrowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenViewAddress.getVisibility() == View.VISIBLE) {

                    TransitionManager.beginDelayedTransition(linearLayoutAddress,
                            new AutoTransition());
                    hiddenViewAddress.setVisibility(View.GONE);
                    arrowAddress.setImageResource(R.drawable.ic_baseline_expand_more_24);
                } else {

                    TransitionManager.beginDelayedTransition(linearLayoutAddress,
                            new AutoTransition());
                    hiddenViewAddress.setVisibility(View.VISIBLE);
                    arrowAddress.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });*/

        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(getContext(), "No Order Method has been selected", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton radioButton = (RadioButton) radioGroup.findViewById(selectedId);
                    orderBy = radioButton.getText().toString();
                }

                date = cart_date.getText().toString();
                NoOfItems = cartNoItems.getText().toString();
                orderBy = cartOrderMethod.getText().toString();
                cartTotChrg = cartTotalCharge.getText().toString();

                mobile = textViewMobile.getText().toString();
                address = textViewAddress.getText().toString();
                city = textViewCity.getText().toString();
                society = textViewSociety.getText().toString();
                flat = textViewFlatNo.getText().toString();


                if (date.isEmpty()) {
                } else if (NoOfItems.isEmpty()) {
                    Toast.makeText(getContext(), "Select Items Quantity...", Toast.LENGTH_SHORT).show();
                } else if (orderBy.isEmpty()) {
                    Toast.makeText(getContext(), "Select Delivery Option...", Toast.LENGTH_SHORT).show();
                } else if (cartTotChrg.isEmpty()) {
                    Toast.makeText(getContext(), "Select Items Quantity...", Toast.LENGTH_SHORT).show();
                } else if (list.isEmpty()) {
                    Toast.makeText(getContext(), "Select Items Quantity...", Toast.LENGTH_SHORT).show();
                } else if (!(date.isEmpty() && NoOfItems.isEmpty() && orderBy.isEmpty() && cartTotChrg.isEmpty() && list.isEmpty())) {

                    materialButton.setVisibility(View.VISIBLE);
                    final Calendar calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("hh:mm");
                    time = simpleDateFormat.format(calendar.getTime());
                    final String Tempkey = generateRandomString(6);
                    key = Tempkey + "-" + date + "-" + time;

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogLayout = inflater.inflate(R.layout.order_confirm_dialoge, null);
                    OrderIdDialoge = dialogLayout.findViewById(R.id.textVieworderidDialoge);
                    orderPriceDialoge = dialogLayout.findViewById(R.id.textViewMRPDialoge);
                    builder.setTitle("Order Confirmation");
                    OrderIdDialoge.setText(key);
                    orderPriceDialoge.setText(cartTotChrg + "Rs");

                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                          /*  Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
                            String transcId = df.format(c);*/
                            if(paymentMethod.equals("Payment through UPI")){
                                initiateUPIPayment(cartTotChrg, "paytmqr1gg0y58rgp@paytm", "Fresh Veggies", "Order Payment");
                                //makePayment(cartTotChrg, "9409366655@idfcfirst", "Fresh Veggies", "Order Payment", transcId);
                            }else{
                                confirmOrder(getActivity());
                            }



                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setView(dialogLayout);
                    builder.show();

                }

            }
        });


        final String Uid = firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference dff = FirebaseDatabase.getInstance().getReference().child("Cart").child(firebaseAuth.getCurrentUser().getUid());
        dff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("Cart").child(Uid);

                options = new FirebaseRecyclerOptions.Builder<HomeModel>().setQuery(df, new SnapshotParser<HomeModel>() {

                    @NonNull
                    @Override
                    public HomeModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new HomeModel(

                                snapshot.child("CartName").getValue(String.class),
                                snapshot.child("CartPrice").getValue(String.class),
                                snapshot.child("CartQuantity").getValue(String.class),
                                snapshot.child("CardID").getValue(String.class)
                        );

                    }
                }).build();
                adapter = new FirebaseRecyclerAdapter<HomeModel, Holder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull final Holder holder, int position, @NonNull final HomeModel model) {

                        holder.setTxtTitleCart(model.getName());
                        holder.setTxtRateCart(model.getPrice());
                        holder.setTxtWeightCart(model.getQuantity());

                        NoOfItems = String.valueOf(adapter.getItemCount());
                        if (adapter.getItemCount() == 0) {
                            materialButton.setVisibility(View.GONE);
                        } else {
                            materialButton.setVisibility(View.VISIBLE);
                        }
                        cartNoItems.setText(NoOfItems);


                        databaseReference = FirebaseDatabase.getInstance().getReference().child("VegetableEntry").child(model.getID());
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                value = snapshot.child("TotalQuantity").getValue(String.class);
                                Weight = snapshot.child("TotalWeight").getValue(String.class);

                                holder.setTxtFromEndQuantity(value, Weight);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.imageViewMinusCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //TotalPriceCart.setText(String.valueOf(0.0));
                                holder.imageViewCheck.setVisibility(View.VISIBLE);
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("VegetableEntry").child(model.getID());
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        value = snapshot.child("TotalQuantity").getValue(String.class);
                                        val = Double.parseDouble(value);
                                        double i = Double.parseDouble(holder.textViewUserQuantityCard.getText().toString());
                                        if (i < val) {
                                            holder.imageViewPlusCart.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(getContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                                            holder.imageViewPlusCart.setVisibility(View.INVISIBLE);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                double i = Double.parseDouble(holder.textViewUserQuantityCard.getText().toString());

                                i = i - 0.25;
                                if (i <= 0.25) {

                                    holder.imageViewMinusCart.setVisibility(View.INVISIBLE);

                                } else {

                                    holder.imageViewMinusCart.setVisibility(View.VISIBLE);
                                }

                                holder.setTxtUserQuantCart(String.valueOf(i));

                                if (model.getQuantity().equals("kilo")) {

                                    int p = Integer.parseInt(model.getPrice());
                                    double tot = i * p;
                                    //Log.e(TAG, "inside kilo=======" + tot);
                                    holder.setTxtTotalRate(String.valueOf(tot));

                                } else if (model.getQuantity().equals("gram")) {

                                    int p = Integer.parseInt(model.getPrice());
                                    double tot = i * p;
                                    //Log.e(TAG, "inside kilo=======" + tot);
                                    holder.setTxtTotalRate(String.valueOf(tot));
                                }
                            }
                        });

                        holder.imageViewPlusCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //TotalPriceCart.setText(String.valueOf(0.0));

                                holder.imageViewCheck.setVisibility(View.VISIBLE);
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("VegetableEntry").child(model.getID());
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        value = snapshot.child("TotalQuantity").getValue(String.class);
                                        val = Double.parseDouble(value);
                                        double i = Double.parseDouble(holder.textViewUserQuantityCard.getText().toString());
                                        if (i < val) {

                                        } else {
                                            Toast.makeText(getContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                                            holder.imageViewPlusCart.setVisibility(View.INVISIBLE);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                double i = Double.parseDouble(holder.textViewUserQuantityCard.getText().toString());
                                i = i + 0.25;

                                if (i >= 0.25) {
                                    holder.imageViewMinusCart.setVisibility(View.VISIBLE);
                                } else {

                                    holder.imageViewMinusCart.setVisibility(View.INVISIBLE);
                                }

                                holder.setTxtUserQuantCart(String.valueOf(i));

                                if (model.getQuantity().equals("kilo")) {

                                    int p = Integer.parseInt(model.getPrice());
                                    double tot = i * p;
                                    holder.setTxtTotalRate(String.valueOf(tot));

                                } else if (model.getQuantity().equals("gram")) {

                                    int p = Integer.parseInt(model.getPrice());
                                    double tot = i * p;
                                    holder.setTxtTotalRate(String.valueOf(tot));
                                }

                            }
                        });

                        holder.imageViewCheck.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                double currentTotalPrice = Double.parseDouble(TotalPriceCart.getText().toString());
                                double itemPrice = Double.parseDouble(holder.cart_totalRate.getText().toString());

                                // Toggle item state fvfvti
                                if (!model.isInCart()) { // Item is not in the cart
                                    currentTotalPrice += itemPrice; // Add item price to total
                                    model.setInCart(true); // Mark item as added
                                    list.add(new CartItems(model.getName(),
                                            holder.textViewUserQuantityCard.getText().toString(),
                                            String.valueOf(itemPrice))); // Add to cart list
                                } else { // Item is already in the cart
                                    currentTotalPrice -= itemPrice; // Subtract item price from total
                                    model.setInCart(false); // Mark item as removed
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        list.removeIf(cartItem -> model.getName().equals(model.getName())); // Remove from cart list
                                    }
                                }

                                // Update UI
                                TotalPriceCart.setText(String.valueOf(currentTotalPrice));
                                cartTotalCharge.setText(String.valueOf(currentTotalPrice));

                                /*double initTotal = Double.parseDouble(TotalPriceCart.getText().toString());
                                double initCart = Double.parseDouble(holder.cart_totalRate.getText().toString());



                                initTotal = initTotal + initCart;
                                TotalPriceCart.setText(String.valueOf(initTotal));
                                holder.imageViewCheck.setVisibility(View.GONE);
                                cartTotalCharge.setText(TotalPriceCart.getText().toString());

                                String Quantity = holder.textViewUserQuantityCard.getText().toString();
                                String rate = holder.cart_totalRate.getText().toString();

                                list.add(new CartItems(model.getName(), Quantity, rate));*/

                            }
                        });

                        final DatabaseReference Delete = FirebaseDatabase.getInstance().getReference();
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                builderDelete = new AlertDialog.Builder(getContext());
                                builderDelete.setMessage("Do You Want To Delete Item ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                TotalPriceCart.setText(String.valueOf(0.0));

                                                Query RoomQuery = Delete.child("Cart").child(firebaseAuth.getCurrentUser().getUid()).orderByChild("CartName").equalTo(model.getName());
                                                RoomQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                                            appleSnapshot.getRef().removeValue();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        System.out.println("On Canceled");
                                                    }

                                                });

                                                Toast.makeText(getContext(), "Delete Content Successfully", Toast.LENGTH_LONG).show();

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert = builderDelete.create();
                                alert.setTitle("Content Delete Alert");
                                alert.show();

                                return false;
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_card, parent, false);

                        return new Holder(view);
                    }
                };

                adapter.startListening();
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        threadAddress = new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        key = firebaseAuth.getCurrentUser().getUid();

                        databaseReference = FirebaseDatabase.getInstance().getReference().child("Address");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                mobile = snapshot.child(key).child("Mobile").getValue(String.class);
                                address = snapshot.child(key).child("Address").getValue(String.class);
                                city = snapshot.child(key).child("City").getValue(String.class);
                                society = snapshot.child(key).child("SocietyName").getValue(String.class);
                                flat = snapshot.child(key).child("FlatNo").getValue(String.class);

                                if (mobile == null && address == null) {

                                    materialButton.setVisibility(View.GONE);
                                    if (isAdded() && getActivity() != null) { // Ensure fragment is attached to an activity
                                        Snackbar snackbar = Snackbar.make(layout,
                                                "Filled Address Section First",
                                                Snackbar.LENGTH_LONG);

                                        View sbView = snackbar.getView();
                                        sbView.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red));
                                        snackbar.show();
                                    } else {
                                        Log.e("SnackbarError", "Fragment is not attached to an activity!");
                                    }


                                    /*Snackbar snackbar
                                            = Snackbar
                                            .make(layout,
                                                    "Filled Address Section First",
                                                    Snackbar.LENGTH_LONG);


                                    View layout = requireView().findViewById(R.id.layout);
                                    layout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red));
                                    snackbar.show();*/
                                    /*Snackbar snackBar = Snackbar.make(requireView(), "Filled Address Section First..", Snackbar.LENGTH_LONG)
                                            .setAction("Yes", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                          *//*  FragmentLocationUser fragment2 = new FragmentLocationUser();
                                            FragmentManager fragmentManager = getFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.containerHomeUser, fragment2);
                                            fragmentTransaction.commit();*//*
                                        }
                                    });
                                    //snackBar.setActionTextColor(Color.BLUE);
                                    View snackBarView = snackBar.getView();
                                    TextView textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text);
                                    textView.setTextColor(Color.RED);
                                    snackBar.show();*/

                                   /* Snackbar snackBar = Snackbar.make(requireView(), "Filled Address Section First..", Snackbar.LENGTH_LONG)
                                            .setAction("Yes", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    // Perform action on click, like navigating to another fragment
                                                    FragmentLocationUser fragment2 = new FragmentLocationUser();
                                                    FragmentManager fragmentManager = getFragmentManager();
                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                    fragmentTransaction.replace(R.id.containerHomeUser, fragment2);
                                                    fragmentTransaction.commit();
                                                }
                                            });
                                    snackBar.setActionTextColor(Color.BLUE);
                                    View snackBarView = snackBar.getView();
                                    TextView textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text); // Correct ID

                                    if (textView != null) {
                                        textView.setTextColor(Color.RED); // Change the text color to red
                                    }

                                    snackBar.show();*/


                                } else if (!(mobile == null && address == null)) {

                                    materialButton.setVisibility(View.VISIBLE);

                                    textViewMobile.setText(mobile);
                                    textViewAddress.setText(address);
                                    textViewCity.setText(city);
                                    textViewSociety.setText(society);
                                    textViewFlatNo.setText(flat);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                });
            }
        });
        threadAddress.start();

        return view;
    }




    public static String extractInt(String str) {

        str = str.replaceAll("[^\\d]", " ");
        str = str.trim();
        str = str.replaceAll(" +", " ");

        if (str.equals(""))
            return "-1";

        return str;
    }

    public String generateRandomString(int length) {

        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";

        String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
        SecureRandom random = new SecureRandom();

        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            sb.append(rndChar);
        }

        return sb.toString();
    }


    private void confirmOrder(Context context){

        String uid = firebaseAuth.getCurrentUser().getUid();


        final DatabaseReference dfUser = FirebaseDatabase.getInstance().getReference().child("Billing").child(uid);


        dfUser.child(key).child("Bill").child("OrderID").setValue(key);
        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(uid);
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("Name").getValue().toString();

                dfUser.child(key).child("Bill").child("UserName").setValue(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        dfUser.child(key).child("Bill").child("Date").setValue(date);
        dfUser.child(key).child("Bill").child("Time").setValue(time);
        dfUser.child(key).child("Bill").child("NoOfItems").setValue(NoOfItems);
        dfUser.child(key).child("Bill").child("OrderMethod").setValue(orderBy);
        dfUser.child(key).child("Bill").child("TotalRate").setValue(cartTotChrg);
        dfUser.child(key).child("Bill").child("PaymentMethod").setValue(paymentMethod);

        dfUser.child(key).child("Bill").child("Mobile").setValue(mobile);
        dfUser.child(key).child("Bill").child("Address").setValue(address);
        dfUser.child(key).child("Bill").child("City").setValue(city);
        dfUser.child(key).child("Bill").child("SocietyName").setValue(society);
        dfUser.child(key).child("Bill").child("FlatNo").setValue(flat);

        Iterator itr = list.iterator();
        while (itr.hasNext()) {

            CartItems item = (CartItems) itr.next();
            String itemKey = dfUser.push().getKey();
            dfUser.child(key).child("ItemList").child(itemKey).child("Name").setValue(item.name);
            dfUser.child(key).child("ItemList").child(itemKey).child("weight").setValue(item.weight);
            dfUser.child(key).child("ItemList").child(itemKey).child("rate").setValue(item.rate);

        }


        //admin section
        final DatabaseReference Admin = FirebaseDatabase.getInstance().getReference().child("AdminData").child("Billing");
        Admin.child(key).child("Bill").child("OrderID").setValue(key);
        Admin.child(key).child("Bill").child("Date").setValue(date);
        Admin.child(key).child("Bill").child("Time").setValue(time);
        Admin.child(key).child("Bill").child("NoOfItems").setValue(NoOfItems);
        Admin.child(key).child("Bill").child("OrderMethod").setValue(orderBy);
        Admin.child(key).child("Bill").child("TotalRate").setValue(cartTotChrg);
        Admin.child(key).child("Bill").child("PaymentMethod").setValue(paymentMethod);

        Admin.child(key).child("Bill").child("Mobile").setValue(mobile);
        Admin.child(key).child("Bill").child("Address").setValue(address);
        Admin.child(key).child("Bill").child("City").setValue(city);
        Admin.child(key).child("Bill").child("SocietyName").setValue(society);
        Admin.child(key).child("Bill").child("FlatNo").setValue(flat);

        final DatabaseReference admin = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(uid);
        admin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("Name").getValue().toString();

                Admin.child(key).child("Bill").child("UserName").setValue(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        Iterator itrad = list.iterator();
        while (itrad.hasNext()) {

            CartItems item = (CartItems) itrad.next();
            String itemKey = dfUser.push().getKey();
            Admin.child(key).child("ItemList").child(itemKey).child("Name").setValue(item.name);
            Admin.child(key).child("ItemList").child(itemKey).child("weight").setValue(item.weight);
            Admin.child(key).child("ItemList").child(itemKey).child("rate").setValue(item.rate);
        }


        //admin History
        final DatabaseReference moveToHis = FirebaseDatabase.getInstance().getReference().child("AdminData").child("History");

        moveToHis.child(key).child("Bill").child("OrderID").setValue(key);
        moveToHis.child(key).child("Bill").child("Date").setValue(date);
        moveToHis.child(key).child("Bill").child("Time").setValue(time);
        moveToHis.child(key).child("Bill").child("NoOfItems").setValue(NoOfItems);
        moveToHis.child(key).child("Bill").child("OrderMethod").setValue(orderBy);
        moveToHis.child(key).child("Bill").child("TotalRate").setValue(cartTotChrg);
        moveToHis.child(key).child("Bill").child("PaymentMethod").setValue(paymentMethod);

        moveToHis.child(key).child("Bill").child("Mobile").setValue(mobile);
        moveToHis.child(key).child("Bill").child("Address").setValue(address);
        moveToHis.child(key).child("Bill").child("City").setValue(city);
        moveToHis.child(key).child("Bill").child("SocietyName").setValue(society);
        moveToHis.child(key).child("Bill").child("FlatNo").setValue(flat);

        final DatabaseReference userName = FirebaseDatabase.getInstance().getReference().child("AppUsers").child(uid);
        userName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String userName = snapshot.child("Name").getValue().toString();

                moveToHis.child(key).child("Bill").child("UserName").setValue(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Iterator itrHis = list.iterator();
        while (itrHis.hasNext()) {

            CartItems item = (CartItems) itrHis.next();
            String itemKey = dfUser.push().getKey();
            moveToHis.child(key).child("ItemList").child(itemKey).child("Name").setValue(item.name);
            moveToHis.child(key).child("ItemList").child(itemKey).child("weight").setValue(item.weight);
            moveToHis.child(key).child("ItemList").child(itemKey).child("rate").setValue(item.rate);
        }

        //Stock Maintain Section
        Iterator itrSearch = list.iterator();
        while (itrSearch.hasNext()) {

            final CartItems item = (CartItems) itrSearch.next();
            String itemName = item.name;
            final double itemWeight = Double.parseDouble(item.weight);
            final DatabaseReference dfSearch = FirebaseDatabase.getInstance().getReference().child("VegetableEntry");
            Query query = dfSearch.orderByChild("Name").equalTo(itemName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String key = snapshot.getKey();
                        double ItemQuantity = Double.parseDouble(snapshot.child("TotalQuantity").getValue(String.class));
                        ItemQuantity = ItemQuantity - itemWeight;
                        String s = Double.toString(ItemQuantity);

                        final DatabaseReference updateVege = FirebaseDatabase.getInstance().getReference().child("VegetableEntry");
                        updateVege.child(key).child("TotalQuantity").setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("Project", "Update Complete");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Project", "error :" + e);
                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("On Canceled");
                }

            });
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("Admin");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                        String fcmToken = dataSnapshot.child("FCMToken").getValue(String.class);
                        if (!fcmToken.isEmpty()) {
                            if (context != null) {
                                SendAdminNoti sendAdminnoti = new SendAdminNoti(context);
                                sendAdminnoti.execute(fcmToken);
                            } else {
                                Log.e("confirmOrder", "Context is null, cannot execute notification");
                            }
                    } else {
                        Log.d("FirebaseDebug", "No FCM tokens found.");
                        Toast.makeText(getActivity(), "No FCM tokens found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("FirebaseDebug", "No data found in AppUsers.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        FirebaseDatabase.getInstance().getReference().child("Cart").removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.e("Project", "cart delete Complete");
            }
        });

        Toast.makeText(getContext(), "Order Confirmed!", Toast.LENGTH_SHORT).show();

        FragmentHomeUser homeUser = new FragmentHomeUser();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerHomeUser, homeUser);
        fragmentTransaction.commit();
    }

    private void initiateUPIPayment(String amount, String upiId, String name, String transactionNote) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId) // Payee UPI ID
                .appendQueryParameter("pn", name) // Payee Name
                .appendQueryParameter("tn", transactionNote) // Transaction Note
                .appendQueryParameter("am", amount) // Amount
                .appendQueryParameter("cu", "INR") // Currency
                .build();

        Log.d("UPI Transaction", "Amount: " + amount);
        Log.d("UPI Transaction", "UPI ID: " + upiId);
        Log.d("UPI Transaction", "Name: " + name);
        Log.d("UPI Transaction", "Note: " + transactionNote);

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        if (chooser.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(chooser, 1001); // Request code for UPI Payment
        } else {
            Toast.makeText(getContext(), "No UPI app found. Please install one to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK || resultCode == 11) {
                if (data != null) {
                    String response = data.getStringExtra("response");
                    Log.d("UPI Response", "Response: " + response);
                    handleUPIResponse(response);
                } else {
                    Log.d("UPI Response", "No response from UPI app");
                    handleUPIResponse("Payment Cancelled");
                }
            } else {
                Log.d("UPI Response", "Payment Failed or Activity Canceled");
                handleUPIResponse("Payment Failed");
            }
        }
    }


    private void handleUPIResponse(String response) {
        if (response == null || response.isEmpty()) {
            Toast.makeText(getContext(), "No response from UPI app. Payment Cancelled.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse the response string
        String status = "";
        String approvalRefNo = "";
        String[] responseParams = response.split("&");
        for (String param : responseParams) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                if (keyValue[0].equalsIgnoreCase("Status")) {
                    status = keyValue[1];
                } else if (keyValue[0].equalsIgnoreCase("txnRef")) {
                    approvalRefNo = keyValue[1];
                }
            }
        }

        // Handle status
        if (status.equalsIgnoreCase("SUCCESS")) {
            Toast.makeText(getContext(), "Payment Successful. Ref: " + approvalRefNo, Toast.LENGTH_SHORT).show();
            savePaymentDetailsToFirebase(true);
        } else if (status.equalsIgnoreCase("FAILURE")) {
            Toast.makeText(getContext(), "Payment Failed. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Payment Cancelled.", Toast.LENGTH_SHORT).show();
        }
    }



    private void savePaymentDetailsToFirebase(boolean isSuccess) {
        String paymentStatus = isSuccess ? "Success" : "Failed";

        DatabaseReference dfUser = FirebaseDatabase.getInstance().getReference().child("Billing").child(firebaseAuth.getCurrentUser().getUid());
        dfUser.child(key).child("Bill").child("PaymentStatus").setValue(paymentStatus);
        dfUser.child(key).child("Bill").child("PaymentMethod").setValue("Online Payment");

        if (isSuccess) {
            confirmOrder(getActivity()); // Call the existing order confirmation method
        }
    }


    private class SendAdminNoti extends AsyncTask<String, Void, String> {

        private Context context;

        public SendAdminNoti(Context context) {
            this.context = context;
        }


        @Override
        protected String doInBackground(String... params) {
            // Perform the network request on a background thread
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonBody = new JSONObject();

            try {


                    AssetManager assetManager = context.getAssets();
                    InputStream inputStream = assetManager.open("service-account-file.json");
                    GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
                            .createScoped("https://www.googleapis.com/auth/firebase.messaging");

                    credentials.refreshIfExpired();
                    accessToken = credentials.getAccessToken().getTokenValue();






                // Set up the message and notification payload
                JSONObject notification = new JSONObject();
                notification.put("title", "New Order");
                notification.put("body", "New Order Confirmed! Check in Notification.");

                JSONObject message = new JSONObject();
                message.put("notification", notification);
                message.put("token", params[0]);

                JSONObject mainPayload = new JSONObject();
                mainPayload.put("message", message);

                // Request Body

                RequestBody body = RequestBody.create(mediaType, mainPayload.toString());




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