package uqac.dim.pixelpursuit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private final Context context;
    private final List<Bitmap> imageList;
    private final ImageView centralImageView;

    public ImageAdapter(Context context, List<Bitmap> imageList, ImageView centralImageView) {
        this.context = context;
        this.imageList = imageList;
        this.centralImageView = centralImageView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bitmap image = imageList.get(position);
        if (image != null) {
            holder.imageView.setImageBitmap(image);
        }

        holder.imageView.setOnClickListener(v -> {
            Drawable drawable = new BitmapDrawable(context.getResources(), image);
            centralImageView.setImageDrawable(drawable);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}