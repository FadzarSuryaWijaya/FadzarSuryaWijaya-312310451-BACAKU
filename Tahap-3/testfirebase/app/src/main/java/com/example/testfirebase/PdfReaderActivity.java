package com.example.testfirebase;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PdfReaderActivity extends AppCompatActivity {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final String TAG = "PdfReaderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        if (savedInstanceState == null) {
            String pdfUrl = getIntent().getStringExtra("PDF_URL");
            Log.d(TAG, "PDF URL: " + pdfUrl);

            // Download and render the PDF using Executor
            downloadPdf(pdfUrl);
        }
    }

    private void downloadPdf(String pdfUrl) {
        executor.execute(() -> {
            File file = downloadFile(pdfUrl);
            runOnUiThread(() -> handleDownloadedFile(file));
        });
    }

    private File downloadFile(String url) {
        try {
            File file = new File(getCacheDir(), "downloaded.pdf");
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(response.body().bytes());
                }
                return file;
            } else {
                Log.e(TAG, "Failed to download file: " + response.message());
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException while downloading file", e);
        }
        return null;
    }

    private void handleDownloadedFile(File file) {
        if (file != null) {
            PdfRendererBasicFragment fragment = new PdfRendererBasicFragment();
            Bundle args = new Bundle();
            args.putString("PDF_FILE_PATH", file.getAbsolutePath());
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            try {
                openRenderer(this, file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(PdfReaderActivity.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // Update PdfRendererBasicFragment to use the local file path
    private void openRenderer(Context context, String pdfFilePath) throws IOException {
        ParcelFileDescriptor mFileDescriptor = ParcelFileDescriptor.open(new File(pdfFilePath), ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer mPdfRenderer = new PdfRenderer(mFileDescriptor);
        // Add your page rendering logic here
    }
}
