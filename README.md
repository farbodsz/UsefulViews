# LabelledSpinner 

[![GitHub version](https://badge.fury.io/gh/FarbodSalamat-Zadeh%2FLabelledSpinner.svg)](https://badge.fury.io/gh/FarbodSalamat-Zadeh%2FLabelledSpinner)
[![Build Status](https://travis-ci.org/FarbodSalamat-Zadeh/LabelledSpinner.svg?branch=master)](https://travis-ci.org/FarbodSalamat-Zadeh/LabelledSpinner)

A Spinner component with a floating label for Android, similar to EditText components wrapped in a TextInputLayout. It answers [this question on Stack Overflow](http://stackoverflow.com/questions/31625620/floating-label-spinner), and [another similar question](http://stackoverflow.com/questions/31001991/how-to-customize-a-spinner-with-floating-text).

## Gradle (Android Studio)
You can add this Android library to your Gradle dependencies.

To do so, add the following to your app module's `build.gradle` file:

```groovy
dependencies {
    ...
    compile 'com.farbod.labelledspinner:labelledspinner:1.1.2'
}
```

## Usage

Below is a basic example of how `LabelledSpinner` can be used. There is also a sample app [you can view on this repository](https://github.com/FarbodSalamat-Zadeh/LabelledSpinner/tree/master/sample), showcasing `LabelledSpinner`'s features in more detail.

XML layout file:
```xml
xmlns:ls="http://schemas.android.com/apk/res-auto"

...

<com.farbod.labelledspinner.LabelledSpinner
        android:id="@+id/your_labelled_spinner"
        ls:labelText="@string/your_spinner_label"
        ls:widgetColor="#F44336"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

And then in Java code...

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.example_usage);
    ...
    LabelledSpinner yourSpinner = (LabelledSpinner) findViewById(R.id.your_labelled_spinner);
    yourSpinner.setItemsArray(R.array.your_array);
    yourSpinner.setOnItemChosenListener(this);
}

@Override
public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
    String selectedText = adapterView.getItemAtPosition(position).toString();
    switch (labelledSpinner.getId()) {
        case R.id.your_labelled_spinner:
            // Do something here
            break;
        // If you have multiple LabelledSpinners, you can add more cases here
    }
}

@Override
public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {
    // Do something here
}
```

## Copyright

```
Copyright 2015-2016 Farbod Salamat-Zadeh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```