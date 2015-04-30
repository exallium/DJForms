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

package com.exallium.djforms.lib;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
                try {
                    Method m = model.getClass().getMethod(getFieldGetter(name));
                    field.setFieldValue(m.invoke(model));
                } catch (NoSuchMethodException e1) {
                } catch (InvocationTargetException e1) {
                    Log.e(TAG, "Bad Invocation", e);
                } catch (IllegalAccessException e1) {
                    Log.e(TAG, "Something Bad Happened", e);
                }
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
            final Object value = field.getFieldValue();
            try {
                Field f = model.getClass().getField(name);
                f.set(model, value);
            } catch (NoSuchFieldException e) {
                // Lookup method is impossible here...
                if (value == null)
                    continue;
                try {
                    Method m = resolveMethod(getFieldSetter(name), model, value);

                    if (m != null)
                        m.invoke(model, value);

                }  catch (InvocationTargetException e1) {
                    Log.e(TAG, "Bad Invocation", e);
                } catch (IllegalAccessException e1) {
                    Log.e(TAG, "Something Bad Happened", e);
                }
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Something Bad Happened", e);
            }
        }

        postSave(model);
    }

    /**
     * Hook to perform after save is complete.
     * @param model The model to act on.
     */
    protected void postSave(Object model) {}

    private String getFieldSetter(String name) {
        return String.format("set%s", capitalize(name));
    }

    private String getFieldGetter(String name) {
        return String.format("get%s", capitalize(name));
    }

    private String capitalize(String name) {
        return name.substring(0,1).toUpperCase() + name.substring(1);
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

    /**
     * Stupid frigging java type system makes no sense sometimes.
     * @param methodName Method we're looking for
     * @param subject    Where we're looking
     * @param value      What we're looking to put there
     * @return The method if found, else null
     */
    private Method resolveMethod(String methodName, Object subject, Object value) {
        try {
            return subject.getClass().getMethod(methodName, value.getClass());
        } catch (NoSuchMethodException e) {
            Class<?> clazz = null;
            if (value instanceof Long) {
                clazz = Long.TYPE;
            } else if (value instanceof Integer) {
                clazz = Integer.TYPE;
            } else if (value instanceof Byte) {
                clazz = Byte.TYPE;
            } else if (value instanceof Character) {
                clazz = Character.TYPE;
            } else if (value instanceof Short) {
                clazz = Short.TYPE;
            } else if (value instanceof Double) {
                clazz = Double.TYPE;
            } else if (value instanceof Float) {
                clazz = Float.TYPE;
            }

            try {
                return subject.getClass().getMethod(methodName, clazz);
            } catch (NoSuchMethodException e1) {
                return null;
            }
        }
    }
}
