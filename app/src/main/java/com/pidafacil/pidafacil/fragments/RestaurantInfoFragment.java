package com.pidafacil.pidafacil.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.beans.UserRequestBean;
import com.pidafacil.pidafacil.fragments.tabs.RestaurantInformationFragmentTab;
import com.pidafacil.pidafacil.fragments.tabs.SectionFragmentTab;
import com.pidafacil.pidafacil.model.Product;
import com.pidafacil.pidafacil.singleton.UD;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by victor on 05-27-15.
 */
public class RestaurantInfoFragment extends Fragment implements MaterialTabListener {

    private MaterialTabHost tabHost;
    private ViewPager pager;
    private ViewPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_restaurant_info, container, false);
        UserRequestBean userRequest = new UserRequestBean(Integer.valueOf(UD.getInstance()
                .get(UD.RESTAURANT_).toString()), UD.USER);
        UD.getInstance().put(UD.CURRENT_USER_RESTAURANT_REQUEST_, userRequest);
        tabSetup(view);
        UD.getInstance().put("restaurant_info_fragment", this);

        return view;
    }

    void tabSetup(View view){
        pager = (ViewPager) view.findViewById(R.id.viewpager_res_info);
        tabHost = (MaterialTabHost) view.findViewById(R.id.materialTabHost);
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // setearlo en el asynchtask es la unica manera para que funcione.
        // porque se genera un bug
        new SetAdapterTask().execute();

        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

    }

    public void goToProducts() {
        MaterialNavigationDrawer parent = (MaterialNavigationDrawer) getActivity();
        parent.setFragmentChild(new ProductFragment(), "");
        UD.getInstance().setCurrentFragment(ProductFragment.class.getSimpleName());
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) { }

    @Override
    public void onTabUnselected(MaterialTab materialTab) { }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int num) {
            if(num == 0){
                 return new SectionFragmentTab();
             }else{
                 return new RestaurantInformationFragmentTab();
             }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0){
                return "MENÚ";
            }else{
                return "INFORMACIÓN";
            }
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) { }
    }

    private class SetAdapterTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if(adapter != null) pager.setAdapter(adapter);
        }
    }
}
