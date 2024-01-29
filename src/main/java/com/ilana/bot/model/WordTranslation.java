package com.ilana.bot.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "word_translation")
public class WordTranslation {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "russian_word_id", referencedColumnName = "id")
    private RussianWord russianWord;
    @ManyToOne
    @JoinColumn(name = "english_word_id", referencedColumnName = "id")
    private EnglishWord englishWord;

}
