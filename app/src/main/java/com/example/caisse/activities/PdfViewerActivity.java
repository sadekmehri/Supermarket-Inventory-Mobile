package com.example.caisse.activities;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.caisse.R;
import com.example.caisse.singletons.SharedInvoiceSingleton;
import com.example.caisse.utils.ConstantsUtils;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.nio.file.Path;

public class PdfViewerActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        PDFView pdfView = findViewById(R.id.pdfView);

        Path path = new File(ConstantsUtils.INVOICE_FOLDER).toPath();
        String invoiceNumber = SharedInvoiceSingleton.getInstance(PdfViewerActivity.this).getInvoiceNumber();
        String filePath = path + "/" + invoiceNumber + ".pdf";

        pdfView.fromFile(new File(filePath)) // all pages are displayed by default
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                .spacing(0) // spacing between pages in dp. To define spacing color, set view background
                .load();
    }
}