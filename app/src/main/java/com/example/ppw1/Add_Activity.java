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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import model.UGotThis;
import utils.UGotThisApi;

import static android.content.ContentValues.TAG;
import static model.UGotThis.timeAdded;

public class Add_Activity extends AppCompatActivity {

    private static final int GALLERY_CODE = 1;
    private static final String TAG = "Add_Activity";
    private EditText title;
    private EditText discription;
    private ImageView postCameraButton;
    private String currentUserId;
    private String currentUserName;
    private Button done;
    static FirebaseStorage storage = FirebaseStorage.getInstance();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // connection to firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("UGotThis");
    private static Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        postCameraButton = findViewById(R.id.postCameraButton);
        title = findViewById(R.id.title);
        discription = findViewById(R.id.discription);
        done = findViewById(R.id.btn_done);



//        final EditText description = (EditText) findViewById(R.id.discription);

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

    private void saveJournal() {

        final String Title = title.getText().toString().trim();
        final String Discription = discription.getText().toString().trim();


        if (!TextUtils.isEmpty(Title) && !TextUtils.isEmpty(Discription) && imageUri != null) {
            //saving image
            final StorageReference filepath = storageReference.child("ugotthis_images").child("my_image_" + Timestamp.now().getSeconds());
            //making the image filenames unique with timestamp

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageUrl = uri.toString();

                            // create journal object, invoke collectionRef, save journal instance

                            UGotThis uGotThis = new UGotThis();
                            uGotThis.setTitle(Title);
                            uGotThis.setDiscription(Discription);
                            uGotThis.setImageUrl(imageUrl);
                            uGotThis.setTimeAdded(new Timestamp(new Date()));
                            uGotThis.setUserName(currentUserName);
                            uGotThis.setUserId(currentUserId);

                            collectionReference.add(uGotThis).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {


                                    startActivity(new Intent(Add_Activity.this, Task_List_Activity.class));
                                    finish();
                            }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } else {

        }
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

    public static void onDeleteStorage() {

        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child("ugotthis_images/" );

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.w(TAG, "Error deleting document");
            }
        });
    }
}
