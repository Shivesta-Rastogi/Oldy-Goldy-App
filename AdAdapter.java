package com.example.myfinalloginpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myfinalloginpage.R;
import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.AdViewHolder> {
    private Context context;
    private List<AdModel> adList;

    public AdAdapter(Context context, List<AdModel> adList) {
        this.context = context;
        this.adList = adList;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new AdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdViewHolder holder, int position) {
        AdModel ad = adList.get(position);
        holder.title.setText(ad.getTitle());
        holder.price.setText(ad.getPrice());
        holder.location.setText(ad.getLocation());

        // Load image using Glide
        Glide.with(context).load(ad.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, price, location;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ad_image);
            title = itemView.findViewById(R.id.ad_title);
            price = itemView.findViewById(R.id.ad_price);
            location = itemView.findViewById(R.id.ad_location);
        }
    }
}

