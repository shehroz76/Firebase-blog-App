package android.msk.com.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitButton;
    private Uri mImageUri;
    private ImageButton mSelectImage;
    private static final int Gallery_Request = 1;

    private ProgressDialog mprogress;

    private StorageReference mStoarge;
    private DatabaseReference mData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mStoarge = FirebaseStorage.getInstance().getReference();
        mData = FirebaseDatabase.getInstance().getReference().child("posts");

        mPostTitle = (EditText) findViewById(R.id.Edit_Title_field) ;
        mPostDesc = (EditText) findViewById(R.id.Edit_description_field);

        mSubmitButton = (Button) findViewById(R.id.Submit_button);

        mprogress = new ProgressDialog(this);

        mSelectImage = (ImageButton) findViewById(R.id.select_image_Button);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , Gallery_Request);

            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();

            }
        });

    }

    private void startPosting() {

        mprogress.setMessage("Posting to blog..");
        mprogress.show();

        final String title_value =  mPostTitle.getText().toString().trim();
        final String desc_value = mPostDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(title_value) && !TextUtils.isEmpty(desc_value) && mImageUri == null){

            StorageReference filepath = mStoarge.child("Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost =mData.push();

                    newPost.child("title").setValue(title_value);
                    newPost.child("Desc").setValue(desc_value);
                    newPost.child("images").setValue(downloadUrl.toString());

                    mprogress.dismiss();

                    startActivity(new Intent(PostActivity.this , MainActivity.class));

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Request && requestCode == RESULT_OK) {
            mImageUri = data.getData();
            mSelectImage.setImageURI(mImageUri);
        }

    }
}
