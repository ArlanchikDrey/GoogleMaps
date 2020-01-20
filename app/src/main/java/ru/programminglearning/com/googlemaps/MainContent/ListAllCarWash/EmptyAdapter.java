package ru.programminglearning.com.googlemaps.MainContent.ListAllCarWash;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.programminglearning.com.googlemaps.R;

/**
 * Данный адаптер вызывается тогда, когда нет данных для нормального адаптера*/
public class EmptyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private String setText;

    public EmptyAdapter(String setText) {
        this.setText = setText;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.empty_adapter, parent, false);
        TextView textView = view.findViewById(R.id.textEmptyAdapter);
        textView.setText(setText);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}