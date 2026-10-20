package com.ledgerline.payments.dto;

import com.ledgerline.payments.config.AppProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class ApprovedHostValidator implements ConstraintValidator<ApprovedHost, String> {

    private final AppProperties props;

    public ApprovedHostValidator(AppProperties props) {
        this.props = props;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(value.trim());
            String host = uri.getHost();
            if (host == null || !"https".equals(uri.getScheme()) || uri.getUserInfo() != null) {
                return false;
            }
            return props.approvedHosts().contains(host.toLowerCase(Locale.ROOT));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
