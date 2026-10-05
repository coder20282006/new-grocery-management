package com.grocery.system.service;

import com.grocery.system.entity.Bill;
import com.grocery.system.entity.BillItem;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generatePdfReceipt(Bill bill) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("GROCERY MANAGEMENT SYSTEM")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20));
            document.add(new Paragraph("Invoice Receipt")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Bill ID: " + bill.getId()));
            document.add(new Paragraph("Date: " + bill.getBillDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))));
            if (bill.getCustomer() != null) {
                document.add(new Paragraph("Customer: " + bill.getCustomer().getName()));
                document.add(new Paragraph("Phone: " + bill.getCustomer().getPhone()));
            }

            document.add(new Paragraph("\n"));

            float[] columnWidths = {200F, 50F, 100F, 100F, 100F};
            Table table = new Table(columnWidths);

            table.addHeaderCell(new Cell().add(new Paragraph("Item").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Qty").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("MRP").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

            for (BillItem item : bill.getBillItems()) {
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getName())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getMrp()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getFinalPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getFinalPrice() * item.getQuantity()))));
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph(String.format("Subtotal: %.2f", bill.getSubtotal())).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph(String.format("Discount: %.2f", bill.getTotalDiscount())).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph(String.format("Final Amount: %.2f", bill.getFinalAmount())).setBold().setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
