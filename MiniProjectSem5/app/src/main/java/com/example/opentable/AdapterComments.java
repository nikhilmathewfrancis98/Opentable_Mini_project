package com.example.opentable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.ViewHolder>{
    List<ModelComment> commentList;
    Context context;

    public AdapterComments(List<ModelComment> list, Context context)
    {
        this.commentList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int p = holder.getAbsoluteAdapterPosition();
        holder.comment.setText(commentList.get(p).getComment());
        holder.userName.setText(commentList.get(p).getUserName());
        holder.likes.setText(Integer.toString(commentList.get(p).getLikesCount()));


        ToggleLove(holder, p);

        holder.fav_filled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);
                commentList.get(p).setLiked(!commentList.get(p).getLiked());
                commentList.get(p).updateLikes(-1);
                notifyDataSetChanged();
            }
        });

        holder.fav_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);
                commentList.get(p).setLiked(!commentList.get(p).getLiked());
                commentList.get(p).updateLikes(1);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView user_img;
        TextView userName;
        TextView postTime;
        TextView comment;
        ImageView fav_outline;
        ImageView fav_filled;
        TextView likes;
        ImageView reportComment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_img = itemView.findViewById(R.id.user_img);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            comment = itemView.findViewById(R.id.comment);
            fav_outline = itemView.findViewById(R.id.img_heart);
            fav_filled = itemView.findViewById(R.id.img_heart_red);
            likes = itemView.findViewById(R.id.countLikes);
            reportComment = itemView.findViewById(R.id.reportComment);

        }
    }


    void ToggleLove(AdapterComments.ViewHolder holder, int position)
    {
        if(commentList.get(position).getLiked())
        {
            holder.fav_filled.setVisibility(View.VISIBLE);
            holder.fav_outline.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.fav_filled.setVisibility(View.INVISIBLE);
            holder.fav_outline.setVisibility(View.VISIBLE);
        }
    }

}
