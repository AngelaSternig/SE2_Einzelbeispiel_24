package com.example.se2_einzelbeispiel_sternig;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText matrikelnummerEditText;
    private TextView ausgabeTextView;
    private Button sendenButton;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrikelnummerEditText = findViewById(R.id.Matrikelnumber);
        ausgabeTextView = findViewById(R.id.Ausgabe);
        sendenButton = findViewById(R.id.Senden);

        executorService = Executors.newSingleThreadExecutor();

        sendenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToServer(matrikelnummerEditText.getText().toString());
            }
        });
    }

    private void sendToServer(final String matrikelNummer) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("se2-submission.aau.at", 20080);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    // Sende Matrikelnummer
                    out.println(matrikelNummer);

                    // Lies Antwort vom Server
                    final String response = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ausgabeTextView.setText(response);
                        }
                    });

                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ausgabeTextView.setText("Fehler bei der Kommunikation mit dem Server.");
                        }
                    });
                }
            }
        });
    }
}