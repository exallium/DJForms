# DJForms - Clean Forms for Android!

## Get it from Gradle

```groovy

repositories {
    maven {
        url  "http://dl.bintray.com/exallium/maven"
    }
}

dependencies {
    compile 'com.exallium.DJForms:lib:0.1'
}

```

## Basic Usage

```java

/**
 * This can be a POJO, SugarRecord, whatever.  Also, DJForms is smart enough
 * to look for get / set methods, so you can write this part in Kotlin as well.
 */
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

forms.xml

```xml
<resources>
    <!-- There are some parent styles available in DJForm's forms.xml -->
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
