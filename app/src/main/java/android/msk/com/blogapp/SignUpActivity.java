package android.msk.com.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "tag" ;
    private EditText mName, mEmail , mPassword;
    private ImageButton mProfile_pic;
    private Button mRegisterSignInButton , mRegisterSignUpbutton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase1;
    private StorageReference mStorageUserImageRef;

    private ProgressDialog mProgress;

    private static final int Gallery_Request = 1 ;
    private Uri mImageUri1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mStorageUserImageRef = FirebaseStorage.getInstance().getReference();

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users");


        mProgress = new ProgressDialog(this);

        mName = (EditText) findViewById(R.id.User_Name);
        mEmail = (EditText) findViewById(R.id.User_Email);
        mPassword = (EditText) findViewById(R.id.User_Password);

        mProfile_pic = (ImageButton) findViewById(R.id.user_profile_pic);


        mRegisterSignUpbutton = (Button) findViewById(R.id.register_singup_button);
        mRegisterSignInButton = (Button) findViewById(R.id.register_singin_button);

        mRegisterSignUpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegister();
            }
        });


        mRegisterSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinIntent = new  Intent(SignUpActivity.this , SignInActivity.class);
                signinIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signinIntent);
            }
        });

        mProfile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile_gallerY_intent = new Intent(Intent.ACTION_GET_CONTENT);
                profile_gallerY_intent.setType("image/*");
                startActivityForResult(profile_gallerY_intent , Gallery_Request);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Request && resultCode == RESULT_OK) {

           Uri ImageUri1 = data.getData();

            CropImage.activity(ImageUri1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1 , 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mImageUri1 = result.getUri();

                mProfile_pic.setImageURI(mImageUri1);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    private void startRegister() {

        mProgress.setMessage("Signing Up..");
        mProgress.show();

        final String Name = mName.getText().toString().trim();
        String Email = mEmail.getText().toString().trim();
        String Password = mPassword.getText().toString().trim();

        if(!TextUtils.isEmpty(Name) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password) && mImageUri1 != null){


            mAuth.createUserWithEmailAndPassword(Email, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            if(task.isSuccessful()){

                                StorageReference filepath = mStorageUserImageRef.child("User Images").child(mImageUri1.getLastPathSegment());
                                filepath.putFile(mImageUri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        Uri dowloadProfilePicUrl = taskSnapshot.getDownloadUrl();


                                        String user_id = mAuth.getCurrentUser().getUid();

                                        DatabaseReference current_user_db = mDatabase1.child(user_id);


                                        current_user_db.child("name").setValue(Name);
                                        current_user_db.child("images").setValue(dowloadProfilePicUrl.toString());
                                        mProgress.dismiss();

                                        Intent signinIntent = new Intent(SignUpActivity.this , SignInActivity.class);
                                        signinIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(signinIntent);

                                    }
                                });

                            }



                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "signup failes",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        }


    }
}
