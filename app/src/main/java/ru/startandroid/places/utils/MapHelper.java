package ru.startandroid.places.utils;

import android.graphics.Bitmap;
import android.location.Location;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.startandroid.places.R;
import ru.startandroid.places.base.Constants;
import ru.startandroid.places.web.data.Item;
import ru.startandroid.places.web.data.ItemBitmapWrapper;

public class MapHelper {

    private Collection<ItemBitmapWrapper> items;
    private Location currentLocation;

    private final List<MapObject> lastItemsMapObjects = new ArrayList<>(Constants.QUERY_SIZE);
    private MapObject lastLocationMapObject;

    private Image imagePlace;
    private Image imageLocation;

    private boolean locationIsUpdated;
    private boolean itemsAreUpdated;

    private Bound itemsBound;

    public void setItems(Collection<ItemBitmapWrapper> items) {
        this.items = items;
        itemsAreUpdated = true;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        locationIsUpdated = true;
    }

    public void forceUpdate() {
        locationIsUpdated = true;
        itemsAreUpdated = true;
    }


    public void showMarkersOnMap(Map map) {
        if (map == null) {
            return;
        }

        if (currentLocation == null) {
            return;
        }

        if (locationIsUpdated) {
            updateLocation(map);
        }

        if (itemsAreUpdated) {
            updateItems(map);
        }

        if (itemsBound == null) {
            zoomToLocation(map);
        } else {
            zoomToBound(map);
        }

    }

    void updateLocation(Map map) {
            locationIsUpdated = false;
            if (lastLocationMapObject != null) {
                map.removeMapObject(lastLocationMapObject);
            }

            MapObject location = new MapMarker(new GeoCoordinate(currentLocation.getLatitude(), currentLocation.getLongitude()),
                    getImageLocation());
            map.addMapObject(location);
            lastLocationMapObject = location;
    }

    void updateItems(Map map) {
            itemsAreUpdated = false;
            map.removeMapObjects(lastItemsMapObjects);
            lastItemsMapObjects.clear();
            itemsBound = null;

            if (items != null && items.size() > 0) {
                itemsBound = new Bound();
                itemsBound.add(currentLocation.getLatitude(), currentLocation.getLongitude());
                for (ItemBitmapWrapper itemBitmapWrapper : items) {
                    Item item = itemBitmapWrapper.getItem();
                    itemsBound.add(item.getPosition().get(0), item.getPosition().get(1));
                    MapObject mapObject = createItemMarker(itemBitmapWrapper);
                    map.addMapObject(mapObject);
                    lastItemsMapObjects.add(mapObject);
                }
            }
    }

    private void zoomToLocation(Map map) {
        map.setCenter(new GeoCoordinate(currentLocation.getLatitude(), currentLocation.getLongitude()),
                Map.Animation.NONE);
        map.setZoomLevel(
                (map.getMaxZoomLevel() + map.getMinZoomLevel()) / 5 * 4);
    }

    private void zoomToBound(Map map) {
        map.zoomTo(itemsBound
                .addCoordinatesAndGetCopy(currentLocation.getLatitude(), currentLocation.getLongitude())
                .createGeoBoundingBox(), Map.Animation.LINEAR, -1.0F);
    }

    private MapObject createItemMarker(ItemBitmapWrapper itemBitmapWrapper) {
        Item item = itemBitmapWrapper.getItem();
        Bitmap bitmap = itemBitmapWrapper.getBitmap();
        GeoCoordinate geoCoordinate = new GeoCoordinate(item.getPosition().get(0),
                item.getPosition().get(1));

        Image image;
        if (bitmap != null) {
            image = new Image();
            image.setBitmap(bitmap);
        } else {
            image = getImagePlace();
        }
        MapMarker mapMarker = new MapMarker(geoCoordinate, image);
        mapMarker.setTitle(item.getTitle());
        return mapMarker;
    }


    private Image getImageLocation() {
        if (imageLocation == null) {
            imageLocation = new Image();
            try {
                imageLocation.setImageResource(R.mipmap.marker_location);
            } catch (IOException e) {
            }
        }
        return imageLocation;
    }

    private Image getImagePlace() {
        if (imagePlace == null) {
            imagePlace = new Image();
            try {
                imagePlace.setImageResource(R.mipmap.marker_place);
            } catch (IOException e) {
            }
        }
        return imagePlace;
    }

    class Bound {
        Double minLat = null;
        Double minLon = null;
        Double maxLat = null;
        Double maxLon = null;

        public void add(double latitude, double longitude) {
            if (minLat == null) {
                minLat = latitude;
            } else {
                minLat = Math.min(minLat, latitude);
            }
            if (minLon == null) {
                minLon = longitude;
            } else {
                minLon = Math.min(minLon, longitude);
            }
            if (maxLat == null) {
                maxLat = latitude;
            } else {
                maxLat = Math.max(maxLat, latitude);
            }
            if (maxLon == null) {
                maxLon = longitude;
            } else {
                maxLon = Math.max(maxLon, longitude);
            }

        }

        public Bound addCoordinatesAndGetCopy(double latitude, double longitude) {
            Bound bound= new Bound();
            bound.minLat = this.minLat;
            bound.minLon = this.minLon;
            bound.maxLat = this.maxLat;
            bound.maxLon = this.maxLon;
            bound.add(latitude, longitude);
            return bound;

        }

        public GeoBoundingBox createGeoBoundingBox() {
            return new GeoBoundingBox(new GeoCoordinate(maxLat, minLon), new GeoCoordinate(minLat, maxLon));
        }
    }


}
