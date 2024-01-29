package com.ilana.bot.repository;

import com.ilana.bot.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

    Topic findTopicById(int id);

}
