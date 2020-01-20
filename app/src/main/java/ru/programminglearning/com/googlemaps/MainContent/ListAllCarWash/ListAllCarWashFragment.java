package ru.programminglearning.com.googlemaps.MainContent.ListAllCarWash;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.programminglearning.com.googlemaps.Maps.ViewWindowMarker.Model;
import ru.programminglearning.com.googlemaps.R;
import ru.programminglearning.com.googlemaps.Utils.Utils;

public class ListAllCarWashFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Location location; // местоположение пользователя

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ставим слушатель на последнее местоположение user'a
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location locations) {
                        if (locations != null) {
                            location = locations;
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_list_all_car_wash, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            recyclerView = view.findViewById(R.id.recycler_ListAllCarWash);
            progressBar = view.findViewById(R.id.ListAllCarWashProgressBar);

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()
                    , LinearLayoutManager.VERTICAL
                    , false));


            if (Utils.isGpsTrue(getActivity())) { //если GPS включен
                getListFromDatabase(recyclerView);
            } else {
                recyclerView.setAdapter(new EmptyAdapter("Включите GPS и обновите список"));
                progressBar.setVisibility(View.GONE);
            }


        } else {
            Log.i("ListAllCarWashFragment", "view == null");
        }
    }

    /**
     * в этом методе мы в адаптер передаем коллекцию модели из базы
     */
    private void getListFromDatabase(final RecyclerView recyclerView) {
        final List<Model> list = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Markers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Model model = snapshot.getValue(Model.class);
                    if (model != null) {
                        //получаем локацию по координатам модели
                        Location locationMarker = new Location("Loc2");
                        locationMarker.setLatitude(model.getLatitude());
                        locationMarker.setLongitude(model.getLongitude());

                        if (location != null) {
                            //сравнимаем локацию с местоположенем юзера
                            float dis = location.distanceTo(locationMarker);
                            model.setDistance(dis);
                        } else {
                            model.setDistance(0);
                        }
                        list.add(model);
                    }
                }

                // сортируем коллекцию по дистанции до маркеров
                Collections.sort(list, new Comparator<Model>() {
                    public int compare(Model o1, Model o2) {
                        return (int) o1.getDistance() - (int) (o2.getDistance());
                    }
                });
                recyclerView.setAdapter(new ListAllCarWashAdapter(list));

                setAnimation(recyclerView);

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ListAllCarWashFragment", databaseError.getMessage());
            }
        });
    }

    private void setAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;

        controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }


}
