package com.example.blog.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.Model.UserDetail;
import com.example.blog.OthersProfilePageActivity;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder> {

    Context context;
    List<UserDetail> list;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    public DatabaseReference refPost, refUser;

    public SearchUserAdapter(Context context, List<UserDetail> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result, parent, false);

        SearchUserAdapter.SearchUserViewHolder viewHolder = new SearchUserAdapter.SearchUserViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserViewHolder holder, int i) {

        UserDetail user = list.get(i);
        String username = user.getUsername();
        String email = user.getEmail();
        holder.uid = user.getId();

        Uri uri = user.getPhoto();

        holder.username.setText(username);
        holder.email.setText(email);
        Picasso.get().load(uri).into(holder.userPhoto);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SearchUserViewHolder extends RecyclerView.ViewHolder {

        private ImageView userPhoto;
        private TextView username;
        private TextView email;
        private LinearLayout linear1, linear2;
        private String uid;

        public SearchUserViewHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OthersProfilePageActivity.class);
                    intent.putExtra("user", username.getText().toString());
                    intent.putExtra("email", email.getText().toString());
                    intent.putExtra("uid", uid);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            linear1 = itemView.findViewById(R.id.linear1);
            linear2 = linear1.findViewById(R.id.linear2);
            userPhoto = linear1.findViewById(R.id.user_photo);
            username = linear2.findViewById(R.id.user_name);
            email = linear2.findViewById(R.id.user_email);
        }
    }

    private void showMessage(String s) {
        Toast.makeText(context.getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
