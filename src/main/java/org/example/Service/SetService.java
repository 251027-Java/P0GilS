package org.example.Service;

import org.example.Repository.ISetRepository;
import org.example.Repository.JdbcSetRepository;
import org.example.model.Set;

import java.util.List;

public class SetService {
    private final ISetRepository setRepo;

    public SetService(ISetRepository setRepo) {
        this.setRepo = setRepo;
    }

    public List<Set> getAllSets() {
        return setRepo.findAll();
    }

}
