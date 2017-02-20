package ru.startandroid.places;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.startandroid.places.search.ui.SearchFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new SearchFragment(), "SearchFragment").commit();
        }
    }
}
