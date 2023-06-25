package com.example.springbatch.repo;


import com.example.springbatch.model.TranstemplateEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranstemplateRepo extends MongoRepository<TranstemplateEntity,Void> {
}
