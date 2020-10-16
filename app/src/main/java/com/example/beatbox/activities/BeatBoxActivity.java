package com.example.beatbox.activities;

import androidx.fragment.app.Fragment;

import com.example.beatbox.fragments.BeatBoxFragment;

public class BeatBoxActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }
}