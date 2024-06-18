package com.usth.edu.View.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.usth.edu.Model.Setting;
import com.usth.edu.R;
import com.usth.edu.View.Adapter.SettingAdapter;

import java.util.ArrayList;

public class SettingFragment extends Fragment {
    private Context mContext;

    private String[] Titles ={"Feedback", "About"};
    private String[] Contents = {"Setting Feedback", "All about you"};
    private int[] Images = {R.drawable.ic_baseline_settings_24, R.drawable.ic_baseline_settings_24};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_fragment, container, false);
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        ArrayList<Setting> settings = new ArrayList<>();
        for (int i = 0; i < Titles.length; i++) {
            settings.add(new Setting(Titles[i], Contents[i], Images[i]));
        }
        SettingAdapter adapter = new SettingAdapter(settings);
        ListView lv = view.findViewById(R.id.lv_settings);
        lv.setAdapter(adapter);

        FeedbackFragment feedbackFragment = new FeedbackFragment();
        AboutFragment aboutFragment = new AboutFragment();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Check if "Feedback" item is clicked
                    navigateToFeedbackFragment(feedbackFragment);
                } if (position == 1) {
                    navigateToFeedbackFragment(aboutFragment);
                }
            }
        });
    }

    private void navigateToFeedbackFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
//        View container = getView().findViewById(R.id.fragment_container);
//        container.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}