package com.example.androidphotos87;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import model.Album;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private List<Album> albumList;
    private OnItemClickListener onItemClickListener;
    public static final int MENU_RENAME = 1;
    public static final int MENU_DELETE = 2;
    private static int currentPosition; // Store the current position

    public AlbumAdapter(List<Album> albumList, OnItemClickListener onItemClickListener) {
        this.albumList = albumList;
        this.onItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(Album album);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.bind(album);
    }
    @Override
    public int getItemCount() {
        return albumList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private TextView albumNameTextView;
        private Button albumOptionsButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameTextView = itemView.findViewById(R.id.albumNameTextView);
            albumOptionsButton = itemView.findViewById(R.id.albumOptionsButton);
            albumOptionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = getAdapterPosition();
                    showContextMenu();
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        currentPosition = position; // Store the current position
                        onItemClickListener.onItemClick(albumList.get(position));
                    }
                }
            });
            itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = getAdapterPosition();

            // Set up the context menu items
            menu.setHeaderTitle("Album Options");
            menu.add(Menu.NONE, MENU_RENAME, Menu.NONE, "Rename");
            menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        }
        private void showContextMenu() {
            // Show the context menu
            itemView.showContextMenu();
        }
        public void bind(Album album) {
            albumNameTextView.setText(album.getAlbumName());
        }
    }
    public static int getCurrentPosition(){
        return currentPosition;
    }
}