package com.kernelhax.remotesensor.input;

import android.text.InputFilter;
import android.text.Spanned;

public class IPByteInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        StringBuilder builder = new StringBuilder(dest);
        if (dstart > (dest.length() - 1)) {
            builder.append(source);
        } else {
            builder.replace(dstart, dend, source.toString().substring(start, end));
        }

        String newString = builder.toString();

        try {
            int newValue = Integer.parseInt(newString);
            if (validateInput(newValue)) {
                return null; // Accept
            } else {
                return ""; // Reject
            }
        } catch (NumberFormatException e) {
            return ""; // Reject input
        }

    }

    private boolean validateInput(int newValue) {
        return (newValue >= 0 && newValue <= 255);
    }
}
