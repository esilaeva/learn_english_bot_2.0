package com.ilana.bot.repository;

import com.ilana.bot.model.EnglishWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnglishWordRepository extends JpaRepository<EnglishWord, Long> {

    @Query("SELECT ew FROM EnglishWord ew ORDER BY RANDOM() LIMIT 1")
    EnglishWord findRandomWord();

    @Query("SELECT ew.id FROM EnglishWord ew WHERE ew.word = :englishWord")
    Long findWordIdByWord(@Param("englishWord") String englishWord);

    @Query("SELECT ew FROM EnglishWord ew")
    List<EnglishWord> findEnglishWord();

}
