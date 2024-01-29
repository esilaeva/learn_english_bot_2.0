package com.ilana.bot.repository;

import com.ilana.bot.model.User;
import com.ilana.bot.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    @Query("SELECT up.wordCounter FROM UserProgress up WHERE up.language = :language and up.wordId = :wordId")
    Long findCounterByLanguageAndWordId(@Param("language") String language, @Param("wordId") Long wordId);


    @Query("SELECT up FROM UserProgress up WHERE up.language = :language and up.wordId = :wordId")
    List<UserProgress> findByLanguageAndWordId(@Param("language") String language, @Param("wordId") Long wordId);

    @Query("SELECT up FROM UserProgress up WHERE up.chatId = :chatId")
    List<UserProgress> findByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE UserProgress up SET up.wordCounter = :wordCounter WHERE up.language = :language and up.wordId = :wordId")
    void updateCountWordByLanguageAndWordId(@Param("wordCounter") Long wordCounter, @Param("language") String language, @Param("wordId") Long wordId);


}
