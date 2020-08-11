package com.example.blog.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.CommentActivity;
import com.example.blog.Model.Post;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostViewHolder> {

    Context context;
    List<Post> list;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    public DatabaseReference refPost, refUser;

    public PostListAdapter(Context context, List<Post> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_card, parent, false);

        PostViewHolder viewHolder = new PostViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int index) {

        Post p = list.get(index);
        final String postId = p.getPostKey();
        final String email = p.getmEmail();
        final String uri = p.getImageUrl();
        final String title = p.getName();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        like(postId, holder.liked);
        holder.name.setText(email.split("@")[0]);
        Picasso.get().load(uri).into(holder.image);

        holder.liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refPost = database.getReference().child("Posts").child(postId).child("likes");
                refUser = database.getReference().child("Users").child(user.getUid());
                if(holder.liked.getTag().equals("like")) {
                    refPost.child(user.getUid()).setValue(user.getEmail());
                    refUser.child("likes").child(postId).setValue(title);
                }
                else{
                    refPost.child(user.getUid()).removeValue();
                    refUser.child("likes").child(postId).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid", postId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


    }

    private void like(String postId, final ImageView liked){
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        refPost = database.getReference().child("Posts").child(postId).child("likes");

        refPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user.getUid()).exists()) {
                    liked.setImageResource(R.drawable.ic_liked);
                    liked.setTag("liked");
                }
                else{
                    liked.setImageResource(R.drawable.ic_like);
                    liked.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public ImageView image;
        public ImageView liked;
        public ImageView comment;

        public PostViewHolder(View view){
            super(view);

            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.imagePost);
            liked = view.findViewById(R.id.like);
            comment = view.findViewById(R.id.comment);
        }
    }

    private void showMessage(String s) {
        Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
