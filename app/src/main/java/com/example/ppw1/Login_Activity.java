package com.example.ppw1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import utils.UGotThisApi;

public class Login_Activity extends AppCompatActivity {

    private EditText password;
    private EditText email;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        TextView register = findViewById(R.id.register);
        Button login =findViewById(R.id.login);

        //navigate to the task list page
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmailPasswordUser(email.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        //navigate to the register page
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new  Intent(getApplicationContext(),Register_Activity.class);
                startActivity(register);
            }
        });


    }

    private void loginEmailPasswordUser(String email, String pwd) {
//        progressBar.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)){

            firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    String currentUserId = user.getUid();

                    collectionReference.whereEqualTo("userId", currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {

                            }

                            assert queryDocumentSnapshots != null;
                            if (!queryDocumentSnapshots.isEmpty()) {

//                                progressBar.setVisibility(View.INVISIBLE);

                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    UGotThisApi uGotThisApi = UGotThisApi.getInstance();
                                    uGotThisApi.setUsername(snapshot.getString("username"));
                                    uGotThisApi.setUserId(snapshot.getString("userId"));

                                    startActivity(new Intent(Login_Activity.this, Task_List_Activity.class));
                                    finish();

                                }
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        }else{
//            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(Login_Activity.this, "Please enter an email and password", Toast.LENGTH_LONG).show();
        }

    }

}
