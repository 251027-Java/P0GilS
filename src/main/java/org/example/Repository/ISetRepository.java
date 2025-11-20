package org.example.Repository;

import org.example.model.Set;
import java.util.List;

public interface ISetRepository {
    Set save(Set set);
    List<Set> findAll();
}
