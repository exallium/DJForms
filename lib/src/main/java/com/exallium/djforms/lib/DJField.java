package com.exallium.djforms.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.InvocationTargetException;

/**
 * The Building Blocks for DJForms.
 * Each form field requires a View class, and optional Layout and Style information.
 * If you are using a custom layout, you should apply the styles as necessary within
 * the layout XML file, and simply pass NO_STYLE to the constructors here.
 *
 * Style will be ignored if there is a custom layout.
 *
 * If there is no custom layout, a view is created based off the style passed.  If
 * there is no style passed, one is created solely based off the given app context.
 */
public abstract class DJField<V extends View> {

    private static final String TAG = DJField.class.getName();
    public static final int NO_LAYOUT = -1;     // Specifies we are not using a custom layout
    public static final int NO_STYLE = -1;      // Specifies we are not using a custom style

    private final int styleId;
    private final int layoutId;
    private final Class<V> viewClass;
    private String name;

    /**
     * Default constructor for quick instances
     * @param viewClass The view class, passed in by the field subclass
     */
    public DJField(Class<V> viewClass) {
        this(viewClass, null, NO_LAYOUT, NO_STYLE);
    }

    /**
     * Constructor for name only
     * @param viewClass The view class, passed in by the Field subclass
     * @param name The name mapping for the model this field will be written into, or null
     */
    public DJField(Class<V> viewClass, String name) {
        this(viewClass, name, NO_LAYOUT, NO_STYLE);
    }

    /**
     * Create an instance of a Field, more common use case.
     * @param viewClass The view class, passed in by the Field subclass
     * @param name The name mapping for the model this field will be written into, or null
     * @param styleId Custom style id or NO_STYLE
     */
    public DJField(Class<V> viewClass, String name, int styleId) {
        this(viewClass, name, NO_LAYOUT, styleId);
    }

    /**
     * Create an instance of a Field
     * @param viewClass The view class passed in by the Field subclass
     * @param name The name mapping for the model field this will be written into, or null
     * @param layoutId Custom layout id or NO_LAYOUT
     * @param styleId Custom style id or NO_STYLE
     */
    public DJField(Class<V> viewClass, String name, int layoutId, int styleId) {
        this.viewClass = viewClass;
        this.name = name;
        this.layoutId = layoutId;
        this.styleId = styleId;
    }

    private V cachedView;

    /**
     * Initialize the view with listeners, etc.
     * @param view The view to initialize
     */
    protected abstract void onViewCreated(V view);

    @SuppressWarnings({"unchecked"})
    private V createFieldView(Context context) {
        V view = null;
        if (layoutId != NO_LAYOUT) {
            view = (V) LayoutInflater.from(context).inflate(layoutId, null);
        } else {
            try {
                if (styleId != NO_STYLE)
                    view = viewClass.getConstructor(Context.class).newInstance(new ContextThemeWrapper(context, styleId));
                else
                    view = viewClass.getConstructor(Context.class).newInstance(context);
            } catch (InstantiationException e) {
                Log.e(TAG, "Failed to Instantiate View", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Illegal Access Occurred", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "Invocation Failure", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "No Such Method Exists, did you create the right constructors?", e);
            }
        }

        if (view == null)
            throw new IllegalStateException("View object failed to be created");

        onViewCreated(view);
        return view;
    }

    /**
     * Allows for custom field validation in subclasses
     * @param view The View to Validate
     * @return true if valid, false otherwise
     */
    protected abstract boolean isValid(V view);

    public final V getFieldView(Context context) {
        if (cachedView == null) {
            cachedView = createFieldView(context);
        }
        return cachedView;
    }

    /**
     * Validate the data present in the field wrapped in this
     * DJField
     * @return true if valid, otherwise false
     */
    public final boolean isFieldValid() {
        return isValid(cachedView);
    }

    /**
     * Gets the name which maps this field to the proper location
     * in the model we write to.
     * @return The name passed to constructor
     */
    public String getName() { return name; }

    /**
     * Sets the field name from the form if and only if the field
     * doesn't already have a name (it's null)
     */
    void setFieldName(String name) { this.name = name; }

    /**
     * See getValue()
     * @return see getValue()
     */
    Object getFieldValue() {
        return getValue(cachedView);
    }

    /**
     * See setValue()
     * @param obj The field value
     */
    void setFieldValue(Object obj) {
        if (obj != null)
            setValue(cachedView, obj);
    }

    /**
     * Computes the final value for population
     * @return The final value.
     */
    public abstract Object getValue(V view);

    /**
     * Sticks the value into the view
     * @param view the View which should recieve the object data
     * @param data the data to throw into the view.
     */
    public abstract void setValue(V view, Object data);
}
