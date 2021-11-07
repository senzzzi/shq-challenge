package com.senzzzi.shq.repository;

import com.senzzzi.shq.model.persistence.QuoteEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends CrudRepository<QuoteEntity, Long> {

    List<QuoteEntity> findAllByAvailable(Boolean available);
}
