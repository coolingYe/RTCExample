package com.zeewain.api.example.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zeewain.api.example.R;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {

    private RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_layout, parent, false);
        view.getLayoutParams().height = mRecyclerView.getWidth() / 3;
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomHolder holder, int position) {
        if (position == 0) {

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    static class RoomHolder extends RecyclerView.ViewHolder {

        private TextView displayName;
        private org.webrtc.SurfaceViewRenderer player;

        public RoomHolder(@NonNull View itemView) {
            super(itemView);
            displayName = itemView.findViewById(R.id.edit_display_name);
            player = itemView.findViewById(R.id.player);
        }
    }
}
