package com.example.beatbox.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.beatbox.R;
import com.example.beatbox.model.Sound;
import com.example.beatbox.repository.BeatBoxRepository;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BeatBoxFragment extends Fragment {

    public static final String TAG = "BeatBoxFragment";
    private RecyclerView mRecyclerView;
    private BeatBoxRepository mRepository;
    private SeekBar mSeekBar;
    private ImageButton mImageButton_Pause, mImageButton_Play;
    private TextView mTextViewTime;
    public BeatBoxFragment() {
        // Required empty public constructor
    }

    public static BeatBoxFragment newInstance() {
        BeatBoxFragment fragment = new BeatBoxFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setRetainInstance(true);
        mRepository = BeatBoxRepository.getInstance(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mRepository.releaseSoundPool();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beat_box, container, false);

        findViews(view);
        initViews();
        listeners();
        seekBar();
        setupAdapter();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView");
    }


    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_beat_box);
        mSeekBar = view.findViewById(R.id.seekBar);
        mImageButton_Play = view.findViewById(R.id.imageBtn_play);
        mImageButton_Pause = view.findViewById(R.id.imageBtn_pause);
        mTextViewTime = view.findViewById(R.id.txtView_Time);
    }

    private void initViews() {
        int rowNumber = getResources().getInteger(R.integer.column);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), rowNumber));
    }

    private void listeners() {

        mImageButton_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.playAgain();
            }
        });
        mImageButton_Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRepository.pause();
            }
        });
    }

    private void seekBar() {
        mSeekBar.setMax(mRepository.getMediaPlayer().getDuration());
        mSeekBar.setProgress(mRepository.getMediaPlayer().getCurrentPosition());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mRepository.getMediaPlayer().getDuration());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mRepository.getMediaPlayer().getDuration()) - (minutes * 60);

        final String maxTime ="/" + minutes + ":" + seconds;
        mTextViewTime.setText("0" + maxTime);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                mSeekBar.setProgress(mRepository.getMediaPlayer().getCurrentPosition());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(mRepository.getMediaPlayer().getCurrentPosition());
                long seconds = TimeUnit.MILLISECONDS.toSeconds(mRepository.getMediaPlayer().getCurrentPosition()) - (minutes * 60);
                String currentTime;
                if (minutes != 0) {
                    currentTime = minutes + ":" + seconds;
                }
                else {
                    currentTime = "" + seconds;
                }
                mTextViewTime.setText(currentTime + maxTime);
            }
        },0,1000);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                if (b)
                    mRepository.getMediaPlayer().seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupAdapter() {
        List<Sound> sounds = mRepository.getSounds();
        SoundAdapter adapter = new SoundAdapter(sounds);
        mRecyclerView.setAdapter(adapter);
    }

    private class SoundHolder extends RecyclerView.ViewHolder {

        private Button mButton;
        private Sound mSound;

        public SoundHolder(@NonNull View itemView) {
            super(itemView);

            mButton = itemView.findViewById(R.id.button_beat_box);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRepository.play(mSound);
                }
            });
        }

        public void bindSound(Sound sound) {
            mSound = sound;
            mButton.setText(mSound.getName());
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSounds;

        public List<Sound> getSounds() {
            return mSounds;
        }

        public void setSounds(List<Sound> sounds) {
            mSounds = sounds;
        }

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @NonNull
        @Override
        public SoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_sound, parent, false);

            return new SoundHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bindSound(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }
}