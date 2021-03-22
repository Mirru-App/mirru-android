package com.handapp.mediapipebluetooth;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class CountDownFragment extends Fragment {
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long startTimeInMilliseconds = 6000; //10 seconds
    private ToggleButton toggleButton;
    private TextView countDownText;
    private int infiniteTime = 600000;

    public CountDownFragment() {
        // Required empty public constructor
    }

    public interface CountdownInterface {
        void sendCountdownState(boolean isTimerRunning);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    CountdownInterface countDownInterface;
    ChipGroup chipGroup;
    TextView countDownInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.countdown_fragment, container, false);

        countDownText = view.findViewById(R.id.countdown_text);
        toggleButton = view.findViewById(R.id.toggleButton);
        chipGroup = (ChipGroup) view.findViewById(R.id.chipGroup);
        countDownInfo = view.findViewById(R.id.countdown_info);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()) {
                    StartTimer();
                } else if (!toggleButton.isChecked()){
                    StopTimer();
                }
                countDownInterface.sendCountdownState(isTimerRunning);
            }
        });

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = (Chip) view.findViewById(checkedId);
                switch (chip.getText().toString()) {
                    case "5s":
                        startTimeInMilliseconds = 6000;
                        break;
                    case "10s":
                        startTimeInMilliseconds = 11000;
                        break;
                    case "20s":
                        startTimeInMilliseconds = 21000;
                        break;
                    case "∞":
                        startTimeInMilliseconds = infiniteTime;
                        break;
                }
                Log.w("w", "startTimeInMilliseconds " + startTimeInMilliseconds);
                Log.w("w", "You Selected " + chip.getText().toString());
                if (toggleButton.isChecked()) {
                    toggleButton.performClick();
                    toggleButton.performClick();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CountdownInterface) {
            countDownInterface = (CountdownInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement CountDownInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        countDownInterface = null;
    }

    public void StartTimer() {
        countDownTimer = new CountDownTimer(startTimeInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownInfo.setText("");
                if (startTimeInMilliseconds == infiniteTime) {
                    countDownText.setText("");
                } else {
                    countDownText.setText("" + millisUntilFinished / 1000);
                }
                if (millisUntilFinished < 1000) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 75);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 600);
                    Log.w("tag", "timer done");
                }
            }

            @Override
            public void onFinish() {
                StopTimer();
            }
        }.start();
        if (startTimeInMilliseconds == infiniteTime) {
            countDownText.setText("");
        } else {
            countDownText.setText(startTimeInMilliseconds / 1000 + "");
        }
        isTimerRunning = true;
    }

    public void StopTimer() {
        countDownTimer.cancel();
        toggleButton.setChecked(false);
        isTimerRunning = false;
        countDownText.setText("");
        countDownInterface.sendCountdownState(isTimerRunning);
    }
}