package com.example.opentable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdapterPostsCollections  extends RecyclerView.Adapter<AdapterPostsCollections.PostViewHolder>{
    List<ModalPost> postsList;
    List<ModalPost> exampleList;
    DocumentReference mDatabase;
    FirebaseFirestore firestore;

    Context context;
    // constructor
    public AdapterPostsCollections(List<ModalPost> postList, Context context)
    {
        this.firestore = FirebaseFirestore.getInstance();
        this.postsList = new ArrayList<>(postList);
        this.exampleList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPostsCollections.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new AdapterPostsCollections.PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPostsCollections.PostViewHolder holder, int position) {

        int p = holder.getAbsoluteAdapterPosition();
        holder.title.setText(exampleList.get(p).getTitle());
//        holder.txt_likes.setText(Integer.toString(exampleList.get(p).getTxt_likes()));
        holder.txt_caption.setText(exampleList.get(p).getTxt_caption());
//        holder.txt_tags.setText(exampleList.get(p).getTxt_tags());

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
//                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });

//------------------------------------------- ENDS HERE -------------------------------------------------


    }

    @Override
    public int getItemCount() {
        return exampleList.size();
    }

//    @Override
//    public Filter getFilter() {
//        return exampleFilter;
//    }

    public class PostViewHolder extends RecyclerView.ViewHolder  {
        TextView title;
        ImageView post_image;
        TextView txt_caption;
//        ImageView option;
//        TextView txt_likes;
//        TextView txt_tags;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.postUserId);
            post_image=itemView.findViewById(R.id.postThumb);
            txt_caption=itemView.findViewById(R.id.description);

//            option=itemView.findViewById(R.id.option);
//            txt_likes=itemView.findViewById(R.id.txt_likes);
//            txt_tags=itemView.findViewById(R.id.txt_tags);
        }
    }
}
