package ru.programminglearning.com.googlemaps.MainContent.ListAllCarWash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import ru.programminglearning.com.googlemaps.MainContent.ActivityContent;
import ru.programminglearning.com.googlemaps.Maps.Maps;
import ru.programminglearning.com.googlemaps.Maps.ViewWindowMarker.Model;
import ru.programminglearning.com.googlemaps.R;
import ru.programminglearning.com.googlemaps.Utils.Utils;

public class ListAllCarWashAdapter
        extends RecyclerView.Adapter<ListAllCarWashAdapter.ViewHolder> {

    private List<Model> modelsList = new ArrayList<>();
    private int lastPosition = -1;

    public ListAllCarWashAdapter() {
    }

    public ListAllCarWashAdapter(List<Model> modelsList) {
        this.modelsList = modelsList;
    }

    @NonNull@Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.list_car_wash_item,viewGroup,false);

        return new ListAllCarWashAdapter.ViewHolder(view);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

         viewHolder.textViewName.setText(modelsList.get(i).getNameCompany());
         viewHolder.textViewAddress.setText(modelsList.get(i).getNameAddress());

         float dis = modelsList.get(i).getDistance();
         if (dis > 1000){
             float distance = dis/1000;
             viewHolder.textViewDis.setText(String.format("%.2f", distance) + " км");
         }else{
            viewHolder.textViewDis.setText(String.valueOf((int) dis + " м"));
         }


    }


    @Override
    public int getItemCount() {
        return modelsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName,textViewAddress,textViewDis;
        Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            textViewName = itemView.findViewById(R.id.textListCompanyName);
            textViewAddress = itemView.findViewById(R.id.textListAddress);
            textViewDis = itemView.findViewById(R.id.textListDistance);
        }
    }
}
