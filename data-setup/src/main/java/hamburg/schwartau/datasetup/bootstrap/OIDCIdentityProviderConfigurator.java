package hamburg.schwartau.datasetup.bootstrap;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import java.util.Collections;

public class OIDCIdentityProviderConfigurator {

    private static final String SERVER_URL = "http://localhost:8080/auth";
    private static final String REALM = RealmSetup.REALM;
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String CLIENT_ID = "admin-cli";

    private final Keycloak keycloak;

    public OIDCIdentityProviderConfigurator(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public void execute() {
        IdentityProviderRepresentation identityProvider = new IdentityProviderRepresentation();
        identityProvider.setAlias("oidc-provider");
        identityProvider.setProviderId("oidc");
        identityProvider.setEnabled(true);

        // OIDC specific configuration
        identityProvider.getConfig().put("clientId", "your-client-id");
        identityProvider.getConfig().put("clientSecret", "your-client-secret");
        identityProvider.getConfig().put("authorizationUrl", "https://your-oidc-provider/auth");
        identityProvider.getConfig().put("tokenUrl", "https://your-oidc-provider/token");
        identityProvider.getConfig().put("userInfoUrl", "https://your-oidc-provider/userinfo");

        // Add the identity provider to the realm
        RealmRepresentation realm = keycloak.realm(REALM).toRepresentation();
        realm.setIdentityProviders(Collections.singletonList(identityProvider));
        keycloak.realm(REALM).update(realm);

        System.out.println("OIDC Identity Provider configured successfully.");
    }

}