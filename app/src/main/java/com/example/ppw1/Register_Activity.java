package com.example.ppw1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import utils.UGotThisApi;


public class Register_Activity extends AppCompatActivity {

    private Button submit;
    private EditText email;
    private EditText password;
    private FirebaseAuth firebaseAuth;
    private EditText username;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = firebaseAuth.getInstance();

        TextView login = findViewById(R.id.login);
        submit = findViewById(R.id.submit);
        password= findViewById(R.id.password);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    //user is already logged in...
                } else
                {
                    //No current user
                }
            }
        };

        // on click navigate to Task list page
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (!TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(password.getText().toString()) && !TextUtils.isEmpty(username.getText().toString())){

                    String Email = email.getText().toString().trim();
                    String Password = password.getText().toString().trim();
                    String Username =username.getText().toString().trim();


                    createUserEmailAccount(Email, Password, Username);

                }
                else {
                    Toast.makeText(Register_Activity.this, "Complete the fields", Toast.LENGTH_LONG);
                }
            }

        });

        // on click navigate to login page
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new  Intent(getApplicationContext(),Login_Activity.class);
                startActivity(login);
            }
        });


    }

    private void createUserEmailAccount(String email, String password, final String username){
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){

//            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        //go to add reflection activity

                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserId = currentUser.getUid();

                        //create a user map to add to a user collection
                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userId", currentUserId);
                        userObj.put("username", username);

                        //save to firestore

                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (Objects.requireNonNull(task.getResult()).exists()) {
//                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name = task.getResult().getString("username");

                                            UGotThisApi campusGeoQuizApi = UGotThisApi.getInstance(); //GLOBAL Api
                                            campusGeoQuizApi.setUserId(currentUserId);
                                            campusGeoQuizApi.setUsername(name);



                                            Intent intent = new Intent(Register_Activity.this, Task_List_Activity.class);
                                            intent.putExtra("username", name);
                                            intent.putExtra("userId", currentUserId);

                                            startActivity(intent);

                                        }else{
//                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }else
                    {
                        Toast.makeText(Register_Activity.this, "something went wrong", Toast.LENGTH_LONG);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
        else
        {

        }

    }
    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }


}



