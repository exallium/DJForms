package com.exallium.djforms.lib;

import android.content.Context;
import android.view.ViewGroup;
import com.exallium.djforms.lib.fields.DateDialogField;
import com.exallium.djforms.lib.fields.EditTextField;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest= "lib/src/main/AndroidManifest.xml", emulateSdk = 18)
public class DjFormTest {

    public final class ExampleModel {
        public String name;
        public Date date;
        public String aThirdField;
    }

    public final class ExampleForm extends DJForm {
        public EditTextField name = new EditTextField();
        public DateDialogField date = new DateDialogField();
        public EditTextField aThirdField = new EditTextField();

        public ExampleForm(Context context) {
            super(context);
        }
    }

    @BeforeClass
    public static void setUpClass() {
        ShadowLog.stream = System.out;
    }

    @AfterClass
    public static void tearDownClass() {
        ShadowLog.stream = null;
    }

    @Test
    public void testGetFormViewGroup() {
        ExampleForm form = new ExampleForm(Robolectric.application);
        ViewGroup viewGroup = form.getFormViewGroup();

        // Assert All fields were read in
        assertEquals(3, viewGroup.getChildCount());

        // Assert Order preserved
        assertEquals(form.name.getFieldView(Robolectric.application), viewGroup.getChildAt(0));
        assertEquals(form.date.getFieldView(Robolectric.application), viewGroup.getChildAt(1));
        assertEquals(form.aThirdField.getFieldView(Robolectric.application), viewGroup.getChildAt(2));

        // Assert multiple calls to getFormViewGroup returns the same object
        assertEquals(viewGroup, form.getFormViewGroup());
    }

    @Test(expected=IllegalStateException.class)
    public void testIsFormValidBeforeGetFormView() {
        ExampleForm form = new ExampleForm(Robolectric.application);
        form.isFormValid();
    }

    @Test
    public void testIsFormValidAndSave() {
        ExampleForm form = new ExampleForm(Robolectric.application);

        form.getFormViewGroup();
        assertFalse(form.isFormValid());

        // Let's add some valid data.
        form.name.getFieldView(Robolectric.application).setText("Hello");
        form.date.getFieldView(Robolectric.application).setText("May 25, 2015");
        form.aThirdField.getFieldView(Robolectric.application).setText("olleH");

        assertTrue(form.isFormValid());

        ExampleModel model = new ExampleModel();
        form.save(model);

        assertEquals(model.name, "Hello");
    }

}
