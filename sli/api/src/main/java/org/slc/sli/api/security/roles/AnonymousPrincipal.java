package org.slc.sli.api.security.roles;

import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class AnonymousPrincipal implements Principal {
    @Override
    public String getName() {
        return "anonymousPrincipal";
    }
}
