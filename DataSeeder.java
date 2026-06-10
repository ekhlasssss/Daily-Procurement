package com.daily.procurement.service;

import com.daily.procurement.model.*;
import com.daily.procurement.repository.*;

import java.time.LocalDate;

public class DataSeeder {
    private final ProductRepository productRepo;
    private final SupplierRepository supplierRepo;
    private final PurchaseRecordRepository recordRepo;

    public DataSeeder(ProductRepository productRepo, SupplierRepository supplierRepo,
                      PurchaseRecordRepository recordRepo) {
        this.productRepo = productRepo;
        this.supplierRepo = supplierRepo;
        this.recordRepo = recordRepo;
    }

    public void seedIfEmpty() {
        if (!productRepo.findAll().isEmpty()) return;

        // Products
        Product[] products = {
            new Product(0, "Tomatoes", "Vegetables", "kg"),
            new Product(0, "Cucumbers", "Vegetables", "kg"),
            new Product(0, "Potatoes", "Vegetables", "kg"),
            new Product(0, "Onions", "Vegetables", "kg"),
            new Product(0, "Whole Milk", "Dairy", "litre"),
            new Product(0, "Butter", "Dairy", "kg"),
            new Product(0, "Cheddar Cheese", "Dairy", "kg"),
            new Product(0, "Chicken Breast", "Meat", "kg"),
            new Product(0, "Beef Mince", "Meat", "kg"),
            new Product(0, "Pork Ribs", "Meat", "kg"),
            new Product(0, "Flour", "Dry Goods", "kg"),
            new Product(0, "Olive Oil", "Dry Goods", "litre")
        };
        for (Product p : products) productRepo.save(p);

        // Suppliers
        Supplier[] suppliers = {
            new Supplier(0, "Green Valley Farm", "+371 2234 5678"),
            new Supplier(0, "Baltic Dairy Co.", "info@balticdairy.lv"),
            new Supplier(0, "City Meat Supply", "+371 2987 6543")
        };
        for (Supplier s : suppliers) supplierRepo.save(s);

        // Purchase Records — seeded across last 14 days
        LocalDate today = LocalDate.now();
        int[] prodIds = {1, 2, 3, 5, 6, 8, 9, 11, 12, 4, 7, 10, 1, 3, 8, 5, 11, 2, 9, 6};
        int[] suppIds = {1, 1, 1, 2, 2, 3, 3, 1, 1, 1, 2, 3, 1, 1, 3, 2, 1, 1, 3, 2};
        double[] qtys = {10, 8, 20, 15, 3, 12, 8, 25, 6, 5, 4, 11, 9, 18, 7, 12, 30, 14, 5, 3};
        double[] prices = {1.20, 0.90, 0.60, 0.95, 2.80, 7.50, 5.60, 2.10, 4.20, 3.10, 1.30, 8.90, 1.20, 0.60, 7.50, 0.95, 1.30, 0.90, 4.20, 2.80};

        for (int i = 0; i < prodIds.length; i++) {
            int daysAgo = i / 2; // 2 records per day going back
            LocalDate date = today.minusDays(daysAgo);

            Product product = productRepo.findById(prodIds[i]).orElse(null);
            Supplier supplier = supplierRepo.findById(suppIds[i]).orElse(null);
            if (product == null || supplier == null) continue;

            PurchaseRecord record = new PurchaseRecord(0, product, supplier, qtys[i], prices[i], date);
            recordRepo.save(record);
        }
    }
}
