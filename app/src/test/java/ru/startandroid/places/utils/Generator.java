package ru.startandroid.places.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.startandroid.places.web.data.Item;
import ru.startandroid.places.web.data.Results;
import ru.startandroid.places.web.data.SearchResponse;

public class Generator {

    static String ICON = "https://download.vcdn.cit.data.here.com/p/d/places2_stg/icons/categories/35.icon";

    public static SearchResponse getDummyResponse() {
        Results results = new Results();
        results.setItems(getDummyListOfItems());

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResults(results);
        return searchResponse;
    }

    public static List<Item> getDummyListOfItems() {
        List<Item> items = new ArrayList<>();
        items.add(createDummyItem(1, 1, "one", ICON));
        items.add(createDummyItem(2, 2, "two", ICON));
        items.add(createDummyItem(3, 3, "three", ICON));
        items.add(createDummyItem(4, 4, "four", ICON));
        items.add(createDummyItem(5, 5, "five", ICON));
        return items;
    }

    public static Item createDummyItem(double lat, double lon, String title, String icon) {
        Item item = new Item();
        item.setPosition(Arrays.asList(lat, lon));
        item.setTitle(title);
        item.setIcon(icon);
        return item;
    }

}
