package ru.programminglearning.com.googlemaps.Maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

import ru.programminglearning.com.googlemaps.Maps.ViewWindowMarker.Model;
import ru.programminglearning.com.googlemaps.R;
import ru.programminglearning.com.googlemaps.Utils.Utils;

/**
 *                              общие поля
 * mMap - сама карта
 * markerAdd - содержит обьект для добавления маркера на карту
 * reference - ссылка на базу для добавления нового маркера на карту
 *
 *
 *                 */

public class Maps extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener{

    private GoogleMap mMap;
    private boolean isFragmentAvailable;
    private boolean isCheckPermission;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isCheckPermission = ActivityCompat
                .checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isCheck",isCheckPermission);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            isCheckPermission = savedInstanceState.getBoolean("isCheckPermission");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment == null){
            FragmentManager manager = getFragmentManager();
            FragmentTransaction  transaction = manager.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            transaction.replace(R.id.map,mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    public Maps() {
        isFragmentAvailable = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);// включение контролеров зума


        initMyLocation(); // включаем в карту местоположение пользователя


        setOnMapLongClickListener();

        //если местоположение включено, то ставим слушатель на него
        if (mMap.isMyLocationEnabled() && isFragmentAvailable){
            mMap.setOnMyLocationChangeListener(this);

            connectDatabase();
        }


    }

    private void connectDatabase(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Markers");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Model model = snapshot.getValue(Model.class);
                    // получаем координаты модели
                    double latitude = model.getLatitude();
                    double longitude = model.getLongitude();
                    //суем их как параметры экземплятру LatLng и создаем маркер
                    LatLng place = new LatLng(latitude, longitude);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(place).icon(setIcon()));

                    try {
                        //если пользователь найден
                        if (mMap.getMyLocation() != null && mMap.isMyLocationEnabled()){
                            Location myLocation = mMap.getMyLocation();
                            Location locationMarker = new Location("Loc2");
                            locationMarker.setLatitude(model.getLatitude());
                            locationMarker.setLongitude(model.getLongitude());

                            float dis = myLocation.distanceTo(locationMarker);
                            // TODO тут нужно создать инфо окно для маркера и дать ему данные
                            marker.setTitle(model.getNameCompany());
                            if (dis >= 1000){
                                float distance = dis/1000;
                                marker.setSnippet(String.format("%.2f", distance) + "км");
                            }else{
                                marker.setSnippet(String.valueOf((int) dis) + "м");

                            }


                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Инициализация местоположения пользователя, которое отображается при условии
     * , что он разрешил доступ и включил gps модуль
     */
    @SuppressLint("MissingPermission")
    private void initMyLocation( ) {

        // если разрешение есть
        if (isCheckPermission) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {

                    if (!Utils.isGpsTrue(getActivity())) { // если GPS не включен

                        Toast.makeText(getActivity()
                                , "Пожалуйста, включите GPS"
                                , Toast.LENGTH_SHORT).show();

                        return true;
                    }


                    return false;
                }
            });

        } else { // если его нет, повторяем запрос
            requestPermissionLocation();
        }

    }

    /**
     * При изменениях местоположения меняется расстояние до маркеров, для этого повторно получаем
     * массив маркеров из базы и высчитаем расстояние до них*/
    @Override
    public void onMyLocationChange(final Location location) {
        //если местоположение включено, то ставим слушатель на него
        if (mMap.isMyLocationEnabled() && isFragmentAvailable){
            mMap.setOnMyLocationChangeListener(this);

            connectDatabase();
        }
    }


    /**
     * Метод спрашивает разрешение на местоположение
     */
    private void requestPermissionLocation() {
        //запрашиваем разрешение
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    // если оно дано
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        if (ContextCompat
                                .checkSelfPermission(getActivity(),
                                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mMap.setMyLocationEnabled(true);
                            if (!Utils.isGpsTrue(getActivity())) { // если GPS не включен

                                Toast.makeText(getActivity()
                                        , "Пожалуйста, включите GPS"
                                        , Toast.LENGTH_SHORT).show();
                            }else{
                                mMap.setMyLocationEnabled(true);
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Markers");
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            final Model model = snapshot.getValue(Model.class);
                                            // получаем координаты модели
                                            double latitude = model.getLatitude();
                                            double longitude = model.getLongitude();
                                            //суем их как параметры экземплятру LatLng и создаем маркер
                                            LatLng place = new LatLng(latitude, longitude);
                                            Marker marker = mMap.addMarker(new MarkerOptions().position(place).icon(setIcon()));
                                            if (mMap.getMyLocation() != null && mMap.isMyLocationEnabled()){
                                                Location myLocation = mMap.getMyLocation();
                                                Location locationMarker = new Location("Loc2");
                                                locationMarker.setLatitude(model.getLatitude());
                                                locationMarker.setLongitude(model.getLongitude());

                                                float dis = myLocation.distanceTo(locationMarker);
                                                // TODO тут нужно создать инфо окно для маркера и дать ему данные
                                                marker.setTitle(model.getNameCompany());
                                                if (dis >= 1000){
                                                    marker.setSnippet(String.format("%.2f", dis/1000) + "км");
                                                }else{
                                                    marker.setSnippet(String.valueOf((int) dis) + "м");

                                                }


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }
                    }
                    // если не дано
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(getActivity()
                                , "Вы должны принять разрешение на использование местоположения для оптимальной работоспособности приложения.", Toast.LENGTH_LONG)
                                .show();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    }
                }).check();

    }



    /**
     * TODO этот метод предстоит сделать после того, как появится модель окна маркера и связь с базой
     * Долгое нажатие на карту, этот метод работает у пользователей, с правами владельца автомоек
     */
    private void setOnMapLongClickListener() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                LatLng place = new LatLng(latLng.latitude, latLng.longitude);
                mMap.addMarker(new MarkerOptions().position(place).icon(setIcon()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place));

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Markers");
                Map<String,Object> map = new HashMap<>();
                map.put("latitude",latLng.latitude);
                map.put("longitude",latLng.longitude);
                map.put("nameCompany","ООО sc");
                map.put("modeWorkTime","С утра до вечера");
                map.put("prices","200-657");
                map.put("authorID","id243");
                map.put("nameAddress","улица 3");
                reference.push().setValue(map);


            }
        });
    }

    /**
     * Иконка для маркера*/
    private BitmapDescriptor setIcon(){
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.
                fromResource(R.drawable.car_wash);
        return bitmapDescriptor;
    }


    class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        private TextView nameCompanyMarker,adressMarker,priceMarker,modeWorkTimeMarker, distanceMarkerText;


        public MarkerInfoWindowAdapter() {
            initInfoContentsView();
        }

        /**
         * инициализация вью для компонента инфо окна*/
        private void initInfoContentsView(){
            LayoutInflater layoutInflater = getLayoutInflater();
            view = layoutInflater.inflate(R.layout.add_view_marker
                    , null
                    , false);
            nameCompanyMarker = view.findViewById(R.id.nameCompanyMarker);
            adressMarker = view.findViewById(R.id.adressMarker);
            priceMarker = view.findViewById(R.id.priceMarker);
            modeWorkTimeMarker = view.findViewById(R.id.modeWorkTimeMarker);
            distanceMarkerText = view.findViewById(R.id.distanceMarker);


        }


        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            return view;
        }
    }

}
