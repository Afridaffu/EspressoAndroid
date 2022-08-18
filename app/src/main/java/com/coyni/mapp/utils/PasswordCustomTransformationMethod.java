package com.coyni.mapp.utils;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;

public class PasswordCustomTransformationMethod extends PasswordTransformationMethod {

        private char BULLET = '•', HYPHEN = '-';

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private final CharSequence source;

            public PasswordCharSequence(@NonNull CharSequence source) {
                this.source = source;
            }

            @Override
            public int length() {
                return source.length();
            }

            @Override
            public char charAt(int index) {
                if (Character.isDigit(source.charAt(index)))
                    return BULLET;
                else
                    return HYPHEN;
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return new PasswordCharSequence(this.source.subSequence(start, end));
            }
        }

    }
