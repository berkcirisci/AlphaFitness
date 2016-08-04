package layout;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class MyService extends Service {


    private LocationManager mgr = null;
    private String locationProvider;
    private boolean isStopped = false;

    @Override
    public void onCreate() {
        mgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationProvider = mgr.getBestProvider(criteria, true);
        Log.v("onCreate", "finished");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //check if internet is on/off
        setDelay();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        isStopped = true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void setDelay() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if(!isStopped)
                    startWork();
            }
        }, 1000);//120000
    }

    private void startWork() {
//check here for 1>gps and 2>network provider
//get location and save it

        Log.v("startWork", "started");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("startWork", "no permission");
            return;
        }
        Location location = mgr.getLastKnownLocation(locationProvider);
        if(location != null)
            Log.v("start workout", location.toString());
        setDelay();
    }
}
