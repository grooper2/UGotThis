package com.example.ppw1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.UGotThis;
import ui.UGotThisRecyclerAdapter;
import utils.UGotThisApi;


public class Task_List_Activity extends AppCompatActivity {

    public static String id = "";
    private static final String TAG = "Task_List_Activity";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    public static List<UGotThis> uGotThisList;
    private RecyclerView recyclerView;
    private static UGotThisRecyclerAdapter reflectionRecyclerAdapter;
    private CollectionReference collectionReference = db.collection("UGotThis");
    private TextView noReflectionEntry;
    public CheckBox checkBox;
    public boolean complete = false;

    public static int ActivityPosition;
    private DocumentReference DocRef;
    private AlertDialog dialog;
    private FirebaseStorage sStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task__list_);
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();
        uGotThisList = new ArrayList<>();
        noReflectionEntry = findViewById(R.id.list_no_reflection);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sStorage = FirebaseStorage.getInstance();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_add:
                //take user to add reflection
                if (user != null && firebaseAuth != null){
                    startActivity(new Intent(Task_List_Activity.this, Add_Activity.class));
                    //finish();
                }
                break;
            case R.id.action_signout:
                //sign out
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(Task_List_Activity.this, Login_Activity.class));
                    //finish();
                }
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", UGotThisApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){

                            for (QueryDocumentSnapshot ugotthis : queryDocumentSnapshots){
                                UGotThis uGotThis = ugotthis.toObject(UGotThis.class);
                                id = ugotthis.getId();
                                uGotThis.setId(ugotthis.getId());
                                uGotThisList.add(uGotThis);
                            }

                            //invoke recycler view
                            reflectionRecyclerAdapter = new UGotThisRecyclerAdapter(Task_List_Activity.this, uGotThisList, new UGotThisRecyclerAdapter.ActivityListener() {
                                @Override
                                public void onActivityComplete(final int ActivityPosition) {


                                    final UGotThis uGotThis = uGotThisList.get(ActivityPosition);
                                    DocRef = db.collection("UGotThis").document(uGotThis.getId());
                                    final UGotThis updateTask = new UGotThis();
                                    final String status = "Finished";

                                    dialog = new AlertDialog.Builder(Task_List_Activity.this)
                                            .setIcon(android.R.drawable.ic_menu_edit)
                                        .setTitle("Manage Task ")
                                        .setMessage(Html.fromHtml("<font color = '#ffffff'> Delete </font>"))
                                        .setPositiveButton(Html.fromHtml("<font color = '#0083FF'> Edit </font>"),
                                                            new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                uGotThisList.remove(uGotThis);
                                                DocRef.update(uGotThis.getDiscription(), FieldValue.delete());
                                                startActivity(new Intent(Task_List_Activity.this, Edit_Activity.class));
                                                finish();
                                            }
                                        })
                                        .setNegativeButton(Html.fromHtml("<font color = '#ff0000'> Delete </font>"),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {


                                                       uGotThisList.remove(uGotThis);
                                                       StorageReference storageReference = sStorage.getReferenceFromUrl(uGotThis.getImageUrl());
                                                       storageReference.delete();
                                                       DocRef.update(uGotThis.getDiscription(), FieldValue.delete());
                                                       DocRef.delete();
                                                       reflectionRecyclerAdapter.notifyDataSetChanged();
                                                       reflectionRecyclerAdapter.notifyItemRemoved(ActivityPosition);

                                                    }
                                                })
                                        .setNeutralButton(Html.fromHtml("<font color = '#22c95c'> Completed </font>"),
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("status", status);


                                                        Log.d(TAG,"Almost" );

                                                        DocRef.update(map)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG,"Success" );
                                                                        reflectionRecyclerAdapter.notifyDataSetChanged();
                                                                        startActivity(new Intent(Task_List_Activity.this, Task_List_Activity.class));
                                                                    }
                                                                });
                                                    }
                                                })
                                        .show();
                                }
                            });
                            recyclerView.setAdapter(reflectionRecyclerAdapter);
                            reflectionRecyclerAdapter.notifyDataSetChanged();

                        }
                        else{
                            noReflectionEntry.setVisibility(View.VISIBLE);
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}

