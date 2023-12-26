package com.attackervictim.firebasenotification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String TAGTOKEN ="FIREBASE_TOKEN";
    private static final String SERVER_KEY = "AAAACi9DFPg:APA91bEgig-o_t-K9_VHhrPXOQnpMHe_B485BDQ7dcpPEyCHGYgowx0ijRw82hvWWk7H-TTdt66AAO6WumweJT3CgS1MNQQ8ME7i9NMc2syHzNtCxScMubB5zSJZRrDxMfP93kL5pgi9";
    private static  String DESTINO=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        obtenerToken();

        // Encuentra tu botón basado en su ID
        Button button = findViewById(R.id.btnLanzarNotificacion);
        final EditText titulotexto = findViewById(R.id.editTextText);
        final EditText bodytexto = findViewById(R.id.editTextText2);
        // Establece un OnClickListener para manejar los clics
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes poner el código que quieres que se ejecute cuando se haga clic en el botón
                String to = getDESTINO();
                String title =titulotexto.getText().toString();
                String body = bodytexto.getText().toString();
                sendNotification(to, title, body);
            }
        });

    }
    public void sendNotification(String to, String title, String body) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject notification = new JSONObject();
                    notification.put("title", title);
                    notification.put("body", body);

                    JSONObject message = new JSONObject();
                    message.put("to", to);
                    message.put("priority", "high");
                    message.put("notification", notification);

                    conn.setDoOutput(true);

                    DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                    outputStream.writeBytes(message.toString());
                    outputStream.flush();
                    outputStream.close();

                    int responseCode = conn.getResponseCode();
                    System.out.println("\nSending 'POST' request to URL : " + url);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }
    public void  obtenerToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAGTOKEN, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        setDESTINO(token);
                        String tokenGuardado = getSharedPreferences(Constantes.SP_FILE,0)
                                .getString(Constantes.SP_KEY_DEVICEID,null);
                       /* if (!token.equals(tokenGuardado)) {
                            //registramos el token en el servidor
                            DeviceManager.postRegistrarDispositivoEnServidor(token, getApplicationContext());
                        }*/
                        // Log and toast
                        String msg = "FCM Registration Token: " + token;
                        Log.d(TAGTOKEN, msg);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    }
                });
    }
    public static String getDESTINO() {
        return DESTINO;
    }

    public static void setDESTINO(String DESTINO) {
        MainActivity.DESTINO = DESTINO;
    }
}
