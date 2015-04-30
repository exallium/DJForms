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

import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import com.exallium.djforms.lib.DJField;

public class AutoCompleteTextField<T extends BaseAdapter & Filterable> extends DJField<AutoCompleteTextView> {

    private final T adapter;

    public AutoCompleteTextField(T adapter) {
        super(AutoCompleteTextView.class, null, NO_STYLE);
        this.adapter = adapter;
    }

    public AutoCompleteTextField(int styleId, T adapter) {
        this(null, styleId, adapter);
    }

    public AutoCompleteTextField(String name, int styleId, T adapter) {
        super(AutoCompleteTextView.class, name, styleId);
        this.adapter = adapter;
    }

    @Override
    protected void onViewCreated(AutoCompleteTextView view){
        view.setAdapter(adapter);
    }

    @Override
    protected boolean isValid(AutoCompleteTextView view) {
        return view.getText().length() != 0;
    }

    @Override
    public Object getValue(AutoCompleteTextView view) {
        return view.getText().toString();
    }

    @Override
    public void setValue(AutoCompleteTextView view, Object data) {
        view.setText(data.toString());
    }

}
