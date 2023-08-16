import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化 osmdroid 配置
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // 檢查定位權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果沒有權限，請求用戶授權
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // 如果已經有權限，開始獲取用戶的位置
            startLocationUpdates();
        }
    }

    // 請求權限的回調
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用戶授權了，開始獲取用戶的位置
                startLocationUpdates();
            } else {
                // 用戶拒絕了授權，你可以在這裡做相應處理
                Log.d("MainActivity", "定位權限被拒絕");
            }
        }
    }

    private void startLocationUpdates() {
        // 在這裡使用 LocationManager 或 FusedLocationProviderClient 開始獲取用戶的位置
        // 並在位置變化時更新用戶的位置

        // 假設你使用 FusedLocationProviderClient 來獲取用戶的位置
        // 創建 FusedLocationProviderClient
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 檢查定位權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 如果沒有權限，返回
            return;
        }

        // 獲取用戶的位置
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // 獲得用戶的位置經緯度
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // 在地圖上顯示用戶的位置
                            showUserLocationOnMap(latitude, longitude);
                        }
                    }
                });
    }

    private void showUserLocationOnMap(double latitude, double longitude) {
        // 設置地圖中心點為用戶位置
        GeoPoint userLocation = new GeoPoint(latitude, longitude);
        mapView.getController().setCenter(userLocation);

        // 在用戶位置添加一個標記
        myLocationOverlay = new MyLocationNewOverlay(mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);

        mapView.invalidate(); // 重繪地圖以顯示標記
    }
}