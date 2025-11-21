package org.example.Service;

import org.example.Repository.JdbcSetRepository;
import org.example.model.Set;

import java.util.List;

public class SetService {

    private final JdbcSetRepository setRepo;

    public SetService(JdbcSetRepository setRepo) {
        this.setRepo = setRepo;
    }

    public List<Set> getAllSets() {
        return setRepo.findAll();
    }

    public Set getSetById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Set id cannot be empty.");
        }

        return setRepo.findAll()
                .stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst()
                .orElse(null);
    }

    public boolean setExists(String id) {
        return getSetById(id) != null;
    }
}
