package com.stylingandroid.christmasvoice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

    private static final int PERMISSIONS_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChristmasVoiceApplication.getComponent(this).inject(this);
        if (!hasRequiredPermissions()) {
            requestRequiredPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasRequiredPermissions()) {
            setFragment(RecorderFragment.class);
        }
    }

    private void setFragment(Class<? extends Fragment> fragmentClass) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentClass.getName());
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
            } catch (Exception e) {
                Timber.e(e, "Error instantiating fragment %s", fragmentClass.getName());
            }
        }
        setFragment(fragment, fragmentClass.getName());
    }

    private void setFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_main, fragment, tag);
        transaction.commit();
    }

    void requestRequiredPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_CODE);
    }

    private boolean hasRequiredPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!hasRequiredPermissions()) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_main);
            if (fragment instanceof PermissionsFragment) {
                ((PermissionsFragment) fragment).alreadyRequested();
            } else {
                setFragment(PermissionsFragment.class);
            }
        }
    }
}
