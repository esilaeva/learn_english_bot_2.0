package com.ilana.bot.repository;

import com.ilana.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserDataByChatId(long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.choice = :choice WHERE u.chatId = :chatId")
    void updateChoiceByChatId(@Param("choice") String choice, @Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.language = :language WHERE u.chatId = :chatId")
    void updateLanguageByChatId(@Param("language") String language, @Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastRuWordId = :lastRuWordId WHERE u.chatId = :chatId")
    void updateLastRussianWordIdByChatId(@Param("lastRuWordId") long lastRuWordId, @Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastEnWordId = :lastEnWordId WHERE u.chatId = :chatId")
    void updateLastEnglishWordIdByChatId(@Param("lastEnWordId") long lastEnWordId, @Param("chatId") Long chatId);


}
