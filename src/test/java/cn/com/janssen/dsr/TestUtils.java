package cn.com.janssen.dsr;

import cn.com.janssen.dsr.domain.User;
import cn.com.janssen.dsr.security.UserBasedUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.StringWriter;
import java.nio.charset.Charset;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    public static void loginAs(User user) {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(new UserBasedUserDetails(user));
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
    }

    public static String toJSON(Object object) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter jsonStringWriter = new StringWriter();
        objectMapper.writeValue(jsonStringWriter, object);
        LOGGER.debug("JSON content: {}", jsonStringWriter.toString());
        return jsonStringWriter.toString();
    }
}
