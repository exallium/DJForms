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
