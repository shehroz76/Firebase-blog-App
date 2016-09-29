package android.msk.com.blogapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import static android.msk.com.blogapp.R.id.action_add;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");

        mBlogList = (RecyclerView) findViewById(R.id.blog_recylView_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<BlogModel,BlogViewHolder> firebaseRecyclerAdapter =new
                FirebaseRecyclerAdapter<BlogModel, BlogViewHolder>(

                        BlogModel.class,
                        R.layout.blog_row,
                        BlogViewHolder.class,
                        mDatabase
                ) {
                    @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, BlogModel model, int position) {

                        viewHolder.setTitle(model.getTitle());
                        viewHolder.setDesc(model.getDesc());
                        viewHolder.setImage(getApplicationContext(), model.getImages());

            }
        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){

            TextView post_title = (TextView) mView.findViewById(R.id.blog_title_text);
            post_title.setText(title);
        }

        public void setDesc(String Desc){

            TextView post_Desc = (TextView) mView.findViewById(R.id.blog_desc_text);
            post_Desc.setText(Desc);
        }
        public void setImage(Context context,String image){

            ImageView post_image = (ImageView) mView.findViewById(R.id.blog_image_view);
            Picasso.with(context).load(image).into(post_image);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==action_add){

            startActivity(new Intent(MainActivity.this ,PostActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
