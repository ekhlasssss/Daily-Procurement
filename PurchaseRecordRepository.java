package com.daily.procurement.repository;

import com.daily.procurement.model.Product;
import com.daily.procurement.model.PurchaseRecord;
import com.daily.procurement.model.Supplier;

import java.time.LocalDate;

public class PurchaseRecordRepository extends BaseFileRepository<PurchaseRecord> {
    private final ProductRepository productRepo;
    private final SupplierRepository supplierRepo;

    public PurchaseRecordRepository(String dataDir,
                                    ProductRepository productRepo,
                                    SupplierRepository supplierRepo) {
        super(dataDir + "/purchase_records.csv");
        this.productRepo = productRepo;
        this.supplierRepo = supplierRepo;
        // Reload now that productRepo and supplierRepo are set
        loadFromFile();
    }

    @Override
    protected PurchaseRecord deserialize(String line) {
        try {
            String[] parts = line.split(",", 6);
            int id = Integer.parseInt(parts[0].trim());
            int productId = Integer.parseInt(parts[1].trim());
            int supplierId = Integer.parseInt(parts[2].trim());
            double quantity = Double.parseDouble(parts[3].trim());
            double unitPrice = Double.parseDouble(parts[4].trim());
            LocalDate date = LocalDate.parse(parts[5].trim());

            if (productRepo == null || supplierRepo == null) return null;

            Product product = productRepo.findById(productId).orElse(null);
            Supplier supplier = supplierRepo.findById(supplierId).orElse(null);
            if (product == null || supplier == null) return null;

            return new PurchaseRecord(id, product, supplier, quantity, unitPrice, date);
        } catch (Exception e) {
            System.err.println("Could not parse purchase record: " + line);
            return null;
        }
    }

    @Override
    protected String serialize(PurchaseRecord r) {
        return r.getId() + "," +
               r.getProduct().getId() + "," +
               r.getSupplier().getId() + "," +
               r.getQuantity() + "," +
               r.getUnitPrice() + "," +
               r.getPurchaseDate().toString();
    }

    @Override
    protected int getId(PurchaseRecord entity) { return entity.getId(); }

    @Override
    protected void setId(PurchaseRecord entity, int id) { entity.setId(id); }
}
