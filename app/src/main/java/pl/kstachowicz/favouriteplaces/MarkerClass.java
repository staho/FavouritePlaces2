package pl.kstachowicz.favouriteplaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by staho on 13.06.2017.
 */

public class MarkerClass {
    private LatLng latLng;
    private String name;
    private String desc;

    MarkerClass(){}

    public MarkerClass(LatLng latLng, String name, String desc) {
        this.latLng = latLng;
        this.name = name;
        this.desc = desc;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}

