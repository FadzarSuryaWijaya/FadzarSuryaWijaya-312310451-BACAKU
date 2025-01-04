package com.example.testfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;

public class ReaderActivity extends AppCompatActivity {
    private PDFView pdfView;
    private ProgressBar pbLoading;
    private TextView tvPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        pdfView = findViewById(R.id.pdfView);
        pbLoading = findViewById(R.id.pb_loading);
        tvPages = findViewById(R.id.tv_pages);

        String url = "https://tubianto.com/sertifikat_android_pemula.pdf";
        new DownloadPdfTask().execute(url);
    }

    private class DownloadPdfTask extends AsyncTask<String, Void, File> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected File doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                File outputFile = new File(getCacheDir(), "downloaded.pdf");
                FileOutputStream output = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                output.close();
                input.close();
                return outputFile;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            pbLoading.setVisibility(View.GONE);
            if (file != null) {
                pdfView.fromFile(file)
                        .onPageChange((page, pageCount) -> tvPages.setText((page + 1) + "/" + pageCount))
                        .load();
            } else {
                Toast.makeText(ReaderActivity.this, "Failed to download PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        pdfView.recycle();
        super.onDestroy();
    }
}
