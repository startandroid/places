package ru.startandroid.places.search.ui;

import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.startandroid.places.R;
import ru.startandroid.places.app.App;
import ru.startandroid.places.base.ui.BaseFragment;
import ru.startandroid.places.utils.MapHelper;

public class SearchFragment extends BaseFragment implements SearchContract.View {

    @BindView(R.id.location_info)
    TextView textViewLocationInfo;

    @BindView(R.id.location_progress)
    ProgressBar progressBarLocationProgress;

    @BindView(R.id.query)
    EditText editTextQuery;

    @BindView(R.id.search_progress)
    View viewSearchProgress;

    @BindView(R.id.search)
    Button buttonSearch;
    @Inject
    SearchContract.Presenter presenter;

    private MapFragment mapFragment;
    private Map map;

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        App.getApp(context).getComponentsHolder().getSearchComponent().injectSearchFragment(this);
        presenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.fillView();
    }

    private void addOnGestureListener() {
        mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
            @Override
            public boolean onMapObjectsSelected(List<ViewObject> objects) {
                for (ViewObject viewObj : objects) {
                    if (viewObj.getBaseType() == ViewObject.Type.USER_OBJECT) {
                        if (((MapObject) viewObj).getType() == MapObject.Type.MARKER) {
                            MapMarker mapMarker = (MapMarker) viewObj;
                            if (mapMarker.isInfoBubbleVisible()) {
                                mapMarker.hideInfoBubble();
                            } else {
                                mapMarker.showInfoBubble();
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onViewStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onViewPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onViewStop();
    }

    private void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(
                R.id.mapfragment);
        mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(
                    OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {
                    map = mapFragment.getMap();
                    addOnGestureListener();
                    presenter.mapIsReady();
                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });
    }

    @OnClick(R.id.search)
    void onSearchClick() {
        presenter.onSearchClick(editTextQuery.getText().toString());
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextQuery.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (!isRecreate()) {
            presenter.destroy();
            App.getApp(getActivity()).getComponentsHolder().releaseSearchComponent();
        }
    }

    @Override
    public void showLocationProgress() {
        progressBarLocationProgress.setVisibility(View.VISIBLE);
        textViewLocationInfo.setVisibility(View.GONE);
    }

    @Override
    public void hideLocationProgress() {
        progressBarLocationProgress.setVisibility(View.GONE);
        textViewLocationInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLocationInfo(String text) {
        textViewLocationInfo.setText(text);
        textViewLocationInfo.setVisibility(View.VISIBLE);
    }

    @Override
    public void enableSearch(boolean enable) {
        editTextQuery.setEnabled(enable);
        buttonSearch.setEnabled(enable);
    }

    @Override
    public void showSearchLoading() {
        viewSearchProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchLoading() {
        viewSearchProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showLocationSettingsDialog(Status status) {
        try {
            status.startResolutionForResult(getActivity(), 1);
        } catch (IntentSender.SendIntentException e) {
        }
    }

    @Override
    public void showResolutions(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(getActivity(), 2);
        } catch (IntentSender.SendIntentException e) {
        }
    }

    @Override
    public void updateMap(MapHelper mapHelper) {
        mapHelper.showMarkersOnMap(map);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showErrorMessage(int messageId) {
        showErrorMessage(getString(messageId));
    }

}
