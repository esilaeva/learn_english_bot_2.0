package com.ilana.bot.repository;

import com.ilana.bot.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

    UserProgress findByChatIdAndWordIdAndLanguage(@Param("chatId") Long chatId, @Param("wordId") Long wordId, @Param("language") String language);

    @Query(value = "SELECT word_counter FROM user_progress WHERE language = :language and word_id = :wordId", nativeQuery = true)
    Long findCounterByLanguageAndWordId(@Param("language") String language, @Param("wordId") Long wordId);

    @Query(value = "SELECT word_id FROM user_progress WHERE chat_id = :chatId", nativeQuery = true)
    List<Long> findWordIdByChatId(@Param("chatId") Long chatId);

    @Query("SELECT up FROM UserProgress up WHERE up.language = :language and up.wordId = :wordId")
    List<UserProgress> findByLanguageAndWordId(@Param("language") String language, @Param("wordId") Long wordId);

    @Query("SELECT up FROM UserProgress up WHERE up.chatId = :chatId")
    List<UserProgress> findByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE UserProgress up SET up.wordCounter = :wordCounter WHERE up.language = :language and up.wordId = :wordId")
    void updateCountWordByLanguageAndWordId(@Param("wordCounter") Long wordCounter, @Param("language") String language, @Param("wordId") Long wordId);

    @Modifying
    @Transactional
    @Query("UPDATE UserProgress up SET up.wordCounter = 0 WHERE up.language = :language and up.chatId = :chatId")
    void updateWordCounterEnByLanguageAndChatId(@Param("language") String language, @Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE UserProgress up SET up.wordCounter = 0 WHERE up.language = :language and up.chatId = :chatId")
    void updateWordCounterRuByLanguageAndChatId(@Param("language") String language, @Param("chatId") Long chatId);

}
