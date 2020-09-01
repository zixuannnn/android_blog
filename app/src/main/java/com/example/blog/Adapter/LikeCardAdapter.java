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

import com.example.blog.PostDetailActivity;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class LikeCardAdapter extends RecyclerView.Adapter<LikeCardAdapter.LikeCardViewHolder> {

    private FirebaseDatabase database;
    private List<Map<String, String>> list;
    private Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();

    public LikeCardAdapter(Context context, List<Map<String, String>> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public LikeCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_detail, parent, false);

        LikeCardAdapter.LikeCardViewHolder viewHolder = new LikeCardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikeCardViewHolder holder, int i) {
        Map<String, String> map = list.get(i);
        String phoroUrl = map.get("photo");
        String postUrl = map.get("post");
        String date = map.get("date");
        String username = map.get("username");
        String postId = map.get("postId");
        String author = map.get("authorUsername");
        String title = map.get("title");
        String email = map.get("email");

        if(phoroUrl != null)
            Picasso.get().load(phoroUrl).into(holder.photo);
        Picasso.get().load(postUrl).into(holder.post);
        holder.date.setText(date);
        holder.username.setText(username);
        holder.postId = postId;
        holder.author = author;
        holder.uri = postUrl;
        holder.title = title;
        holder.email = email;

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class LikeCardViewHolder extends RecyclerView.ViewHolder{
        public ImageView photo, post;
        public TextView date, username;
        public String postId, author, title, uri, email;

        public LikeCardViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("username", author);
                    intent.putExtra("postId", postId);
                    intent.putExtra("email", email);
                    intent.putExtra("title", title);
                    intent.putExtra("uri", uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            photo = itemView.findViewById(R.id.photo);
            post = itemView.findViewById(R.id.post);
            date = itemView.findViewById(R.id.date);
            username = itemView.findViewById(R.id.username);

        }
    }
}
