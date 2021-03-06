package de.fhg.iais.roberta.javaServer.provider;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.util.UtilForREST;
import de.fhg.iais.roberta.util.dbc.DbcKeyException;

@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Provider
public class DbcKeyExceptionMapper implements ExceptionMapper<DbcKeyException> {
    private static final Logger LOG = LoggerFactory.getLogger(DbcKeyExceptionMapper.class);
    private static final String SERVER_ERROR = Key.SERVER_ERROR.getKey();
    static final String ERROR_IN_ERROR = "{\"rc\":\"error\",\"message\":\"" + SERVER_ERROR + "\",\"rc\":\"" + SERVER_ERROR + "\",}";

    @Override
    public Response toResponse(DbcKeyException e) {
        final String errorKey = e.getKey().getKey();
        if ( errorKey.startsWith("ORA_INIT_FAIL") ) {
            LOG.error("init exception was caught at system border: " + e.getMessage());
        } else {
            LOG.error("exception was caught at system border", e);
        }
        try {
            final JSONObject response = new JSONObject();
            response.put("rc", "error");
            response.put("message", errorKey).put("cause", errorKey).put("parameters", e.getParameter());
            return UtilForREST.responseWithFrontendInfo(response, null, null);
        } catch ( Exception eInE ) {
            LOG.error("server error - exception in exception processor", eInE);
            return Response.ok(ERROR_IN_ERROR).build();
        }
    }
}