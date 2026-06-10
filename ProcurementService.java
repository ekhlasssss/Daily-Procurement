package com.daily.procurement.service;

import com.daily.procurement.model.*;
import com.daily.procurement.repository.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ProcurementService {
    private final ProductRepository productRepo;
    private final SupplierRepository supplierRepo;
    private final PurchaseRecordRepository recordRepo;

    public ProcurementService(ProductRepository productRepo,
                               SupplierRepository supplierRepo,
                               PurchaseRecordRepository recordRepo) {
        this.productRepo = productRepo;
        this.supplierRepo = supplierRepo;
        this.recordRepo = recordRepo;
    }

    // ---- Products ----
    public List<Product> getAllProducts() { return productRepo.findAll(); }

    public String validateProduct(String name, String category, String unit) {
        if (name == null || name.trim().isEmpty()) return "Product name is required.";
        if (category == null || category.trim().isEmpty()) return "Category is required.";
        if (unit == null || unit.trim().isEmpty()) return "Unit is required.";
        return null;
    }

    public Product saveProduct(Product p) { return productRepo.save(p); }

    public boolean deleteProduct(int id) {
        boolean usedInRecord = recordRepo.findAll().stream()
            .anyMatch(r -> r.getProduct().getId() == id);
        if (usedInRecord) return false;
        return productRepo.deleteById(id);
    }

    // ---- Suppliers ----
    public List<Supplier> getAllSuppliers() { return supplierRepo.findAll(); }

    public String validateSupplier(String name, String contactInfo) {
        if (name == null || name.trim().isEmpty()) return "Supplier name is required.";
        if (contactInfo == null || contactInfo.trim().isEmpty()) return "Contact info is required.";
        return null;
    }

    public Supplier saveSupplier(Supplier s) { return supplierRepo.save(s); }

    public boolean deleteSupplier(int id) {
        boolean usedInRecord = recordRepo.findAll().stream()
            .anyMatch(r -> r.getSupplier().getId() == id);
        if (usedInRecord) return false;
        return supplierRepo.deleteById(id);
    }

    // ---- Purchase Records ----
    public List<PurchaseRecord> getAllRecords() { return recordRepo.findAll(); }

    public List<PurchaseRecord> getRecordsByDateRange(LocalDate from, LocalDate to) {
        return recordRepo.findAll().stream()
            .filter(r -> !r.getPurchaseDate().isBefore(from) && !r.getPurchaseDate().isAfter(to))
            .collect(Collectors.toList());
    }

    public List<PurchaseRecord> getRecordsByCategory(String category) {
        return recordRepo.findAll().stream()
            .filter(r -> r.getProduct().getCategory().equals(category))
            .collect(Collectors.toList());
    }

    public List<PurchaseRecord> getRecordsByDateRangeAndCategory(LocalDate from, LocalDate to, String category) {
        return recordRepo.findAll().stream()
            .filter(r -> !r.getPurchaseDate().isBefore(from) && !r.getPurchaseDate().isAfter(to))
            .filter(r -> category == null || category.equals("All") || r.getProduct().getCategory().equals(category))
            .collect(Collectors.toList());
    }

    public String validatePurchaseRecord(Product product, Supplier supplier,
                                          String quantityStr, String unitPriceStr,
                                          LocalDate date) {
        if (product == null) return "Please select a product.";
        if (supplier == null) return "Please select a supplier.";

        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            return "Quantity must be a valid number.";
        }
        if (quantity <= 0) return "Quantity must be greater than zero.";

        double unitPrice;
        try {
            unitPrice = Double.parseDouble(unitPriceStr);
        } catch (NumberFormatException e) {
            return "Unit price must be a valid number.";
        }
        if (unitPrice <= 0) return "Unit price must be greater than zero.";

        if (date == null) return "Purchase date is required.";
        if (date.isAfter(LocalDate.now())) return "Purchase date cannot be in the future.";

        return null;
    }

    public PurchaseRecord savePurchaseRecord(PurchaseRecord record) {
        return recordRepo.save(record);
    }

    public boolean deletePurchaseRecord(int id) {
        return recordRepo.deleteById(id);
    }

    // ---- Reporting ----
    public double getDailyTotal(LocalDate date) {
        return recordRepo.findAll().stream()
            .filter(r -> r.getPurchaseDate().equals(date))
            .mapToDouble(PurchaseRecord::getTotalCost)
            .sum();
    }

    public Map<String, Double> getDailyTotalByCategory(LocalDate date) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Category cat : Category.values()) {
            result.put(cat.getDisplayName(), 0.0);
        }
        recordRepo.findAll().stream()
            .filter(r -> r.getPurchaseDate().equals(date))
            .forEach(r -> {
                String cat = r.getProduct().getCategory();
                result.merge(cat, r.getTotalCost(), Double::sum);
            });
        return result;
    }

    public double getTotalForRecords(List<PurchaseRecord> records) {
        return records.stream().mapToDouble(PurchaseRecord::getTotalCost).sum();
    }
}
