package pt.gov.ticapp.pdun.keycloak.idp.storage;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.SubjectCredentialManager;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserModelDefaultMethods;
import org.keycloak.models.utils.RoleUtils;

public class FaUserModel extends UserModelDefaultMethods {

  private final KeycloakSession session;
  private final RealmModel realm;
  private final FaUser faUser;

  public FaUserModel(KeycloakSession session, RealmModel realm,
      ComponentModel componentModel, String username) {

    this.session = session;
    this.realm = realm;
    faUser = new FaUser(username, session, componentModel);
  }

  public FaUserModel(KeycloakSession session, RealmModel realm, FaUser faUser) {

    this.session = session;
    this.realm = realm;
    this.faUser = faUser;
  }

  @Override
  public String getId() {

    return faUser.getStorageId().getId();
  }

  @Override
  public String getUsername() {

    return faUser.getUsername();
  }

  @Override
  public void setUsername(String username) {

    faUser.setUsername(username);
  }

  @Override
  public Long getCreatedTimestamp() {

    return faUser.getCreatedTimestamp();
  }

  @Override
  public void setCreatedTimestamp(Long timestamp) {

    if (timestamp != null && timestamp >= 0) {
      faUser.setCreatedTimestamp(timestamp);
    }
  }

  @Override
  public boolean isEnabled() {

    return faUser.isEnabled();
  }

  @Override
  public void setEnabled(boolean enabled) {

    faUser.setEnabled(enabled);
  }

  @Override
  public void setSingleAttribute(String name, String value) {

    faUser.setAttribute(name, value);
  }

  @Override
  public void removeAttribute(String name) {

    faUser.setAttribute(name, null);
  }

  @Override
  public void setAttribute(String name, List<String> values) {

    faUser.setAttribute(name, String.join("", values));
  }

  @Override
  public String getFirstAttribute(String name) {

    return faUser.getAttributeValue(name);
  }

  @Override
  public Stream<String> getAttributeStream(String name) {

    return Stream.of(faUser.getAttributeValue(name));
  }

  @Override
  public Map<String, List<String>> getAttributes() {
    MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();

    faUser.getAttributes().forEach(attributes::putSingle);

    return attributes;
  }

  @Override
  public Stream<String> getRequiredActionsStream() {

    return Stream.empty();
  }

  @Override
  public void addRequiredAction(String action) {
  }

  @Override
  public void removeRequiredAction(String action) {
  }

  @Override
  public boolean isEmailVerified() {

    return faUser.isEmailVerified();
  }

  @Override
  public void setEmailVerified(boolean verified) {

    faUser.setEmailVerified(verified);
  }

  @Override
  public Stream<GroupModel> getGroupsStream() {

    return Collections.<GroupModel>emptyList().stream();
  }

  @Override
  public void joinGroup(GroupModel group) {
  }

  @Override
  public void leaveGroup(GroupModel group) {
  }

  @Override
  public boolean isMemberOf(GroupModel group) {

    return RoleUtils.isMember(getGroupsStream(), group);
  }

  @Override
  public String getFederationLink() {

    return null;
  }

  @Override
  public void setFederationLink(String link) {
  }

  @Override
  public String getServiceAccountClientLink() {

    return null;
  }

  @Override
  public void setServiceAccountClientLink(String clientInternalId) {
  }

  @Override
  public SubjectCredentialManager credentialManager() {

    return new SubjectCredentialManager() {
      @Override
      public boolean isValid(List<CredentialInput> inputs) {
        return false;
      }

      @Override
      public boolean updateCredential(CredentialInput input) {
        return false;
      }

      @Override
      public void updateStoredCredential(CredentialModel cred) {
      }

      @Override
      public CredentialModel createStoredCredential(CredentialModel cred) {
        throw new UnsupportedOperationException("FA users do not support local credential storage");
      }

      @Override
      public boolean removeStoredCredentialById(String id) {
        return false;
      }

      @Override
      public CredentialModel getStoredCredentialById(String id) {
        return null;
      }

      @Override
      public Stream<CredentialModel> getStoredCredentialsStream() {
        return Stream.empty();
      }

      @Override
      public Stream<CredentialModel> getStoredCredentialsByTypeStream(String type) {
        return Stream.empty();
      }

      @Override
      public CredentialModel getStoredCredentialByNameAndType(String name, String type) {
        return null;
      }

      @Override
      public boolean moveStoredCredentialTo(String id, String newPreviousCredentialId) {
        return false;
      }

      @Override
      public void updateCredentialLabel(String credentialId, String userLabel) {
      }

      @Override
      public void disableCredentialType(String credentialType) {
      }

      @Override
      public Stream<String> getDisableableCredentialTypesStream() {
        return Stream.empty();
      }

      @Override
      public boolean isConfiguredFor(String credentialType) {
        return false;
      }

      @Override
      public boolean isConfiguredLocally(String credentialType) {
        return false;
      }

      @Override
      public Stream<String> getConfiguredUserStorageCredentialTypesStream() {
        return Stream.empty();
      }

      @Override
      public CredentialModel createCredentialThroughProvider(CredentialModel cred) {
        throw new UnsupportedOperationException("FA users do not support local credential storage");
      }
    };
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (!(o instanceof UserModel)) {
      return false;
    }

    UserModel that = (UserModel) o;

    return that.getId().equals(getId());
  }

  @Override
  public int hashCode() {

    return getId().hashCode();
  }

  @Override
  public Stream<RoleModel> getRealmRoleMappingsStream() {

    return realm.getDefaultRole().getCompositesStream().filter(RoleUtils::isRealmRole);
  }

  @Override
  public Stream<RoleModel> getClientRoleMappingsStream(ClientModel clientModel) {

    return getRealmRoleMappingsStream().filter(r -> RoleUtils.isClientRole(r, clientModel));
  }

  @Override
  public boolean hasRole(RoleModel role) {

    return RoleUtils.hasRole(getRoleMappingsStream(), role)
        || RoleUtils.hasRoleFromGroup(getGroupsStream(), role, true);
  }

  @Override
  public void grantRole(RoleModel role) {
  }

  @Override
  public Stream<RoleModel> getRoleMappingsStream() {

    return realm.getDefaultRole().getCompositesStream();
  }

  @Override
  public void deleteRoleMapping(RoleModel role) {
  }

  public FaUser getFaUser() {

    return faUser;
  }
}
