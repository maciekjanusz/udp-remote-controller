package com.kernelhax.remotesensor.input;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kernelhax.remotesensor.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IPInputView extends LinearLayout {

    @Bind(R.id.byte0_edit_text)
    EditText byte0EditText;
    @Bind(R.id.byte1_edit_text)
    EditText byte1EditText;
    @Bind(R.id.byte2_edit_text)
    EditText byte2EditText;
    @Bind(R.id.byte3_edit_text)
    EditText byte3EditText;

    public IPInputView(Context context) {
        super(context);
        init(context);
    }

    public IPInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IPInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public IPInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_ip_input, this);
        ButterKnife.bind(this, this);

        InputFilter[] byteInputFilter = {new IPByteInputFilter()};
        byte0EditText.setFilters(byteInputFilter);
        byte1EditText.setFilters(byteInputFilter);
        byte2EditText.setFilters(byteInputFilter);
        byte3EditText.setFilters(byteInputFilter);
    }

    public byte[] getIpAddress() {
        String byte0String = byte0EditText.getText().toString();
        String byte1String = byte1EditText.getText().toString();
        String byte2String = byte2EditText.getText().toString();
        String byte3String = byte3EditText.getText().toString();

        byte[] ip = {
                Byte.parseByte(byte0String),
                Byte.parseByte(byte1String),
                Byte.parseByte(byte2String),
                Byte.parseByte(byte3String),
            };

        return ip;
    }
}
