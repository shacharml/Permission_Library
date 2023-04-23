# Permission_Library [![Release](https://jitpack.io/v/jitpack/maven-simple.svg?style=flat-square)](https://jitpack.io/#jitpack/maven-simple)

This is a simple permission library for Android that allows you to easily request runtime permissions.

# Getting Started

## Installation

1) Add it in your root `build.gradle` at the end of repositories:

	    allprojects {
		repositories {
		  ...
		  maven { url 'https://jitpack.io' }
		}
	      }
      
------------------------------------------------------------------------------------------------------------

2) Add the following dependencies in you app level gradle file if not exists:

	    dependencies {
			implementation 'com.github.shacharml:Permission_Library:1.00.02'
		}

------------------------------------------------------------------------------------------------------------

## Usage

1) Create an instance of `permissionManager`:

		permissionManager permissionManager = new permissionManager(ActivityResultRegistry activityResultRegistry);

* Note that `ActivityResultRegistry` is from the androidx.activity.result package.


2) Call the `askForPermissions()` method to request the required permissions. 
This method requires an Activity instance, an implementation of the `PermissionGrantCallback` interface, an array of permission strings, and two strings for the rationale and message:

		permissionManager.askForPermissions(Activity activity, PermissionGrantCallback callback, String[] permissions, String message, String rationale);

* The `PermissionGrantCallback` interface has a single method `onPermissionGrant()`, which will be called when the permissions are granted. 
  The message string will be used to prompt the user for permission.
  The rationale string is used to explain to the user why the app requires the permission. 
  If the user previously denied the permission, the rationale will be displayed to the user before the permission request is made again.


3) Add the following code to your `AndroidManifest.xml` file:

		<uses-permission android:name="android.permission.XXX" />

* Replace XXX with the name of the permission you want to request.

------------------------------------------------------------------------------------------------------------

## Example (Java)

Here's an example of how to use the permission library:

	public class MainActivity extends AppCompatActivity implements PermissionGrantCallback {

    private permissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionManager = new permissionManager(getActivityResultRegistry());

        String[] permissions = new String[]{Manifest.permission.CAMERA};
        permissionManager.askForPermissions(this, this, permissions, "We need permission to use the camera", "We need permission to take pictures");
    }

    @Override
    public void onPermissionGrant() {
        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
    }
}

*In this example, the permission library is used to request permission to use the camera. When the permission is granted, a toast message will be displayed.

	
