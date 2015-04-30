package com.exallium.djforms.lib.fields;

import android.widget.EditText;
import com.exallium.djforms.lib.R;

public class NumberField extends EditTextField {

    public NumberField() {
        this(null);
    }

    public NumberField(String name) {
        this(name, R.style.Form_NumberField);
    }

    public NumberField(String name, int styleId) {
        super(name, styleId);
    }

    @Override
    public Object getValue(EditText view) {
        return Long.parseLong((String) super.getValue(view));
    }
}
