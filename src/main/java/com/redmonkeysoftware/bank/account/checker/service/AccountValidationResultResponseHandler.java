package com.redmonkeysoftware.bank.account.checker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redmonkeysoftware.bank.account.checker.model.AccountValidationResult;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

public class AccountValidationResultResponseHandler implements ResponseHandler<AccountValidationResult> {

    private final static Logger LOGGER = Logger.getLogger(AccountValidationResultResponseHandler.class.getName());
    private final ObjectMapper objectMapper;

    public AccountValidationResultResponseHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AccountValidationResult handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        int status = response.getStatusLine().getStatusCode();
        if ((status >= 200) && (status < 300)) {
            try {
                AccountValidationResult result = objectMapper.readValue(response.getEntity().getContent(), AccountValidationResult.class);
                return result;
            } catch (IOException | UnsupportedOperationException ex) {
                LOGGER.log(Level.WARNING, String.format("Error dealing with response JSON: %s", ex.getMessage()));
                throw new ClientProtocolException(String.format("Error dealing with response JSON: %s", ex.getMessage()));
            }
        } else {
            LOGGER.log(Level.WARNING, String.format("BankAccountChecker Request was not successful: %d:%s", status, response.getStatusLine().getReasonPhrase()));
            throw new ClientProtocolException(String.format("BankAccountChecker Request was not successful: %d:%s", status, response.getStatusLine().getReasonPhrase()));
        }
    }

}
