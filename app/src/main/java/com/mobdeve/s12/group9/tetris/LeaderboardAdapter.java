package com.mobdeve.s12.group9.tetris;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardHolder> {
    private ArrayList<LeaderboardEntry> data;

    public LeaderboardAdapter (ArrayList<LeaderboardEntry> data) { this.data = data; }

    @NonNull
    @Override
    public LeaderboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(viewType, parent, false);

        LeaderboardHolder holder = new LeaderboardHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
