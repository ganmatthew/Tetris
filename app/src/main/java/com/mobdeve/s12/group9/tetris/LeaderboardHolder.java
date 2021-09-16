package com.mobdeve.s12.group9.tetris;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;

public class LeaderboardHolder extends RecyclerView.ViewHolder {
    private TextView tvRank;
    private TextView tvUsername;
    private TextView tvScore;
    private TextView tvTimestamp;

    public LeaderboardHolder(@NonNull View itemView) {
        super(itemView);

        this.tvRank = itemView.findViewById(R.id.tv_lead_item_rank);
        this.tvUsername = itemView.findViewById(R.id.tv_lead_item_username);
        this.tvScore = itemView.findViewById(R.id.tv_lead_item_score_value);
        this.tvTimestamp = itemView.findViewById(R.id.tv_lead_item_timestamp);
    }

    public void bindData(LeaderboardEntry entry) {
        this.tvUsername.setText(entry.getUsername());
        this.tvScore.setText(entry.getScore());
        this.tvTimestamp.setText(entry.getTimestamp());
    }


}