package com.example.opentable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class HomePostAdapter extends RecyclerView.Adapter<HomePostAdapter.PostViewHolder>
        implements Filterable {

    List<ModalPost> postsList;
    List<ModalPost> exampleList;
    DocumentReference mDatabase;
    FirebaseFirestore firestore;


    ArrayList<String> locations;

    Context context;
    // constructor
    public HomePostAdapter(List<ModalPost> postList, Context context)
    {
        this.firestore = FirebaseFirestore.getInstance();
        locations = new ArrayList<>();
        this.postsList = postList;
        for(ModalPost p : postList) {
            this.locations.add(p.getLocation());
        }

        this.exampleList = postList;
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
        holder.title.setText(exampleList.get(p).getTitle());
        holder.location.setText(exampleList.get(p).getLocation());
        holder.txt_likes.setText(Integer.toString(exampleList.get(p).getTxt_likes()));
        holder.txt_caption.setText(exampleList.get(p).getTxt_caption());
        holder.txt_tags.setText(exampleList.get(p).getTxt_tags());
        holder.usrName.setText("Posted by :"+exampleList.get(p).getUserName());

        // to display badge for a post
        if(exampleList.get(p).getTxt_likes() > 20 && exampleList.get(p).getReportCount()<5)
        {
            holder.badge.setVisibility(View.VISIBLE);
        }
        else
            holder.badge.setVisibility(View.INVISIBLE);

//        RequestOptions options =
//                new RequestOptions()
//                        .centerCrop()
//                        .placeholder(R.drawable.bookmark_icon)
//                        .error(R.drawable.add_btn);

//----------------------------- LOADING IMAGES OF POSTS FROM FIREBASE -----------------------------------

        // getting url of the required image and upon success, display the image in the image view using the received url
        exampleList.get(p).getPost_image().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageURL = uri.toString();
                Glide.with(context).load(imageURL).into(holder.post_image); // display image using url
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });

//------------------------------------------- ENDS HERE -------------------------------------------------

        ToggleLove(holder, p);

        holder.favorite_filled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);
                exampleList.get(p).setLiked(!exampleList.get(p).getLiked());
                mDatabase = firestore.collection("posts").document(exampleList.get(p).getPostId());
                mDatabase.update("likesCount", exampleList.get(p).getTxt_likes()-1);
                exampleList.get(p).updateLikes(-1);


                // adding current user id to the existing list of liked users if it dosen't exists
                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(exampleList.get(p).getPostId().trim());

                // Atomically remove from likedUsers arraylist
                documentReference.update("likedUsers", FieldValue.arrayRemove(exampleList.get(p).getUserId()));

                notifyDataSetChanged();
            }
        });

        holder.favorite_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);
                exampleList.get(p).setLiked(!exampleList.get(p).getLiked());


                // adding current user id to the existing list of liked users if it dosen't exists
                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(exampleList.get(p).getPostId().trim());

                // Atomically add a new region to the likedUsers arraylist
                documentReference.update("likedUsers", FieldValue.arrayUnion(exampleList.get(p).getUserId()));

                mDatabase = firestore.collection("posts").document(exampleList.get(p).getPostId());
                mDatabase.update("likesCount", exampleList.get(p).getTxt_likes()+1);

                exampleList.get(p).updateLikes(1);
                notifyDataSetChanged();
            }
        });

        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(p);
            }
        });

        holder.img_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", exampleList.get(p).getPostId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder  {
        TextView title, usrName;
        TextView location;
        ImageView post_image;
        ImageView favorite_outline;
        ImageView favorite_filled;
        ImageView img_comments;
        ImageView img_bookmark;
        ImageView option;
        TextView txt_likes;
        TextView txt_caption;
        TextView txt_tags;
        ImageView badge;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
            location=itemView.findViewById(R.id.location);
            post_image=itemView.findViewById(R.id.post_image);
            favorite_outline=itemView.findViewById(R.id.img_heart);
            favorite_filled=itemView.findViewById(R.id.img_heart_red);
            img_comments=itemView.findViewById(R.id.img_comments);
//            img_bookmark=itemView.findViewById(R.id.img_bookmark);
            badge=itemView.findViewById(R.id.badge);
            option=itemView.findViewById(R.id.option);
            txt_likes=itemView.findViewById(R.id.txt_likes);
            txt_caption=itemView.findViewById(R.id.txt_caption);
            txt_tags=itemView.findViewById(R.id.txt_tags);
            usrName = itemView.findViewById(R.id.user_name);
        }
    }

    void ToggleLove(HomePostAdapter.PostViewHolder holder, int position)
    {
        if(exampleList.get(position).getLiked())
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
    public void showAlert(int p)
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

                // adding current user id to the existing list of liked users if it dosen't exists
                DocumentReference documentReference = FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(exampleList.get(p).getPostId().trim());

                // Atomically add a new region to the likedUsers arraylist
                documentReference.update("reportedUsers", FieldValue.arrayUnion(exampleList.get(p).getUserId()));
                documentReference.update("reportCount", exampleList.get(p).getReportCount()+1);

                alert.dismiss();
            }
        });
    }

    // filter the list of posts based on query (handle searching)
    private final Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ModalPost> filteredList = new ArrayList<>();
            String filterPattern = "";

//            Toast.makeText(context, Integer.toString(postsList.size()), Toast.LENGTH_SHORT).show();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(HomeActivity.postList);

            }
            else {
                filterPattern = constraint.toString().toLowerCase().trim();

                for (ModalPost item : HomeActivity.postList) {
                    if (item.getTxt_tags().toLowerCase().contains(filterPattern)
                            || item.getLocation().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

//            Log.d("hiui", Integer.toString(postsList.size()));
//            Log.d("hiuii", filterPattern);
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//            exampleList = new ArrayList<>();
            exampleList.clear();
            // we are creating new songs list with the help of filtered results
            exampleList.addAll((List) filterResults.values);
            notifyDataSetChanged(); // notify the changes

//            Toast.makeText(context, Integer.toString(postsList.size()), Toast.LENGTH_SHORT).show();
//            Toast.makeText(context, Integer.toString(exampleList.size()), Toast.LENGTH_SHORT).show();


        }
    };

}
