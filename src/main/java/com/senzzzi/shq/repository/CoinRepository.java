package com.senzzzi.shq.repository;

import com.senzzzi.shq.model.persistence.CoinEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRepository extends CrudRepository<CoinEntity, Long> {

}
