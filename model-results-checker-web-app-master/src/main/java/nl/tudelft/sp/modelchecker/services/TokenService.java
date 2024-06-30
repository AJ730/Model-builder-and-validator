package nl.tudelft.sp.modelchecker.services;

import nl.tudelft.sp.modelchecker.entities.User;

public interface TokenService {

    /**
     * Parse token.
     *
     * @param token token
     * @return parsed token
     * @throws Exception Exception
     */
    User parseToken(String token) throws Exception;
}
