package com.example.opentable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.zip.Inflater;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.PostViewHolder>{

    List<ModalPost> postsList;
    Context context;
    // constructor
    public HomePostAdapter(List<ModalPost> postList, Context context)
    {
        this.postsList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomePostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePostAdapter.PostViewHolder holder, int position) {

        int p = holder.getAbsoluteAdapterPosition();
        holder.title.setText(postsList.get(p).getTitle());
        holder.location.setText(postsList.get(p).getLocation());
        holder.txt_likes.setText(Integer.toString(postsList.get(p).getTxt_likes()));
        holder.txt_caption.setText(postsList.get(p).getTxt_caption());
        holder.txt_tags.setText(postsList.get(p).getTxt_tags());
        Bitmap bitmap = BitmapFactory.decodeFile(postsList.get(p).getPost_image());

        ToggleLove(holder, p);

        holder.favorite_filled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);
                postsList.get(p).setFavorite(!postsList.get(p).getFavorite());
                postsList.get(p).updateLikes(-1);
                notifyDataSetChanged();
            }
        });

        holder.favorite_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);
                postsList.get(p).setFavorite(!postsList.get(p).getFavorite());
                postsList.get(p).updateLikes(1);
                notifyDataSetChanged();
            }
        });

        holder.post_image.setImageBitmap(bitmap);
        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder  {
        TextView title;
        TextView location;
        ImageView post_image;
        ImageView favorite_outline;
        ImageView favorite_filled;
        ImageView img_comments;
        ImageView img_bookmark;
        ImageView img_save;
        ImageView option;
        TextView txt_likes;
        TextView txt_caption;
        TextView txt_tags;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            location=itemView.findViewById(R.id.location);
            post_image=itemView.findViewById(R.id.post_image);
            favorite_outline=itemView.findViewById(R.id.img_heart);
            favorite_filled=itemView.findViewById(R.id.img_heart_red);
            img_comments=itemView.findViewById(R.id.img_comments);
            img_bookmark=itemView.findViewById(R.id.img_bookmark);
            img_save=itemView.findViewById(R.id.img_save);
            option=itemView.findViewById(R.id.option);
            txt_likes=itemView.findViewById(R.id.txt_likes);
            txt_caption=itemView.findViewById(R.id.txt_caption);
            txt_tags=itemView.findViewById(R.id.txt_tags);
        }
    }

    void ToggleLove(HomePostAdapter.PostViewHolder holder, int position)
    {
        if(postsList.get(position).getFavorite())
        {
            holder.favorite_filled.setVisibility(View.VISIBLE);
            holder.favorite_outline.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.favorite_filled.setVisibility(View.INVISIBLE);
            holder.favorite_outline.setVisibility(View.VISIBLE);
        }
    }



    // show alert for creation of new list
    public void showAlert()
    {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        View view = li.inflate(R.layout.report_alert_box, null);
        LinearLayout contentLayout = view.findViewById(R.id.content);
        contentLayout.setVisibility(View.GONE);
        Button reportBtn = view.findViewById(R.id.btnSubmit);
        TextView reportThis = view.findViewById(R.id.reportPost);
        reportThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentLayout.setVisibility(View.VISIBLE);
                reportThis.setVisibility(View.GONE);
            }
        });

        ab.setView(view);
        AlertDialog alert = ab.create();

        alert.show();// show the dialog box

        // upon clicking create button
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
    }

}
