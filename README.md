# Dublin Bus Android App Project - Kotlin

Using user's mobile GPS information, Getting 10 near stops of Dublin Bus from user's current location. Indicating that onto Google Map. If the user presses the Bus-Stops marker, Show Detail of real-time bus information via Dublin Bus official SOAP API.
RecyclerView Used. In order to get 10 nearest stop information, Java Machine Learning library Used. - KDtree implemantation. For consuming SOAP API, Ksoap2-android Used.

## APP Use-Case

<img src="https://media.giphy.com/media/St37eEcPkyxOlv2hGN/giphy.gif" height="50%" width="50%">

## Getting Started

> Import Project using Android Studio.

### Prerequisites

Google map API token will be Needed.

```
    <string name="google_maps_key" translatable="false" templateMergeStrategy="preserve"> ----- API KEY ----</string>
```

## Built With

* [Android SDK](https://developer.android.com/studio/) - Android Studio provides the fastest tools for building apps
* [Google Map](https://developers.google.com/maps/documentation/android-sdk/start/) - Google Map
* [KDtree](http://java-ml.sourceforge.net/api/0.1.0/net/sf/javaml/core/kdtree/KDTree.html/) - Java Machine Learning Algorithm to find Nearest Stop.
* [Ksoap2](http://www.kobjects.org/ksoap2/index.html/) - A lightweight and efficient SOAP library for the Android platform.


## Authors

* Jin Lim 2019.

## License

This project is licensed under the MIT License

