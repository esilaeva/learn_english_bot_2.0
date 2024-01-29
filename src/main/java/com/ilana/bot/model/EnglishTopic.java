package com.ilana.bot.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "english_topic")
public class EnglishTopic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int topicId;
    private int enWordId;
}
