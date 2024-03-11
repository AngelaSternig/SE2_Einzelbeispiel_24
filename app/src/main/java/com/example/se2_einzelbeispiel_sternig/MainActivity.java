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
    private Button sendenButton, berechnenButton;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrikelnummerEditText = findViewById(R.id.Matrikelnumber);
        ausgabeTextView = findViewById(R.id.Ausgabe);
        sendenButton = findViewById(R.id.Senden);
        berechnenButton = findViewById(R.id.Berechnen);

        executorService = Executors.newSingleThreadExecutor();

        sendenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToServer(matrikelnummerEditText.getText().toString());
            }
        });

        berechnenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modifiedMatrikelNummer = replaceEverySecondDigit(matrikelnummerEditText.getText().toString());
                matrikelnummerEditText.setText(modifiedMatrikelNummer);
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

    private String replaceEverySecondDigit(String matrikelNummer) {
        StringBuilder modifiedString = new StringBuilder();
        for (int i = 0; i < matrikelNummer.length(); i++) {
            if (i % 2 == 1) { // Für jede zweite Ziffer
                int num = Character.getNumericValue(matrikelNummer.charAt(i));
                char replaceChar = (char) ('a' + num); // Umwandeln von 0-9 zu a-j
                modifiedString.append(replaceChar);
            } else {
                modifiedString.append(matrikelNummer.charAt(i));
            }
        }
        return modifiedString.toString();
    }
}