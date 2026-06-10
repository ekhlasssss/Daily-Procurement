package com.daily.procurement.repository;

import com.daily.procurement.model.Supplier;

public class SupplierRepository extends BaseFileRepository<Supplier> {

    public SupplierRepository(String dataDir) {
        super(dataDir + "/suppliers.csv");
    }

    @Override
    protected Supplier deserialize(String line) {
        try {
            String[] parts = line.split(",", 3);
            return new Supplier(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim()
            );
        } catch (Exception e) {
            System.err.println("Could not parse supplier line: " + line);
            return null;
        }
    }

    @Override
    protected String serialize(Supplier s) {
        return s.getId() + "," + escape(s.getName()) + "," + escape(s.getContactInfo());
    }

    @Override
    protected int getId(Supplier entity) { return entity.getId(); }

    @Override
    protected void setId(Supplier entity, int id) { entity.setId(id); }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", ";");
    }
}
