package com.example.blog.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.blog.Model.Comment;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    Context context;
    List<Comment> list;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    public DatabaseReference refPost, refUser;

    public CommentAdapter(Context context, List<Comment> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment, parent, false);

        CommentAdapter.CommentViewHolder viewHolder = new CommentAdapter.CommentViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int index) {
        Comment c = list.get(index);
        final String publisher = c.getPublisher();
        final String commentDetail = c.getComment();
        final Date date = c.getDate();
        String time = ""+ (date.getYear()+1900)+"-"+(date.getMonth()+1)+"-"+date.getDate();
        holder.user.setText(publisher);
        holder.comment.setText(commentDetail);
        holder.date.setText(time);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        public TextView user;
        public TextView comment;
        public TextView date;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            user = itemView.findViewById(R.id.user);
            comment = itemView.findViewById(R.id.comment_detail);
            date = itemView.findViewById(R.id.commentDate);
        }
    }
}
