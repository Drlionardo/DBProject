package com.example.dbproject.repos;

import com.example.dbproject.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepo extends CrudRepository<Message,Long> {
    List<Message> findByTag(String filter);
}
