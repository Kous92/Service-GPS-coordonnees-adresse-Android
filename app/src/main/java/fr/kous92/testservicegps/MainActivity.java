package fr.kous92.testservicegps;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private Button buttonStart, buttonStop;
    private TextView coordonneesGPS, infoGPS, statutService, adresseGPS;
    private BroadcastReceiver broadcastReceiver;
    private Geocoder geocoder;
    private List<Address> position = null;
    private RequestQueue request_queue;

    @Override
    protected void onResume()
    {
        super.onResume();

        if (broadcastReceiver == null)
        {
            broadcastReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    // String coordonnees = intent.getExtras().get("coordinates").toString();
                    coordonneesGPS.setText(intent.getExtras().get("coordinates").toString());
                    // coordonneesGPS.append("\n" +intent.getExtras().get("coordinates"));
                    infoGPS.setVisibility(View.VISIBLE);
                    coordonneesGPS.setVisibility(View.VISIBLE);

                    double longitude = intent.getDoubleExtra("longitude", 1);
                    double latitude = intent.getDoubleExtra("latitude", 1);
                    StringBuilder str = new StringBuilder();

                    request_queue = Volley.newRequestQueue(getApplicationContext());
                    String api_key = "&key=AIzaSyAZda2KBjVDLFZagiyVQQWA0L74D4l0TH0";
                    String lat = String.valueOf(latitude);
                    String lng = String.valueOf(longitude);
                    String latlng = "latlng=" + lat + "," + lng;
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?" + latlng + api_key;
                    System.out.println("Requête: " + url);

                    adresseGPS.setVisibility(View.VISIBLE);
                    adresseGPS.setText("Synchronisation de l'adresse...");

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            // mTextView.setText("Response: " + response.toString());
                            System.out.println("Réponse: " + response.toString());

                            try
                            {
                                // String address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                String city = response.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("long_name");
                                String country = response.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("long_name");
                                String region = response.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(4).getString("long_name");
                                String departement = response.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(3).getString("long_name");
                                String formatted_address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                // System.out.println(response.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(5).getString("long_name"));
                                adresseGPS.setVisibility(View.VISIBLE);
                                adresseGPS.setText(city + ", " + country + "\n" + formatted_address);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                adresseGPS.setVisibility(View.VISIBLE);
                                adresseGPS.setText("ERREUR");
                            }
                        }
                    }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            // TODO: Handle error
                            Log.e("ERROR", "Error occurred ", error);

                        }
                    });
                    request_queue.add(request);
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (broadcastReceiver != null)
        {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geocoder = new Geocoder(this, Locale.FRENCH);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setVisibility(View.INVISIBLE);
        infoGPS = (TextView) findViewById(R.id.infoGPS);
        adresseGPS = (TextView) findViewById(R.id.adresseGPS);
        coordonneesGPS = (TextView) findViewById(R.id.coordonneesGPS);
        statutService = (TextView) findViewById(R.id.statutService);
        statutService.setText("Statut service GPS: désactivé");
        infoGPS.setVisibility(View.INVISIBLE);
        coordonneesGPS.setVisibility(View.INVISIBLE);
        adresseGPS.setVisibility(View.INVISIBLE);


        if (!runtime_permissions())
        {
            enable_buttons();
        }
    }

    private void enable_buttons()
    {

        buttonStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getApplicationContext(), ServiceGPS.class);
                coordonneesGPS.setText("Synchronisation en cours...");
                coordonneesGPS.setVisibility(View.VISIBLE);
                infoGPS.setVisibility(View.VISIBLE);
                adresseGPS.setVisibility(View.INVISIBLE);
                startService(i);
                statutService.setText("Statut service GPS: activé");
                buttonStart.setVisibility(View.INVISIBLE);
                buttonStop.setVisibility(View.VISIBLE);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                Intent i = new Intent(getApplicationContext(), ServiceGPS.class);
                stopService(i);
                statutService.setText("Statut service GPS: désactivé");
                System.out.println("Service arrêté");
                buttonStart.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.INVISIBLE);
            }
        });

    }

    private boolean runtime_permissions()
    {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }

        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100)
        {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED))
            {
                enable_buttons();
            }
            else
            {
                runtime_permissions();
            }
        }
    }
}
