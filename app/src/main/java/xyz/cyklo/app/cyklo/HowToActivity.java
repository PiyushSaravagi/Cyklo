package xyz.cyklo.app.cyklo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import xyz.cyklo.api.PagerAdapter;
import xyz.cyklo.api.ViewsFragment;
public class HowToActivity extends AppCompatActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getFragments());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(ViewsFragment.newInstance("1. Connect to WIFI \n DETAILS", R.drawable.password,false,true));
        fragments.add(ViewsFragment.newInstance("2. Request A Cycle", R.drawable.locked,true,true));
        fragments.add(ViewsFragment.newInstance("3. Approach the caretaker", R.drawable.processing,true,true));
        fragments.add(ViewsFragment.newInstance("4. Ride your cycle", R.drawable.unlocked,true,true));
        fragments.add(ViewsFragment.newInstance("5. Check Amount", R.drawable.amount,true,true));
        fragments.add(ViewsFragment.newInstance("6. Return Cycle", R.drawable.confirm_return,true,true));
        fragments.add(ViewsFragment.newInstance("7. Pay up", R.drawable.processing,true,true));
        fragments.add(ViewsFragment.newInstance("8. Enjoy Again!", R.drawable.locked,true,false));

        return fragments;

    }
}
