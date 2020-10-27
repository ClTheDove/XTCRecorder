package xds.fishy.util.widget.maxlengthwatcher;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import xds.fishy.recorder.R;

public class MaxLengthWatcher implements TextWatcher {
    private long lastToastTime = 0;
    private Context context;
    private int maxLen;

    public MaxLengthWatcher(Context context, int i) {
        this.context = context;
        maxLen = i;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() >= maxLen && System.currentTimeMillis() - lastToastTime > 1000) {
            Toast.makeText(context, R.string.cannot_be_more_longer, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }
}

