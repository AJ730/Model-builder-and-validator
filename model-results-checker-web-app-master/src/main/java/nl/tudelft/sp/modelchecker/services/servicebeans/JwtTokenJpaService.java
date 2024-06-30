package nl.tudelft.sp.modelchecker.services.servicebeans;

import static java.util.Base64.Decoder;
import static java.util.Base64.getDecoder;

import java.util.Arrays;
import java.util.HashMap;
import javassist.NotFoundException;
import nl.tudelft.sp.modelchecker.entities.User;
import nl.tudelft.sp.modelchecker.exceptions.AuthorityException;
import nl.tudelft.sp.modelchecker.exceptions.DateException;
import nl.tudelft.sp.modelchecker.exceptions.ExistsException;
import nl.tudelft.sp.modelchecker.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenJpaService implements TokenService {


    private final Decoder decoder;
    private final HashMap<String, String> hashMap;

    @Autowired
    AuthJpaService authJpaService;

    /**
     * Initialize a JwtTokenParser.
     */
    public JwtTokenJpaService() {
        this.decoder = getDecoder();
        this.hashMap = new HashMap<>();
    }

    /**
     * Parse token.
     *
     * @param token token
     * @return user
     * @throws DateException      DateException
     * @throws ExistsException    ExistsException
     * @throws NotFoundException  NotFoundException
     * @throws AuthorityException AuthorityException
     */
    public User parseToken(String token) throws DateException,
            ExistsException, NotFoundException, AuthorityException {
        decodeToken(token);

        String oid = hashMap.get("oid");
        String username = hashMap.get("name");
        String email = hashMap.get("preferred_username");

        return authJpaService.register(oid, username, email, isAdmin());
    }


    /**
     * Decode a token.
     *
     * @param token token.
     */
    protected void decodeToken(String token) {
        hashMap.clear();
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));

        String[] claims = payload
                .replace("{", "")
                .replace("}", "")
                .replace("[", "")
                .replace("]", "")
                .split(",");


        Arrays.stream(claims).map(claim -> claim.split(":")).forEach(keyValue -> {
            String key = keyValue[0].replace("\"", "");
            String value = keyValue[1].replace("\"", "");
            hashMap.put(key, value);
        });
    }

    /**
     * Is the currentUser an admin.
     *
     * @return boolean
     */
    protected boolean isAdmin() {
        return hashMap.containsKey("roles");
    }

}
