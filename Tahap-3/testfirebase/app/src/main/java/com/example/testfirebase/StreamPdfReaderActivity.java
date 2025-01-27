package com.example.testfirebase;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamPdfReaderActivity extends AppCompatActivity {

    private static final String TAG = "StreamPdfReaderActivity";
    private PDFView pdfView;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = findViewById(R.id.pdfView);
        String pdfUrl = getIntent().getStringExtra("PDF_URL");

        Log.d(TAG, "PDF URL in StreamPdfReaderActivity: " + pdfUrl);

        if (pdfUrl == null || pdfUrl.isEmpty()) {
            Log.e(TAG, "PDF URL is null or empty");
            Toast.makeText(this, "PDF URL is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService = Executors.newFixedThreadPool(2);

        fetchPdf(pdfUrl);
    }

    private void fetchPdf(String pdfUrl) {
        if (pdfUrl == null || pdfUrl.isEmpty()) {
            Log.e(TAG, "PDF URL is null or empty inside fetchPdf");
            showError();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pdfUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching PDF", e);
                runOnUiThread(() -> showError());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream inputStream = response.body().byteStream();
                    if (inputStream != null) {
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        runOnUiThread(() -> loadPdf(bufferedInputStream));
                    } else {
                        Log.e(TAG, "InputStream is null");
                        runOnUiThread(() -> showError());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch PDF: " + response.code());
                    runOnUiThread(() -> showError());
                }
            }
        });
    }

    private void loadPdf(InputStream inputStream) {
        pdfView.fromStream(inputStream)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(StreamPdfReaderActivity.this))
                .spacing(10)
                .load();
    }

    private void showError() {
        Toast.makeText(StreamPdfReaderActivity.this, "Failed to load PDF. Please try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
