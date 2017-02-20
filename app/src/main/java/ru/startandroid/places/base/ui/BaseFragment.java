package ru.startandroid.places.base.ui;

import android.app.Fragment;
import android.os.Bundle;

public class BaseFragment extends Fragment {

    private boolean reCreate = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        reCreate = true;
    }

    protected boolean isRecreate() {
        return reCreate;
    }
}
