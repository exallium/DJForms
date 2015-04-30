package com.exallium.djforms.lib;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * A DJForm is, in essence, a collection of DJFields.
 * This is not a View, but it contains the necessary logic
 * to create them based off of the containing fields.
 *
 * Forms are stateful.  They represent a controller which
 * handles the validation and saving of user data to your
 * model layer.  Thus, you shouldn't use one form in 5 places,
 * you'll want 5 instances of the form.
 *
 * A form is made up of public DJFields, like DateDialogField and
 * EditTextField.  You can also create your own fields, all you need
 * to do is subclass DJField and implement the required methods
 */
public abstract class DJForm {

    public static final String TAG = DJForm.class.getSimpleName();

    private ViewGroup cachedViewGroup = null;
    private List<DJField> fieldCache = new LinkedList<>();
    private WeakReference<Context> weakContext = new WeakReference<Context>(null);

    public DJForm(Context context) {
        this.weakContext = new WeakReference<>(context);
    }

    /**
     * You can put whatever you want here as long as you return a valid ViewGroup.
     * @return The ViewGroup to stick the form into.
     */
    protected ViewGroup getViewGroup(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    /**
     * Generates the form based off the available DJFields, compiles it into a
     * ViewGroup, and returns it.
     * @return The ViewGroup object representing the form.
     */
    public final ViewGroup getFormViewGroup() {

        final Context context = weakContext.get();

        // If our context (Activity or whatever) has gone away, fail out.
        if (context == null)
            throw new IllegalStateException("Context is NULL");

        // If we already have a cached view group, return it.
        if (cachedViewGroup == null) {
            // Otherwise, generate a new one and fill it out.
            cachedViewGroup = getViewGroup(context);
            for (DJField field : getFormFields(false)) {
                cachedViewGroup.addView(field.getFieldView(context));
            }
        }

        return cachedViewGroup;
    }

    /**
     * Runs validation on all of the interior DJFields
     * @return true if all fields are valid, false otherwise
     */
    public final boolean isFormValid() {

        if (cachedViewGroup == null)
            throw new IllegalStateException("Must call getFormViewGroup before isFormValid");

        for (DJField field : getFormFields(false))
            if (!field.isFieldValid()) return false;
        return true;
    }

    /**
     * Writes from a model into fields
     * @param model The model to use for initialization
     */
    public final void fillViews(Object model) {
        if (fieldCache == null)
            throw new IllegalStateException("Must call getFormViewGroup before fillViews");

        for (DJField field : fieldCache) {
            final String name = field.getName();
            try {
                Field f = model.getClass().getField(name);
                field.setFieldValue(f.get(model));
            } catch (NoSuchFieldException e) {
                // Nothing to init, so we ignore it
            } catch (IllegalAccessException e) {
                Log.d(TAG, "Field" + field + " can't be accessed");
            }
        }
    }

    /**
     * Saves a model with the given info regardless of whether it's valid
     * @param model The object to save into
     */
    public final void save(Object model) {

        if (fieldCache == null)
            throw new IllegalStateException("Must call getFormViewGroup before save");

        // We get passed a "destination" for the field info.  The fields map from either their name
        // or from their DJField name
        for (DJField field : fieldCache) {
            final String name = field.getName();
            try {
                // Get corresponding model field
                Field f = model.getClass().getField(name);
                f.set(model, field.getFieldValue());
            } catch (NoSuchFieldException e) {
                Log.d(TAG, "Field " + field + " doesn't exist on passed model");
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Something Bad Happened", e);
            }
        }
    }

    private List<DJField> getFormFields(boolean clearCache) {

        if (clearCache)
            fieldCache.clear();
        else if (fieldCache.size() != 0)
            return fieldCache;

        for (Field field : this.getClass().getFields()) {
            if (DJField.class.isAssignableFrom(field.getType())) {
                try {
                    DJField djField = (DJField) field.get(this);
                    djField.setFieldName(field.getName());
                    fieldCache.add(djField);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, "Could not retrieve field");
                }
            }
        }
        return fieldCache;
    }
}
