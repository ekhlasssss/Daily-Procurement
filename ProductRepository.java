package com.daily.procurement.repository;

import com.daily.procurement.model.Product;

public class ProductRepository extends BaseFileRepository<Product> {

    public ProductRepository(String dataDir) {
        super(dataDir + "/products.csv");
    }

    @Override
    protected Product deserialize(String line) {
        try {
            String[] parts = line.split(",", 4);
            return new Product(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim()
            );
        } catch (Exception e) {
            System.err.println("Could not parse product line: " + line);
            return null;
        }
    }

    @Override
    protected String serialize(Product p) {
        return p.getId() + "," + escape(p.getName()) + "," +
               escape(p.getCategory()) + "," + escape(p.getUnit());
    }

    @Override
    protected int getId(Product entity) { return entity.getId(); }

    @Override
    protected void setId(Product entity, int id) { entity.setId(id); }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
