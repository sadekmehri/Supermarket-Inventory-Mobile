package com.example.caisse.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.caisse.models.Cart;
import com.example.caisse.models.Product;
import com.example.caisse.singletons.SharedInvoiceSingleton;
import com.example.caisse.singletons.ToastSingleton;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

public class InvoiceGeneratorUtils {

    private final String invoiceNumber;

    public InvoiceGeneratorUtils(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    private PdfPCell getIRHCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 16);
        /*	font.setColor(BaseColor.GRAY);*/
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private PdfPCell getIRDCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell getBillHeaderCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 11);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        return cell;
    }

    private PdfPCell getBillRowCell(String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5.0f);
        cell.setBorderWidthBottom(0);
        cell.setBorderWidthTop(0);
        return cell;
    }

    private PdfPCell getAccountsCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setPadding(5.0f);
        return cell;
    }

    private PdfPCell getAccountsCellR(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthTop(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5.0f);
        cell.setPaddingRight(20.0f);
        return cell;
    }

    private PdfPCell getDescCell(String text) {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(text);
        PdfPCell cell = new PdfPCell(phrase);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(0);
        return cell;
    }

    @NonNull
    private File getStorageDirectory() {
        File file = new File(ConstantsUtils.INVOICE_FOLDER);

        if (!file.exists()) file.mkdirs();

        return file;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void generateInvoice(Context context, Cart cart) {
        ToastSingleton toastSingleton = ToastSingleton.getInstance(context);

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            toastSingleton.toast("External Storage not available or you don't have permission to write", Toast.LENGTH_LONG);
            return;
        }

        try {
            createPDF(context, cart);
            toastSingleton.toast("Invoice pdf is located in download folder", Toast.LENGTH_LONG);
        } catch (Exception e) {
            Log.e("err", e.toString());
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createPDF(Context context, Cart cart) throws Exception {

        if (cart.getSize() == 0) return;

        String today = LocalDate.now().toString();
        ArrayList<Product> lstProduct = cart.getCart();
        float total = cart.calculateTotal();
        float tax = total * ConstantsUtils.TVA;
        float totalWithTax = total + tax;
        int index = 1;

        // path for the PDF file in the external storage
        Path path = getStorageDirectory().toPath();
        String filePath = path + "/" + invoiceNumber + ".pdf";

        OutputStream file = new FileOutputStream(filePath);
        Document document = new Document();
        PdfWriter.getInstance(document, file);

        //Inserting Image in PDF
        //Header Image
        InputStream inputStream = context.getAssets().open("company_logo.png");
        Bitmap bmp = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Image image = Image.getInstance(stream.toByteArray());
        image.scaleAbsolute(150f, 100f);

        // Invoice header
        PdfPTable irdTable = new PdfPTable(2);
        irdTable.setWidths(new float[]{2.5f, 2f});
        irdTable.addCell(getIRDCell("Invoice No"));
        irdTable.addCell(getIRDCell("Invoice Date"));
        irdTable.addCell(getIRDCell(invoiceNumber)); // pass invoice number
        irdTable.addCell(getIRDCell(today)); // pass invoice date

        PdfPTable irhTable = new PdfPTable(3);
        irhTable.setWidthPercentage(100);

        irhTable.addCell(getIRHCell(""));
        irhTable.addCell(getIRHCell(""));
        irhTable.addCell(getIRHCell("Invoice"));
        irhTable.addCell(getIRHCell(""));
        irhTable.addCell(getIRHCell(""));
        PdfPCell invoiceTable = new PdfPCell(irdTable);
        invoiceTable.setBorder(0);
        irhTable.addCell(invoiceTable);

        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 10, Font.BOLD);
        fs.addFont(font);

        PdfPTable billTable = new PdfPTable(6);
        billTable.setWidthPercentage(100);
        billTable.setWidths(new float[]{1.5f, 3f, 4f, 3f, 1f, 3f});
        billTable.setSpacingBefore(30.0f);
        billTable.addCell(getBillHeaderCell("Index"));
        billTable.addCell(getBillHeaderCell("Qrcode"));
        billTable.addCell(getBillHeaderCell("Product Name"));
        billTable.addCell(getBillHeaderCell("Unit Price"));
        billTable.addCell(getBillHeaderCell("Qty"));
        billTable.addCell(getBillHeaderCell("Amount"));

        for (Product product : lstProduct) {
            billTable.addCell(getBillRowCell(String.valueOf(index)));
            billTable.addCell(getBillRowCell(product.getQrCode()));
            billTable.addCell(getBillRowCell(product.getName()));
            billTable.addCell(getBillRowCell(product.getPrice() + " TND"));
            billTable.addCell(getBillRowCell(String.valueOf(product.getQuantity())));
            billTable.addCell(getBillRowCell(product.getTotal() + " TND"));
            index++;
        }

        billTable.addCell(getBillRowCell(" "));
        billTable.addCell(getBillRowCell(""));
        billTable.addCell(getBillRowCell(""));
        billTable.addCell(getBillRowCell(""));
        billTable.addCell(getBillRowCell(""));
        billTable.addCell(getBillRowCell(""));

        PdfPTable validity = new PdfPTable(1);
        validity.setWidthPercentage(100);
        validity.addCell(getValidityCell());
        validity.addCell(getValidityCell());
        validity.addCell(getValidityCell());
        validity.addCell(getValidityCell());
        PdfPCell summaryL = new PdfPCell(validity);
        summaryL.setColspan(2);
        summaryL.setPadding(1f);
        billTable.addCell(summaryL);

        PdfPTable accounts = new PdfPTable(2);
        accounts.setWidthPercentage(100);
        accounts.addCell(getAccountsCell("Subtotal"));
        accounts.addCell(getAccountsCellR(total + " TND"));
        accounts.addCell(getAccountsCell("Tax (" + ConstantsUtils.TVA * 100 + "%)"));
        accounts.addCell(getAccountsCellR(tax + " TND"));
        accounts.addCell(getAccountsCell("Total"));
        accounts.addCell(getAccountsCellR(totalWithTax + " TND"));
        PdfPCell summaryR = new PdfPCell(accounts);
        summaryR.setColspan(4);
        billTable.addCell(summaryR);

        PdfPTable describer = new PdfPTable(1);
        describer.setWidthPercentage(100);
        describer.addCell(getDescCell(" "));
        describer.addCell(getDescCell("Goods once sold will not be taken back or exchanged || Subject to product justification || Product damage no one responsible || "
                + " Service only at concerned authorized service centers"));

        document.open(); //PDF document opened........

        document.add(image);
        document.add(irhTable);
        document.add(billTable);
        document.add(describer);

        document.close();

        file.close();
        SharedInvoiceSingleton.getInstance(context).save(invoiceNumber);
    }

    private PdfPCell getValidityCell() {
        FontSelector fs = new FontSelector();
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 10);
        font.setColor(BaseColor.GRAY);
        fs.addFont(font);
        Phrase phrase = fs.process(" ");
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(0);
        return cell;
    }

    private boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

}
