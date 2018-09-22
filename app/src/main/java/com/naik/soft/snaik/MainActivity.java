package com.naik.soft.snaik;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naik.soft.snaik.model.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String DATA_BASE_NAME = "userdata";
    private DatabaseReference mDatabase;
    int userID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        addDataChangeListener();

        findViewById(R.id.addUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                writeNewUser(String.valueOf(userID), ((EditText)findViewById(R.id.name)).getText().toString()
                        , ((EditText)findViewById(R.id.email)).getText().toString()
                        , ((EditText)findViewById(R.id.phone)).getText().toString());
            }
        });



        findViewById(R.id.removeUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeUser(String.valueOf(userID));
            }
        });

    }

    private void removeUser(String userId) {

        mDatabase.child(DATA_BASE_NAME).child(userId).setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Data Remove Success");
                        displayMessage("Data Remove Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Data Remove Failure");
                        displayMessage("Data Remove Failure");
                    }
                });

    }

    private void addDataChangeListener() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(DATA_BASE_NAME);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("onDataChange");
                StringBuilder userData = new StringBuilder();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);
                    System.out.println("The read : "+ dataSnapshot.getKey()+":" + user.email +":id:"+user.id);
                    userData.append("User Id: ");
                    userData.append(String.valueOf(user.id));
                    userData.append(" User name: ");
                    userData.append(String.valueOf(user.username));
                    userData.append(" User email: ");
                    userData.append(String.valueOf(user.email));
                    userData.append(" Phone: ");
                    userData.append(String.valueOf(user.phone));
                    userData.append("\n");
                    userID = user.id + 1 ;
                }
                ((TextView)findViewById(R.id.textView)).setText(userData.toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void writeNewUser(final String userId, final String name, String email, String phone) {
        final User user = new User(name, email, phone, userID);
        mDatabase.child(DATA_BASE_NAME).child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Write Success");
                        displayMessage("Write Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Write Failure");
                        displayMessage("Write Failure");
                    }
                });
    }


    private void displayMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

}
