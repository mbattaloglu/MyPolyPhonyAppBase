package com.maphomework.mypolyphonyapp;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.regex.Pattern;

public class EmailChecker {

    public static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean isValid = false;

    public boolean isValid() {
        return isValid;
    }

    public static boolean isValidEmail(CharSequence email) {
        return email != null && EMAIL_REGEX_PATTERN.matcher(email).matches();
    }
}