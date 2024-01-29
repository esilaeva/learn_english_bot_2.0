package com.ilana.bot.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "topic")
public class Topic {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String topic;
}
