package pl.kstachowicz.favouriteplaces;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.constraint.solver.SolverVariable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String name_Text = "";
    private String desc_Text = "";
    private LatLng latLngPoint;
    private List<MarkerClass> listOfMarkers;
    private SharedPreferences sharedPref;
    private GoogleMap mMap;
    //TODO: FIREBASE implementation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listOfMarkers = new LinkedList<>();

        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        String json = sharedPref.getString(getString(R.string.json_string_prefs_name), "null");

        if(!json.equals("") && !json.equals("null")) {
            Gson gson = new Gson();

            Type collectionType = new TypeToken<Collection<MarkerClass>>() {}.getType();
            List<MarkerClass> temp = gson.fromJson(json, collectionType);
            for (MarkerClass x: temp){
                listOfMarkers.add(x);
            }

            for (MarkerClass marker : listOfMarkers) {
                makeMarker(marker);
                //jsonPointsStringSet.add(jsonPointString);

            }
        }

        final SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                mMap = googleMap;
                if(!listOfMarkers.isEmpty()){
                    moveToCurrentLocation(listOfMarkers.get(0).getLatLng());
                }

                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        latLngPoint = latLng;
                        createAlert(1);
                    }
                });
            }
        });
    }
    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();

        String json = gson.toJson(listOfMarkers);

        editor.putString(getString(R.string.json_string_prefs_name), json);
        editor.apply();
    }




    private void createAlert(final int option){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(option == 1) {
            builder.setTitle("Wprowadź nazwę");
        } else {
            builder.setTitle("Opisz punkt");
        }

        final EditText markerName = new EditText(this);

        markerName.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(markerName);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(option == 1) {
                    name_Text = markerName.getText().toString();
                    createAlert(2);
                }
                else{
                    desc_Text = markerName.getText().toString();
                    makeMarker();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void makeMarker(){
        final SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                BitmapDescriptor defaultMaker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                // LatLng listingPosition = new LatLng(50.0341, 19.5618);
                Marker mapMaker = googleMap.addMarker(new MarkerOptions()
                        .position(latLngPoint)
                        .title(name_Text)
                        .snippet(desc_Text)
                        .icon(defaultMaker));

                //String jsonString = convToJsonString(new MarkerClass(latLngPoint, name_Text, desc_Text));

                //Context context = getApplicationContext();

                //Toast toast = Toast.makeText(context, "JSON is: " + jsonString, Toast.LENGTH_LONG);
                //toast.setGravity(Gravity.TOP | Gravity.LEFT, 0 ,0);
                //toast.show();

                //saveJsonStringToPrefs(context, jsonString);
                listOfMarkers.add(new MarkerClass(latLngPoint, name_Text, desc_Text));
            }
        });
    }
    private void makeMarker(final MarkerClass marker){
        final SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                //Gson gson = new Gson();
                //MarkerClass marker = gson.fromJson(jsonPoint, MarkerClass.class);

                BitmapDescriptor defaultMaker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                // LatLng listingPosition = new LatLng(50.0341, 19.5618);
                Marker mapMaker = googleMap.addMarker(new MarkerOptions()
                        .position(marker.getLatLng())
                        .title(marker.getName())
                        .snippet(marker.getDesc())
                        .icon(defaultMaker));
            }
        });
    }
    private String convToJsonString(MarkerClass markerClass){
        Gson gson = new Gson();
        //String jsonMarker = ;
        return gson.toJson(markerClass);
    }

    public void showMapTypeSelectorDialog(View view) {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);
        final CharSequence[] MAP_TYPE_ITEMS =
                {"Road Map", "Hybrid", "Satellite", "Terrain"};
        // Find the current map type to pre-check the item representing the current state.
        int checkItem = mMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,10));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 500, null);


    }

}
