package com.jobdev.dataharvest.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jobdev.dataharvest.model.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

}
