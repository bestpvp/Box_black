package com.github.tvbox.osc.widget;

import android.view.View;

import com.owen.tvrecyclerview.widget.TvRecyclerView;

public interface OnItemClickListener extends TvRecyclerView.OnItemListener {
    @Override
    void onItemClick(TvRecyclerView tvRecyclerView, View view, int i);

    @Override
    default void onItemPreSelected(TvRecyclerView tvRecyclerView, View view, int i) {
    }

    @Override
    default void onItemSelected(TvRecyclerView tvRecyclerView, View view, int i) {
    }

    ;
}
