package com.senzzzi.shq.repository;

import com.senzzzi.shq.model.persistence.QuoteEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends CrudRepository<QuoteEntity, Long> {

}
