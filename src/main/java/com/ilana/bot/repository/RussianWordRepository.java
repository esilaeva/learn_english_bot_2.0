package com.ilana.bot.repository;

import com.ilana.bot.model.EnglishWord;
import com.ilana.bot.model.RussianWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RussianWordRepository extends JpaRepository<RussianWord, Long> {

    @Query("SELECT rw FROM RussianWord rw ORDER BY RANDOM() LIMIT 1")
    RussianWord findRandomWord();

    @Query("SELECT wt.englishWord FROM WordTranslation wt WHERE wt.russianWord.id = :russianWordId")
    List<EnglishWord> findTranslationsByRussianWordId(@Param("russianWordId") Long russianWordId);

    @Query("SELECT rw.id FROM RussianWord rw WHERE rw.word = :russianWord")
    Long findWordIdByWord(@Param("russianWord") String russianWord);


}
