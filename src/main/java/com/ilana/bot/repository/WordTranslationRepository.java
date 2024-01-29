package com.ilana.bot.repository;

import com.ilana.bot.model.EnglishWord;
import com.ilana.bot.model.RussianWord;
import com.ilana.bot.model.WordTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordTranslationRepository extends JpaRepository<WordTranslation, Long> {

    @Query("SELECT wt.englishWord FROM WordTranslation wt WHERE wt.russianWord.word = :word")
    List<EnglishWord> findEnglishTranslationsForRussianWord(@Param("word") String word);

    @Query("SELECT wt.russianWord FROM WordTranslation wt WHERE wt.englishWord.word = :word")
    List<RussianWord> findRussianTranslationsForEnglishWord(@Param("word") String word);



}
