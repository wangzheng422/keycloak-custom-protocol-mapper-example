package hamburg.schwartau.datasetup.bootstrap;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import java.util.Collections;

public class OIDCIdentityProviderConfigurator {

    private static final String REALM = RealmSetup.REALM;

    private final Keycloak keycloak;

    public OIDCIdentityProviderConfigurator(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void execute() {
        IdentityProviderRepresentation identityProvider = new IdentityProviderRepresentation();
        // identityProvider.setAlias(System.getenv("OIDC_PROVIDER_ALIAS") != null ? System.getenv("OIDC_PROVIDER_ALIAS") : "default-oidc-provider");
        identityProvider.setAlias(System.getenv().getOrDefault("OIDC_PROVIDER_ALIAS", "azure")); 
        identityProvider.setProviderId("oidc");
        identityProvider.setEnabled(true);

        // OIDC specific configuration
        identityProvider.getConfig().put("clientId", System.getenv().getOrDefault("OIDC_PROVIDER_CLIENT_ID", "your-client-id"));
        identityProvider.getConfig().put("clientSecret", System.getenv().getOrDefault("OIDC_PROVIDER_CLIENT_SECRET", "your-client-secret"));
        identityProvider.getConfig().put("authorizationUrl", System.getenv().getOrDefault("OIDC_PROVIDER_AUTHORIZATION_URL", "https://your-oidc-provider/auth"));
        identityProvider.getConfig().put("tokenUrl", System.getenv().getOrDefault("OIDC_PROVIDER_TOKEN_URL", "https://your-oidc-provider/token"));
        // identityProvider.getConfig().put("userInfoUrl", System.getenv().getOrDefault("OIDC_PROVIDER_USERINFO_URL", "https://your-oidc-provider/userinfo"));
        identityProvider.getConfig().put("defaultScope", "openid profile");

        // identityProvider.setConfig(Collections.singletonMap("defaultScope", "openid profile")); // This is a more concise way to set the default scope  

        // Assuming identityProvider.getConfig() returns a Map<String, String>
        for (String key : identityProvider.getConfig().keySet()) {
            System.out.println(key);
        }

        // Directly add the identity provider to the realm
        keycloak.realm(REALM).identityProviders().create(identityProvider);

        System.out.println("OIDC Identity Provider configured successfully.");
    }

}