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
import com.example.blog.PostDetailActivity;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private String postId, email, title, uri, userPhotoUri, detail, username;

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
        this.uri = p.getImageUrl();
        this.detail = p.getIntro();
        this.postId = p.getPostKey();
        this.email = p.getmEmail();
        this.title = p.getName();
        holder.postId = p.getPostKey();
        holder.userEmail = p.getmEmail();
        holder.uri = p.getImageUrl();
        holder.title = p.getName();


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");
        Query query = ref.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()) {
                    if (user.child("photo").exists()) {
                        userPhotoUri = user.child("photo").getValue(String.class);
                        Picasso.get().load(userPhotoUri).into(holder.userPhoto);
                    }
                    username = user.child("username").getValue(String.class);
                    holder.name.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        like(holder.postId, holder.liked);
        Picasso.get().load(uri).into(holder.image);
        //holder.details.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        holder.details.setText(detail);

        holder.liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refPost = database.getReference().child("Posts").child(holder.postId).child("likes");
                refUser = database.getReference().child("Users").child(user.getUid());
                if(holder.liked.getTag().equals("like")) {
                    refPost.child(user.getUid()).setValue(user.getEmail());
                    refUser.child("likes").child(holder.postId).setValue(title);
                }
                else{
                    refPost.child(user.getUid()).removeValue();
                    refUser.child("likes").child(holder.postId).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postid", holder.postId);
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
                if(user != null && dataSnapshot.child(user.getUid()).exists()) {
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
        public TextView details;
        public ImageView image, userPhoto;
        public ImageView liked;
        public ImageView comment;
        public String postId, userEmail, title, uri;

        public PostViewHolder(View view){
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("username", name.getText().toString());
                    intent.putExtra("postId", postId);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("title", title);
                    intent.putExtra("uri", uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            userPhoto = view.findViewById(R.id.photo);
            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.imagePost);
            liked = view.findViewById(R.id.like);
            comment = view.findViewById(R.id.comment);
            details = view.findViewById(R.id.details);
        }
    }

    private void showMessage(String s) {
        Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
