package com.example.ppw1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.UGotThis;
import ui.UGotThisRecyclerAdapter;
import utils.UGotThisApi;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.ppw1.Task_List_Activity.ActivityPosition;
import static com.example.ppw1.Task_List_Activity.uGotThisList;

public class Edit_Activity extends AppCompatActivity {

    public static String id = "";
    private static final int GALLERY_CODE = 1;
    private static final String TAG = "Add_Activity";
    private static EditText title;
    private static EditText discription;
    private ImageView postCameraButton;
    private static String currentUserId;
    private static String currentUserName;
    private Button done;
    static FirebaseStorage storage = FirebaseStorage.getInstance();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DocumentReference DocRef;

    // connection to firestore
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static StorageReference storageReference;

    private static CollectionReference collectionReference = db.collection("UGotThis");
    private static Uri imageUri;
    private static UGotThisRecyclerAdapter reflectionRecyclerAdapter;

    private UGotThisRecyclerAdapter positionAdapter;
    private List<UGotThis> uGotThisList;
    public int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_);



        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        postCameraButton = findViewById(R.id.postCameraButton);
        title = findViewById(R.id.title);
        discription = findViewById(R.id.discription);
        done = findViewById(R.id.btn_done);

        uGotThisList = new ArrayList<>();

        discription.setScroller(new Scroller(getApplicationContext()));
        discription.setVerticalScrollBarEnabled(true);
        discription.setMinLines(1);
        discription.setMaxLines(10);

        if (UGotThisApi.getInstance() != null) {
            currentUserId = UGotThisApi.getInstance().getUserId();
            currentUserName = UGotThisApi.getInstance().getUsername();


        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();
                if (user != null) {


                } else {


                }
            }
        };

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveJournal();
            }
        });

        postCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*"); //anything that is image related
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });
    }

    protected void saveJournal() {
        super.onStart();

        final String Title = title.getText().toString().trim();
        final String Discription = discription.getText().toString().trim();
        final UGotThis updateTask = new UGotThis();

        collectionReference.whereEqualTo("userId",UGotThisApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        Log.d(TAG,"I know who you are " );

                        if (!queryDocumentSnapshots.isEmpty()) {

                            for (QueryDocumentSnapshot ugotthis : queryDocumentSnapshots) {
                                UGotThis uGotThis = ugotthis.toObject(UGotThis.class);
                                id = ugotthis.getId();
                                uGotThis.setId(ugotthis.getId());
                                uGotThisList.add(uGotThis);

                            }

                            Log.d(TAG,"On The Way" );


                            final UGotThis uGotThis = uGotThisList.get(ActivityPosition);
//                                    DocRef = db.collection("UGotThis").document(uGotThis.getId());
                            final UGotThis updateTask = new UGotThis();

                            final DocumentReference DocRef = FirebaseFirestore.getInstance()
                                    .collection("UGotThis")
                                    .document(uGotThis.getId());

                            if (!TextUtils.isEmpty(Title) && !TextUtils.isEmpty(Discription) && imageUri != null) {

                                final StorageReference filepath = storageReference.child("ugotthis_images").child("my_image_" + Timestamp.now().getSeconds());


                                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                String imageUrl = uri.toString();

                                                Map<String, Object> map = new  HashMap<>();
                                                map.put("discription", Discription);
                                                map.put("title", Title);
                                                map.put("imageUrl",imageUrl);

                                                Log.d(TAG,"Almost" );

                                                DocRef.update(map)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG,"Success" );
                                                                positionAdapter.notifyDataSetChanged();
                                                                startActivity(new Intent(Edit_Activity.this, Task_List_Activity.class));
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e(TAG, "Failed");
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                });
                            }

                            positionAdapter = new UGotThisRecyclerAdapter(Edit_Activity.this, uGotThisList, new UGotThisRecyclerAdapter.ActivityListener()
                            {
                                @Override
                                public void onActivityComplete(final int ActivityPosition) {




                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {




                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); //we have the actual path
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

}
