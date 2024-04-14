package com.example.webstoresoftwarepatternsca.Model;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CommentsRepository {
    private final DatabaseReference commentsRef;

    public CommentsRepository(String productId) {
        commentsRef = FirebaseDatabase.getInstance("https://softwarepatternsca-f1030-default-rtdb.europe-west1.firebasedatabase.app").getReference("comments").child(productId);
    }

    public Task<Void> addComment(Comment comment) {
        String commentId = commentsRef.push().getKey();
        comment.setId(commentId);
        return commentsRef.child(commentId).setValue(comment);
    }

    public DatabaseReference getCommentsRef() {
        return commentsRef;
    }

    public void getComments(DataStatus dataStatus) {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                dataStatus.DataIsLoaded(comments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.DataLoadFailed(databaseError);
            }
        });
    }

    public void calculateAverageRating(final DataStatus dataStatus) {
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<>();
                float totalRating = 0;
                int count = 0;

                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null && comment.getRating() > 0.0) {
                        totalRating += comment.getRating();
                        comments.add(comment);
                        count++;
                    }
                }

                float averageRating = (count > 0) ? totalRating / count : 0;
                dataStatus.AverageRatingLoaded(averageRating);
                dataStatus.DataIsLoaded(comments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dataStatus.DataLoadFailed(databaseError);
            }
        });
    }

    public interface DataStatus {
        void DataIsLoaded(List<Comment> comments);
        void DataLoadFailed(DatabaseError databaseError);
        void AverageRatingLoaded(float averageRating);
    }

}