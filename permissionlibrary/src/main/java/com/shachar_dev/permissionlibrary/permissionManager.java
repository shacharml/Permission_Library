package com.shachar_dev.permissionlibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class permissionManager implements DefaultLifecycleObserver {

    private ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private final ActivityResultRegistry resultRegistry;
    private final String ASK_PERMISSION_KEY = "ASK_PERMISSION_KEY";


    public interface PermissionGrantCallback {
        void onPermissionGrant();
    }

    public permissionManager(@NonNull ActivityResultRegistry mmRegistry) {
        this.resultRegistry = mmRegistry;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
    }

    public void askForPermissions(AppCompatActivity activity, PermissionGrantCallback callback, String[] permissions, String message, String rational) {

        requestPermissionsLauncher = resultRegistry.register(ASK_PERMISSION_KEY, new ActivityResultContracts.RequestMultiplePermissions(),
                result -> permissionsHandler(activity, callback, permissions, message, rational));

        requestPermissions(permissions);
    }

    /**
     * This function is the Handler of All the permission ask
     * 1. in first time ask for permission
     * if press to not give the permission
     * 2.ask the Permission with Rationale massage
     * if press to not give the permission
     * 3. Open the Permission Setting Dialogs on the phone
     * @param activity
     * @param callback
     * @param permissions
     * @param message
     * @param rational
     */
    private void permissionsHandler(AppCompatActivity activity, PermissionGrantCallback callback, String[] permissions, String message, String rational) {
        if (hasPermissions(activity, permissions)) {
            Toast.makeText(activity, "Permission granted", Toast.LENGTH_SHORT).show();
            callback.onPermissionGrant();
        } else if (shouldShowRequestPermissionRationale(activity, permissions)) {
            Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show();
            requestPermissionWithRationaleCheck(activity, permissions, rational);
        } else
            // Permission is denied, and user clicked on "Don't ask again"
            openPermissionSettingDialogs(activity, message);
    }

    /**
     * Check if the app has the given permission.
     *
     * @param activity   Activity
     * @param permission the permission to check
     * @return true if the app has the permission, false otherwise
     */
    private boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check if the app has all the given permissions.
     *
     * @param activity    the activity
     * @param permissions the permissions to check
     * @return true if the app has all the permissions, false otherwise
     */
    private boolean hasPermissions(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Open the app's settings page.
     *
     * @param context the context
     */
    private void openSettingsManually(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    /**
     * Open the permission settings dialog so the user can grant the permissions manually.
     *
     * @param context the activity
     */
    private void openPermissionSettingDialogs(Context context, String toastMessage) {
        AlertDialog alertDialog =
                new AlertDialog.Builder(context)
                        .setMessage(toastMessage)
                        .setPositiveButton(("OK"),
                                (dialog, which) -> {
                                    openSettingsManually(context);
                                    dialog.cancel();
                                }).show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * Check if the app should show the rationale for requesting the given permissions.
     *
     * @param activity    the activity
     * @param permissions the permissions to check
     * @return true if the app should show the rationale, false otherwise
     */
    private static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return true;
        }
        return false;
    }

    /**
     * Request the given permissions, showing a rationale if necessary.
     *
     * @param activity    the activity
     * @param permissions the permissions to request
     * @param rationale   the rationale to show if necessary
     */
    private void requestPermissionWithRationaleCheck(Activity activity, String[] permissions, String rationale) {
        // Show the rationale and request the permission
        new AlertDialog.Builder(activity)
                .setTitle("Permission required")
                .setMessage(rationale)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Request the permission after the user has acknowledged the rationale
                    requestPermissions(permissions);
                })
                .create()
                .show();
    }

    /**
     * Request the given permissions.
     *
     * @param permissions the permissions to request
     */
    private void requestPermissions(String[] permissions) {
        requestPermissionsLauncher.launch(permissions);
    }

}