package com.ledgerline.payments.dto;

import com.ledgerline.payments.config.AppProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class TrustedReturnUrlValidator implements ConstraintValidator<TrustedReturnUrl, String> {

    private final AppProperties props;

    public TrustedReturnUrlValidator(AppProperties props) {
        this.props = props;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        try {
            URI uri = new URI(value);
            String host = uri.getHost();
            return "https".equals(uri.getScheme())
                    && host != null
                    && props.trustedReturnHosts().contains(host.toLowerCase(Locale.ROOT));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
