package com.nodeers.finder.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.nodeers.finder.fragments.LostMobileFragment;
import com.nodeers.finder.fragments.LostPersonFragment;
import com.nodeers.finder.fragments.LostVehicleFragment;
import com.nodeers.finder.fragments.WantedFragment;

public class LostAdapter extends FragmentStateAdapter {

    public LostAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public LostAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public LostAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new LostPersonFragment();
            case 1:
                return new LostVehicleFragment();
//            case 2:
//                return new WantedFragment();
            case 2:
                return new LostMobileFragment();

        }

        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
