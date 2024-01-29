package com.ilana.bot.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "english_word")
public class EnglishWord {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    private String level;
    private String transcription;
    private String comment;

}
