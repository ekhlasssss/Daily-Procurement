package com.daily.procurement.repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class BaseFileRepository<T> implements Repository<T> {
    protected final String filePath;
    protected final List<T> entities = new ArrayList<>();
    protected int nextId = 1;

    protected BaseFileRepository(String filePath) {
        this.filePath = filePath;
        ensureDirectoryExists();
        loadFromFile();
    }

    private void ensureDirectoryExists() {
        try {
            Path dir = Paths.get(filePath).getParent();
            if (dir != null) Files.createDirectories(dir);
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
    }

    protected void loadFromFile() {
        entities.clear();
        File file = new File(filePath);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    T entity = deserialize(line);
                    if (entity != null) {
                        entities.add(entity);
                        int id = getId(entity);
                        if (id >= nextId) nextId = id + 1;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load from " + filePath + ": " + e.getMessage());
        }
    }

    protected void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (T entity : entities) {
                writer.println(serialize(entity));
            }
        } catch (IOException e) {
            System.err.println("Could not save to " + filePath + ": " + e.getMessage());
        }
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(entities);
    }

    @Override
    public Optional<T> findById(int id) {
        return entities.stream().filter(e -> getId(e) == id).findFirst();
    }

    @Override
    public T save(T entity) {
        int id = getId(entity);
        if (id == 0) {
            setId(entity, nextId++);
            entities.add(entity);
        } else {
            for (int i = 0; i < entities.size(); i++) {
                if (getId(entities.get(i)) == id) {
                    entities.set(i, entity);
                    saveToFile();
                    return entity;
                }
            }
            entities.add(entity);
        }
        saveToFile();
        return entity;
    }

    @Override
    public boolean deleteById(int id) {
        boolean removed = entities.removeIf(e -> getId(e) == id);
        if (removed) saveToFile();
        return removed;
    }

    protected abstract T deserialize(String line);
    protected abstract String serialize(T entity);
    protected abstract int getId(T entity);
    protected abstract void setId(T entity, int id);
}
