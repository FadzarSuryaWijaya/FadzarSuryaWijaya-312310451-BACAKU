package com.example.testfirebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.io.IOException;

public class PdfRendererBasicFragment extends Fragment {

    private ViewPager mViewPager;
    private PdfRenderer mPdfRenderer;
    private ParcelFileDescriptor mFileDescriptor;
    private ProgressBar mProgressBar;
    private TextView mTextViewPages;

    private static final String TAG = "PdfRendererFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reader, container, false);
        mViewPager = view.findViewById(R.id.viewPager);
        mProgressBar = view.findViewById(R.id.pb_loading);
        mTextViewPages = view.findViewById(R.id.tv_pages);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            String pdfFilePath = getArguments().getString("PDF_FILE_PATH");
            Log.d(TAG, "PDF File Path: " + pdfFilePath);
            openRenderer(view.getContext(), pdfFilePath);
            PdfPagerAdapter adapter = new PdfPagerAdapter();
            mViewPager.setAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
            mTextViewPages.setText(String.format("Page 1 of %d", mPdfRenderer.getPageCount()));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(view.getContext(), "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openRenderer(Context context, String pdfFilePath) throws IOException {
        mFileDescriptor = ParcelFileDescriptor.open(new File(pdfFilePath), ParcelFileDescriptor.MODE_READ_ONLY);
        mPdfRenderer = new PdfRenderer(mFileDescriptor);
    }

    @Override
    public void onDestroy() {
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void closeRenderer() throws IOException {
        if (mPdfRenderer != null) {
            mPdfRenderer.close();
        }
        if (mFileDescriptor != null) {
            mFileDescriptor.close();
        }
    }

    private class PdfPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPdfRenderer.getPageCount();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Context context = container.getContext();
            ImageView imageView = new ImageView(context);
            imageView.setContentDescription("Page " + (position + 1));
            container.addView(imageView);
            showPage(position, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    private void showPage(int index, ImageView imageView) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        PdfRenderer.Page page = mPdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        imageView.setImageBitmap(bitmap);
        page.close();

        // Update page number display
        mTextViewPages.setText(String.format("Page %d of %d", index + 1, mPdfRenderer.getPageCount()));
    }
}
