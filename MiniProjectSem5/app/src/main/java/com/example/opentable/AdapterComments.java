package com.example.opentable;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.ViewHolder>{
    List<ModelComment> commentList;
    Context context;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String currentUserId;
    DocumentReference mDatabase;

    public AdapterComments(List<ModelComment> list, Context context, String currentUserId)
    {
        this.commentList = list;
        this.context = context;
        this.currentUserId = currentUserId;

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


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

        // converting the time of the comment in 'timestamp' format to required format
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm dd/MM/yy");
        sfd.format(commentList.get(p).getTimeStamp().toDate());

        holder.postTime.setText(sfd.format(commentList.get(p).getTimeStamp().toDate()));

        holder.reportComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert(p);
            }
        });
        ToggleLove(holder, p);

        holder.fav_filled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);

                commentList.get(p).setLiked(!commentList.get(p).getLiked());
                commentList.get(p).updateLikes(-1);
                notifyDataSetChanged();

                // updating likes count in the server
                mDatabase = FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(commentList.get(p).getPostId())
                        .collection("comments")
                        .document(commentList.get(p).getProfileUrl().trim());
                mDatabase.update("likesCount", commentList.get(p).getLikesCount());


                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(commentList.get(p).getPostId().trim())
                        .collection("comments")
                        .document(commentList.get(p).getProfileUrl().trim());

                // Atomically remove current user form the likedUsers arraylist
                documentReference.update("likedUsers", FieldValue.arrayRemove(commentList.get(p).getProfileUrl()));


            }
        });

        holder.fav_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToggleLove(holder, p);

                // adding current user id to the existing list of liked users if it dosen't exists
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(commentList.get(p).getPostId().trim())
                        .collection("comments")
                        .document(commentList.get(p).getProfileUrl().trim());

                // Atomically add a new region to the likedUsers arraylist
                documentReference.update("likedUsers", FieldValue.arrayUnion(commentList.get(p).getProfileUrl()));


                commentList.get(p).setLiked(!commentList.get(p).getLiked());
                commentList.get(p).updateLikes(1);
                notifyDataSetChanged();

                // updating likes count in the server
                mDatabase = FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(commentList.get(p).getPostId())
                        .collection("comments")
                        .document(commentList.get(p).getProfileUrl().trim());

                mDatabase.update("likesCount", commentList.get(p).getLikesCount());
            }
        });

        //
        // setting user image of each comment
        storageReference.child("OpenTable/Images/ProfilePicture/"+commentList.get(p).getProfileUrl()+"/profile.jpg")
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageURL = uri.toString();
                        Glide.with(context).load(imageURL).into(holder.user_img); // display image using url
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
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
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("posts").document(commentList.get(p).getPostId().trim())
                        .collection("comments")
                        .document(commentList.get(p).getProfileUrl().trim());

                // Atomically add a new region to the likedUsers arraylist
                documentReference.update("reportedUsers", FieldValue.arrayUnion(commentList.get(p).getProfileUrl()));
                documentReference.update("reportCount", commentList.get(p).getReportCount()+1);
                alert.dismiss();
            }
        });
    }


    void ToggleLove(AdapterComments.ViewHolder holder, int position)
    {


//        Toast.makeText(context, (String)likedUsers.get(currentUserId), Toast.LENGTH_SHORT).show();
//        Toast.makeText(context, Boolean.toString(likedUsers.containsValue(currentUserId)), Toast.LENGTH_SHORT).show();


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
