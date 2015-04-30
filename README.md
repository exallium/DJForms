# DJForms - Clean Forms for Android!

## Basic Usage

```::java
class Contact {
    public String emailAddress;
    public String name;
}

/**
 * Currently required to be implemented in Java due to how the Fields are
 * collected by DJForm
 */
class ContactForm extends DJForm {
    public EditTextField name = new EditTextField();
    public EditTextField address = new EditTextField("emailAddress", R.style.EmailField);
}

class MyActivity extends Acticity {

    ContactForm form;
    Contact contact;

    public onCreate(/* ... */) {
        form = new ContactForm(this);
        ViewGroup viewGroup = form.getFormViewGroup();
        // Do whatever you want with ViewGroup
        contact = /* get a contact */
        form.fillViews(contact);
    }

    /* when you hit a save button or something */
    public void onSave() {
        if (form.isFormValid()) { // Optional Validation
            form.save(contact);
            contact.save();
        } else {
            // popup a message or something.
        }
    }

}
```

## Adding Hint text, etc.

styles.xml

```::xml
<resources>
    <style name="EmailField" parent="Form">
        <item name="android:hint">@string/email_hint</item>
    </style>
</resources>
```

## Adding a custom Form Field

```
public class EmailField extends EditTextField {

    /**
     * Constructors
     */

    protected isValid(EditText view) {
        // compare to a regex
    }    

}
```

## TODO:

* Full Kotlin Support
* Form validator coloring
* Add mor fields
