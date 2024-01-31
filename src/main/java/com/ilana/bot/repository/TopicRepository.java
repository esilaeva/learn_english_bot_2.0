package com.ilana.bot.repository;

import com.ilana.bot.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

    Topic findTopicById(Long id);

    Long findTopicIdByTopic(String topic);

    @Query(value = "SELECT topic FROM Topic", nativeQuery = true)
    List<String> findAllTopic();

}
