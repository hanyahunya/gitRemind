package com.hanyahunya.gitRemind.token.repository;

import com.hanyahunya.gitRemind.token.entity.Token;

import java.util.Optional;

public interface TokenRepository {
    boolean saveToken(Token token);

//    Optional<Token> findTokenByTokenId(String tokenId);

    boolean updateToken(Token token);

    Optional<Token> findByTokenId(String tokenId);

    boolean deleteByTokenId(String tokenId);
}
