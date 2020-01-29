package ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ppw1.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;
import model.UGotThis;

public class UGotThisRecyclerAdapter extends RecyclerView.Adapter<UGotThisRecyclerAdapter.ViewHolder>{

    private StorageReference storageReference;
    private Context context;
    private List<UGotThis> uGotThisList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("UGotThis");

    private ActivityListener IactivityListener;

    public interface ActivityListener{
        void onActivityComplete(int ActivityPosition);
    }

    public UGotThisRecyclerAdapter(Context context, List<UGotThis> uGotThisList, ActivityListener activityListener) {

        IactivityListener = activityListener;
        this.context = context;
        this.uGotThisList = uGotThisList;
    }



    @NonNull
    @Override
    public UGotThisRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_task, parent, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        final ViewHolder holder = new ViewHolder(view, context);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IactivityListener.onActivityComplete(holder.getAdapterPosition());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UGotThisRecyclerAdapter.ViewHolder holder, int position) {

        UGotThis reflection = uGotThisList.get(position);
        String imageUrl;

        holder.title.setText(reflection.getTitle());
        holder.status.setText(reflection.getStatus());
        holder.reflections.setText(reflection.getDiscription());
        //holder.name.setText(reflection.getUserName());
        imageUrl = reflection.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(reflection.getTimeAdded().getSeconds() *1000);
        holder.dateAdded.setText(timeAgo);

        //use picasso library to download and show image
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.reflection_tree)
                .fit()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return uGotThisList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;

        public TextView title, reflections, dateAdded, status;
        public ImageView image;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            status = itemView.findViewById(R.id.Status);
            title = itemView.findViewById(R.id.reflection_title_list);
            reflections = itemView.findViewById(R.id.reflection_description_list);
            dateAdded = itemView.findViewById((R.id.reflection_time_stamp));
            image = itemView.findViewById(R.id.reflection_image_list);
            cardView = itemView.findViewById(R.id.cardView);


        }
    }

}
