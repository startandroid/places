package ru.startandroid.places.search.dagger;

import dagger.Subcomponent;
import ru.startandroid.places.search.ui.SearchFragment;

@SearchScope
@Subcomponent(modules = SearchModule.class)
public interface SearchComponent {
    void injectSearchFragment(SearchFragment searchFragment);

}
