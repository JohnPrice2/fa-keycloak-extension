package pt.gov.ticapp.pdun.keycloak.idp;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.broker.provider.UserAuthenticationIdentityProvider;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.broker.saml.SAMLIdentityProvider;
import org.keycloak.broker.saml.SAMLIdentityProviderConfig;
import org.keycloak.dom.saml.v2.protocol.ResponseType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.saml.common.constants.GeneralConstants;
import org.keycloak.saml.processing.core.saml.v2.common.SAMLDocumentHolder;
import org.keycloak.saml.validators.DestinationValidator;

public class FaSamlEndpoint extends SAMLEndpoint {

  private final KeycloakSession session;

  public FaSamlEndpoint(KeycloakSession session, SAMLIdentityProvider provider, SAMLIdentityProviderConfig config,
      UserAuthenticationIdentityProvider.AuthenticationCallback callback, DestinationValidator destinationValidator) {

    super(session, provider, config, callback, destinationValidator);
    this.session = session;
  }

  @Override
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response postBinding(
      @FormParam(GeneralConstants.SAML_REQUEST_KEY) String samlRequest,
      @FormParam(GeneralConstants.SAML_RESPONSE_KEY) String samlResponse,
      @FormParam(GeneralConstants.SAML_ARTIFACT_KEY) String samlArt,
      @FormParam(GeneralConstants.RELAY_STATE) String relayState) {

    if (samlArt != null) {
      return super.postBinding(samlRequest, samlResponse, samlArt, relayState);
    }
    return new FaSamlPostBinding().execute(samlRequest, samlResponse, null, relayState, null);
  }

  class FaSamlPostBinding extends SAMLEndpoint.PostBinding {

    @Override
    protected Response handleLoginResponse(String samlResponse, SAMLDocumentHolder holder, ResponseType responseType,
        String relayState, String clientId) {

      return new FaSamlPostBindingWorker(session, callback, this).handleLoginResponse(samlResponse, holder, responseType,
          relayState, clientId);
    }

    Response handleLoginResponseWithPrincipal(String samlResponse, SAMLDocumentHolder holder, ResponseType responseType,
        String relayState, String clientId) {

      return super.handleLoginResponse(samlResponse, holder, responseType, relayState, clientId);
    }
  }
}
