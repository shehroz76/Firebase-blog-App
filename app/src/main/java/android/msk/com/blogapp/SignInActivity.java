package android.msk.com.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    private EditText mUserEmail , mUserPassword;
    private Button mSignInButton,mSignUpButon;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase2;

    private ProgressDialog mprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase2.keepSynced(true);

        mprogress =new ProgressDialog(this);

        mUserEmail = (EditText) findViewById(R.id.Login_User_Email);
        mUserPassword = (EditText) findViewById(R.id.Login_User_password);

        mSignInButton = (Button) findViewById(R.id.login_singin_button);
        mSignUpButon = (Button) findViewById(R.id.login_singup_button);

        mSignUpButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent RegisterIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(RegisterIntent);

            }
        });



        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkingSignIn();

            }
        });

    }

    private void checkingSignIn() {

        String email = mUserEmail.getText().toString().trim();
        String password = mUserPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mprogress.setMessage("Signing In");
            mprogress.show();


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            if(task.isSuccessful()){

                                mprogress.dismiss();

                                CheckUserExitIndb();

                            }

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {

                                mprogress.dismiss();

                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(SignInActivity.this, "signIn failes",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });



        }

    }

    private void CheckUserExitIndb() {

        final String user_id = mAuth.getCurrentUser().getUid();



        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(user_id)){


                    Intent mainActvityIntent = new Intent(SignInActivity.this , MainActivity.class);
                    mainActvityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainActvityIntent);

                }else{
                    Toast.makeText(SignInActivity.this, "You need to first create your account",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
