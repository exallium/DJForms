/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Alex Hart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.exallium.djforms.lib.fields;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import com.exallium.djforms.lib.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * DateDialogField is made specifically to show a datepicker dialog
 * when clicked, and fill the information into an EditView.
 */
public class DateDialogField extends EditTextField {

    private static DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.CANADA);

    public DateDialogField() {
        this(null);
    }

    public DateDialogField(String name) {
        super(name, R.style.Form_DatePickerField);
    }

    public DateDialogField(String name, int styleId) {
        super(name, styleId);
    }

    @Override
    protected void onViewCreated(EditText view) {
        super.onViewCreated(view);
        view.setFocusable(false);
        view.setOnClickListener(onClickListener);
    }

    /**
     * Get a DateFormat object describing how you want to input
     * data into the EditText
     * @return a DateFormat instance.
     */
    protected DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Basic validator.  If you want more refined control (say, over a
     * range of dates) it's best to do it in a subclass
     * @param view The view contained.
     * @return true if we're good to go, false otherwise
     */
    @Override
    protected boolean isValid(EditText view) {
        try {
            return super.isValid(view) && getDateFormat().parse(view.getText().toString()) != null;
        } catch (ParseException e) {
            return false;
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View eView) {
            final Calendar now = Calendar.getInstance();
            new DatePickerDialog(eView.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    now.set(Calendar.YEAR, year);
                    now.set(Calendar.MONTH, monthOfYear + 1);
                    now.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    ((EditText) eView).setText(getDateFormat().format(now.getTime()));
                }
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH) - 1, now.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    @Override
    public Object getValue(EditText view) {
        try {
            return getDateFormat().parse((String) super.getValue(view));
        } catch (ParseException e) {
            return new Date();
        }
    }

    @Override
    public void setValue(EditText view, Object data) {
        view.setText(getDateFormat().format((Date) data));
    }
}
