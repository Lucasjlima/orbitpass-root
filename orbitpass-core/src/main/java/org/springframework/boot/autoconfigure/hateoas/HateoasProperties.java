package org.springframework.boot.autoconfigure.hateoas;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.hateoas")
public class HateoasProperties {
    private boolean useHalAsDefaultJsonMediaType = true;

    public boolean isUseHalAsDefaultJsonMediaType() {
        return this.useHalAsDefaultJsonMediaType;
    }

    public void setUseHalAsDefaultJsonMediaType(boolean useHalAsDefaultJsonMediaType) {
        this.useHalAsDefaultJsonMediaType = useHalAsDefaultJsonMediaType;
    }

    // Binary compatibility bridge method to satisfy springdoc-openapi 2.8.5
    public boolean getUseHalAsDefaultJsonMediaType() {
        return isUseHalAsDefaultJsonMediaType();
    }
}
