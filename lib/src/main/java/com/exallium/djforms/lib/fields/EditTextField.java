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

import android.widget.EditText;
import com.exallium.djforms.lib.DJField;

public class EditTextField extends DJField<EditText> {

    public EditTextField() {
        super(EditText.class);
    }

    public EditTextField(String name) {
        this(name, NO_STYLE);
    }

    public EditTextField(String name, int styleId) {
        super(EditText.class, name, styleId);
    }

    /**
     * Does nothing, but we don't want abstract stuff
     * @param view The view to initialize
     */
    @Override
    protected void onViewCreated(EditText view) {}

    /**
     * Simple case check if we have anything in our edittext
     * @param view The View to Validate
     * @return True if we're good to go, false otherwise
     */
    @Override
    protected boolean isValid(EditText view) {
        return view.length() != 0;
    }

    @Override
    public Object getValue(EditText view) {
        return view.getText().toString();
    }

    @Override
    public void setValue(EditText view, Object data) {
        view.setText(data.toString());
    }
}
