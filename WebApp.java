package com.daily.procurement;

import com.daily.procurement.repository.*;
import com.daily.procurement.service.*;
import com.daily.procurement.web.ApiServer;

import java.io.IOException;

/**
 * Entry point for the web version of the Daily Procurement System.
 *
 * Reuses the same model / repository / service layers as the desktop
 * version; only the presentation layer changed from Swing to a web
 * frontend served over HTTP.
 *
 * The listening port is taken from the PORT environment variable when
 * present (free hosting platforms such as Render, Railway and Koyeb set
 * it automatically), and falls back to 8080 for local development.
 */
public class WebApp {
    public static void main(String[] args) throws IOException {
        String dataDir = System.getenv().getOrDefault("DATA_DIR", "data");

        ProductRepository productRepo = new ProductRepository(dataDir);
        SupplierRepository supplierRepo = new SupplierRepository(dataDir);
        PurchaseRecordRepository recordRepo =
            new PurchaseRecordRepository(dataDir, productRepo, supplierRepo);

        ProcurementService service =
            new ProcurementService(productRepo, supplierRepo, recordRepo);

        new DataSeeder(productRepo, supplierRepo, recordRepo).seedIfEmpty();

        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try { port = Integer.parseInt(envPort.trim()); } catch (NumberFormatException ignored) {}
        }

        new ApiServer(service, port).start();
    }
}
