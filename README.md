# Introduction

This extension allows configuring the a _SAML Identity Provider_ in _Keycloak_ where it is necessary to handle the _Attribute_ tags that are contained in _AttributeStatement_ tag that belongs to the _Assertion_ tag section in a _SAML Response_ tag.

This extension was specially made for integrating with _Fornecedor de Autenticação_ since it contains specific fields for it.

# General Workflow

The following image depicts the general workflows where the code is used.

## Initialization

![General Workflows - Initialization](./assets/GeneralFlow-Initialization.png)

## Login

## ![General Workflows - Login](./assets/GeneralFlow-Login.png)

# Configuring an Identity Provider

## Creating a New Identity Provider

To create a new Identity Provider select _FA SAML v1.5.2 IDP_ entry.

![](assets/2023-01-23-12-01-14-image.png)

Configure the _Identity Provider_ as usual for other _Identity Providers_ of the same kind.

## Specifying First Login Flow

![](assets/2023-01-23-12-16-08-image.png)

See how to create this authentication bellow.

## Specifying Principal

To comply with RGPD and not storying any user attributes (that can be easily traced to the user), configure the principal as next:

![](assets/2023-01-23-12-04-22-image.png)

*calculatedPrincipalAssertion* is a value defined in the project properties. The value used in _Principal Attribute_ must match the value in the property file.

## Specifying Requested Attributes

In the section _FA SAML Extension Configuration_ it is possible to specify which attributes are
being requested from the _Identity Provider_.

![FA SAML EXTENSION CONFIGURATION](./assets/FA_SAML_EXTENSION_CONFIGURATION.png)

More information over this configuration available at:
[Autenticação.Gov](https://www.autenticacao.gov.pt/documents/20126/115760/Manual+de+Integra%C3%A7%C3%A3o+do+Fornecedor+de+Autentica%C3%A7%C3%A3o+v1.5.7.pdf)

### Requested Attributes Field

The field _Requested Attributes_ allow to specify which attributes to ask from the
_Identity Provider_. The value format is _JSON_.

The _JSON_ format is the following;

```json
{
  "xmlns" : "",
  "name" : "",
  "format" : "",
  "required" : ""
}
```

Only **name** is mandatory.

### FA AA Level Field

The field _FA AA Level_ allows to specify the trust level:

- **1** -> Authentication with _Cartão de Cidadão_;
- **2** -> Authentication with _Chave Móvel Digital (CMD);
- **3** -> Authentication with _CMD_ through _Email_ or _Twitter_;
- **4** -> Authentication with Username/password and Social Media.

# Configuring an Identity Provider Mapper

To retrieve properties from the _Attribute_ tag in a _SAML Response_ it is necessary to configure 
an _Identity Provider Mapper_.

The following is an example of such a configuration:

![Identity Provider Mapper Configuration](./assets/IDENTITY_PROVIDER_MAPPER.png)

- _Mapper Type_: _Attribute Importer_ must be selected
- _Attribute Name_: The specification (in *JSON*) that allows retrieving a value from the _SAML 
  Response_ from the _Attribute_ assertions. 
- _User Attribute Name_: Where to set the value retrieved from the response. This is the property
  where _Keycloak_ will store the value in the current user attributes.

## Attribute Name JSON specification

The following is the _JSON_ specification that can be used in the _Attribute Name_ field.

```json
{
  "name" : "", 
  "type" : "DIRECT"
  "selector" : ""
}
```

- **name** - The value to search in the *XML* attributes. The value will be searched in the
  _Attribute_ tags, where the tag attribute _Name_ is equal to the value specified in the field 
  _name_.
- **type** - Type can be: 
  - DIRECT : If used, no processing will occur over the found value. **selector** value will be 
    ignored;
  - XML : If used, some processing will occur over the found value using the **selector** field. 
    It is expected the value to be _XML_;
  - FUNCTION: 
- **selector** : Used in conjunction with type **XML** or **FUNCTION**. 
  - When used with **XML**: 
    indicates the path from where extract the requested value from the _XML_ value. "." indicates a 
    path to transverse. ":" indicates that the given key value must have the given one. "|" indicates 
    that the given key should be used to
    retrieve the next value to process.
  - When used with **FUNCTION**: Indicates the function to apply to the given arguments. Currently 
    the only existing function is _concat_, that allows to concatenate two arguments. One can refer
    to the argument value read from _XML_ as _${AttrbibuteValue}_.

### name example

Assuming the following piece of _SAML Response_

```xml
<Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
           Name="http://interop.gov.pt/MDC/Cidadao/NomeCompleto"
           NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
           d4p1:AttributeStatus="Available">
    <AttributeValue xsi:type="xsd:string">Manuel João António José</AttributeValue>
</Attribute>
```

to retrieve the _AttributeValue_ "Manuel João António José", field name should be 
"http://interop.gov.pt/MDC/Cidadao/NomeCompleto".

```json
{
"name" : "http://interop.gov.pt/MDC/Cidadao/NomeCompleto",
"type":"DIRECT"
}
```

### type DIRECT example

Assuming the following piece of _SAML Response_

```xml
<Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
           Name="http://interop.gov.pt/MDC/Cidadao/NomeCompleto"
           NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
           d4p1:AttributeStatus="Available">
    <AttributeValue xsi:type="xsd:string">Manuel João António José</AttributeValue>
</Attribute>
```

to retrieve the _AttributeValue_ "Manuel João António José", field name should be
"http://interop.gov.pt/MDC/Cidadao/NomeCompleto" and type should be _DIRECT_.

```json
{
  "name" : "http://interop.gov.pt/MDC/Cidadao/NomeCompleto", 
  "type":"DIRECT"
}
```

### type XML example

Assuming the following piece of _SAML Response_

```xml
<Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
           Name="http://interop.gov.pt/SCAP/FAF"
           NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
           d4p1:AttributeStatus="Available">
    <AttributeValue xsi:type="xsd:string">
        <ScapAttributes xmlns="http://www.scap.autenticacao.gov.pt/FAScapAttributes">
            <AttributeSupplier>
                <Name>Empresa Teste Q 0402 1610</Name>
                <Nipc>509726607</Nipc>
                <Attributes>
                    <Attribute>
                        <Name>Cargo 4</Name>
                        <IdentifiableName>Cargo_4</IdentifiableName>
                        <SubAttributes>
                            <SubAttribute>
                                <Description>Nome da entidade</Description>
                                <Value>Empresa Teste Q 0402 1610</Value>
                            </SubAttribute>
                            <SubAttribute>
                                <Description>NIPC</Description>
                                <Value>509726607</Value>
                            </SubAttribute>
                            <SubAttribute>
                                <Description>E-mail do funcionário</Description>
                                <Value>one.email@some.company.com</Value>
                            </SubAttribute>
                            <SubAttribute>
                                <Description>Atribuído por</Description>
                                <Value>Administrador</Value>
                            </SubAttribute>
                        </SubAttributes>
                    </Attribute>
                </Attributes>
            </AttributeSupplier>
        </ScapAttributes>
    </AttributeValue>
</Attribute>
```

to retrieve the _AttributeValue_ "one.email@some.company.com", field name should be
"http://interop.gov.pt/SCAP/FAF", type should be _XML_ and the selector is the path to reach the 
requested value (_ScapAttributes.AttributeSupplier.Attributes.Attribute.SubAttributes.SubAttribute.Description:'E-mail do funcionário'_).

```json
{
  "name" : "http://interop.gov.pt/SCAP/FAF", 
  "selector": "ScapAttributes.AttributeSupplier.Attributes.Attribute.SubAttributes.SubAttribute.Description:'E-mail do funcionário|Value'", 
  "type": "XML"}
```

### type FUNCTION example

Assuming the following piece of _SAML Response_

```xml
<Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
           Name="http://interop.gov.pt/MDC/Cidadao/NIF"
           NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
           d4p1:AttributeStatus="Available">
    <AttributeValue xsi:type="xsd:string">133133133</AttributeValue>
</Attribute>
```

Let's assume that it is needed to use _NIF_ as an email, where the domain name will be always
_@fa.gov.pt_. To retrieve the _AttributeValue_ "133133133", field name should be
"http://interop.gov.pt/MDC/Cidadao/NIF", type should be _FUNCTION_ and selector should be 
_concat(${AttributeValue},"@fa.gov.pt")_.

```json
{
  "name" : "http://interop.gov.pt/MDC/Cidadao/NIF", 
  "type": "FUNCTION",
  "selector": "concat(${AttributeValue},"@fa.gov.pt")"
}
```

# Configuring User Federation

Add a new _User Federation_ by selecting **fa-user-storage**.

![](assets/2023-01-23-12-09-00-image.png)

![](assets/2023-01-23-12-09-38-image.png)

Click save.

**It is this configuration that allows not storing any user data on keycloak database.**

# Configuring Authentication

Create a new authentication.

![](assets/2023-01-23-12-11-59-image.png)

![](assets/2023-01-23-12-13-31-image.png)

Add execution

![](assets/2023-01-23-12-13-51-image.png)

![](assets/2023-01-23-12-14-14-image.png)

Add execution

![](assets/2023-01-23-12-13-51-image.png)

![](assets/2023-01-23-12-14-34-image.png)

![](assets/2023-01-23-12-15-03-image.png)

# Annex - Example of a _SAML Response_

```xml
<Response xmlns:xsd="http://www.w3.org/2001/XMLSchema"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns="urn:oasis:names:tc:SAML:2.0:protocol"
          ID="_de943457-d5c6-4686-8f86-bd5e0966f147"
          InResponseTo="ID_e0340ba4-d1ea-48f2-bc0d-a0ddc1a4318b"
          Version="2.0"
          IssueInstant="2022-05-19T14:19:35.857789Z"
          Destination="https://some.server.com/endpoint"
          Consent="urn:oasis:names:tc:SAML:2.0:consent:unspecified">
    <Issuer xmlns="urn:oasis:names:tc:SAML:2.0:assertion">https://autenticacao.cartaodecidadao.pt</Issuer>
    <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
        <SignedInfo>
            <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315"/>
            <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
            <Reference URI="#_de943457-d5c6-4686-8f86-bd5e0966f147">
                <Transforms>
                    <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
                    <Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
                </Transforms>
                <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
                <DigestValue>NzuY2B1BF2L9MjerriuA0lIgJcs=</DigestValue>
            </Reference>
        </SignedInfo>
        <SignatureValue>SomeSignatureValue==</SignatureValue>
        <KeyInfo>
            <X509Data>
                <X509Certificate>X509CertificateValue==</X509Certificate>
            </X509Data>
        </KeyInfo>
    </Signature>
    <Extensions>
        <fa:FAAALevel xmlns:fa="http://autenticacao.cartaodecidadao.pt/atributos">3</fa:FAAALevel>
    </Extensions>
    <Status>
        <StatusCode Value="urn:oasis:names:tc:SAML:2.0:status:Success"/>
    </Status>
    <Assertion xmlns="urn:oasis:names:tc:SAML:2.0:assertion"
               Version="2.0"
               ID="_aed14629-5a20-41ee-b6e3-bd43d5d8b7ba"
               IssueInstant="2022-05-19T14:19:35.857789Z">
        <Issuer>https://autenticacao.cartaodecidadao.pt</Issuer>
        <Subject>
            <NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</NameID>
            <SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
                <SubjectConfirmationData NotOnOrAfter="2022-05-19T14:24:35Z"
                                         Recipient="some.recipient.com"
                                         InResponseTo="ID_e0340ba4-d1ea-48f2-bc0d-a0ddc1a4318b"
                                         Address="https://ignore.mordomo.gov.pt"/>
            </SubjectConfirmation>
        </Subject>
        <Conditions NotBefore="2022-05-19T14:19:35Z"
                    NotOnOrAfter="2022-05-19T14:24:35Z">
            <AudienceRestriction>
                <Audience>some.recipient.com</Audience>
            </AudienceRestriction>
            <OneTimeUse/>
        </Conditions>
        <AuthnStatement AuthnInstant="2022-05-19T14:19:35.857789Z">
            <AuthnContext>
                <AuthnContextDecl xsi:type="xsd:string"/>
            </AuthnContext>
        </AuthnStatement>
        <AttributeStatement>
            <Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
                       Name="http://interop.gov.pt/MDC/FA/PassarConsentimento"
                       NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
                       d4p1:AttributeStatus="Available">
                <AttributeValue xsi:type="xsd:string">1</AttributeValue>
            </Attribute>
            <Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
                       Name="http://interop.gov.pt/MDC/Cidadao/NIC"
                       NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
                       d4p1:AttributeStatus="Available">
                <AttributeValue xsi:type="xsd:string">10000000</AttributeValue>
            </Attribute>
            <Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
                       Name="http://interop.gov.pt/MDC/Cidadao/NIF"
                       NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
                       d4p1:AttributeStatus="Available">
                <AttributeValue xsi:type="xsd:string">133133133</AttributeValue>
            </Attribute>
            <Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
                       Name="http://interop.gov.pt/MDC/Cidadao/NomeCompleto"
                       NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
                       d4p1:AttributeStatus="Available">
                <AttributeValue xsi:type="xsd:string">Manuel João António José</AttributeValue>
            </Attribute>
            <Attribute xmlns:d4p1="http://autenticacao.cartaodecidadao.pt/atributos"
                       Name="http://interop.gov.pt/SCAP/FAF"
                       NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri"
                       d4p1:AttributeStatus="Available">
                <AttributeValue xsi:type="xsd:string">
                    <ScapAttributes xmlns="http://www.scap.autenticacao.gov.pt/FAScapAttributes">
                        <AttributeSupplier>
                            <Name>Empresa Teste Q 0402 1610</Name>
                            <Nipc>509726607</Nipc>
                            <Attributes>
                                <Attribute>
                                    <Name>Cargo 4</Name>
                                    <IdentifiableName>Cargo_4</IdentifiableName>
                                    <SubAttributes>
                                        <SubAttribute>
                                            <Description>Nome da entidade</Description>
                                            <Value>Empresa Teste Q 0402 1610</Value>
                                        </SubAttribute>
                                        <SubAttribute>
                                            <Description>NIPC</Description>
                                            <Value>509726607</Value>
                                        </SubAttribute>
                                        <SubAttribute>
                                            <Description>E-mail do funcionário</Description>
                                            <Value>one.email@some.company.com</Value>
                                        </SubAttribute>
                                        <SubAttribute>
                                            <Description>Atribuído por</Description>
                                            <Value>Administrador</Value>
                                        </SubAttribute>
                                    </SubAttributes>
                                </Attribute>
                            </Attributes>
                        </AttributeSupplier>
                    </ScapAttributes>
                </AttributeValue>
            </Attribute>
        </AttributeStatement>
    </Assertion>
</Response>
```

# Annex - Example of a _Request Attributes_ Definition

```json
{
  "xmlns" : "http://autenticacao.cartaodecidadao.pt/atributos",
  "name" : "http://interop.gov.pt/MDC/Cidadao/NomeCompleto",
  "format" : "urn:oasis:names:tc:SAML:2.0:attrname-format:uri",
  "required" : "true"
}
```

# Keycloak Configuration Guide

This section describes all steps required to configure Keycloak with this extension and integrate it with the _Authentication Provider_ (FA).

## Prerequisites

- Keycloak 26.x running with this extension's JAR placed in the `providers/` folder
- A PKCS12 file with the **private key** and **X.509 certificate** of the _Service Provider_ (SP) registered with AMA, including the **full CA certificate chain** (leaf certificate + intermediate CA + root CA)
- The **AMA signing certificate** (to validate SAML responses from FA)

> AMA signing certificates are available in the official repository:
> [https://github.com/amagovpt/doc-AUTENTICACAO](https://github.com/amagovpt/doc-AUTENTICACAO)

### Build and Install the Extension

```shell
mvn clean package
cp target/keycloakfaidp-1.5.2.jar <keycloak-home>/providers/keycloakfaidp-1.5.2.jar
```

Restart Keycloak after copying the JAR.

---

## 1. SP Signing Key

Keycloak must sign SAML _AuthnRequests_ with the SP private key registered with AMA.

In the Keycloak Admin Console, go to **Realm Settings → Keys → Providers → Add provider → java-keystore** and fill in:

| Field | Value |
|---|---|
| Name | _(any name, e.g. `sp-signing-key`)_ |
| Priority | `200` _(must be higher than the default `100` to take precedence)_ |
| Enabled | `ON` |
| Active | `ON` |
| Algorithm | `RS256` |
| Keystore | _(absolute path to the PKCS12 file)_ |
| Keystore Type | `PKCS12` |
| Keystore Password | _(keystore password)_ |
| Key Alias | _(alias of the key entry inside the keystore)_ |
| Key Password | _(key password)_ |

> **Important:** The PKCS12 file must contain the **full certificate chain** (leaf certificate + intermediate CA + root CA). If it only contains the leaf certificate, Keycloak rejects the key provider with the error `Certificate error on server. Path does not chain with any of the trust anchors`.

To create the PKCS12 from separate files (private key + certificate + AMA CA chain):

```shell
# 1. Convert private key from DER to PEM format (if needed)
openssl pkcs8 -inform DER -in private_key.key -nocrypt -out private_key.pem

# 2. Export SP certificate from an existing JKS (if needed)
keytool -exportcert -keystore sp.jks -storepass <password> -alias <alias> -rfc -file sp_cert.pem

# 3. Extract CA certificates from the AMA p7b chain file
openssl pkcs7 -inform DER -in ama_chain.p7b -print_certs -out full_chain.pem
# Manually split into intermediate_ca.pem and root_ca.pem

# 4. Create the PKCS12 with the full chain
cat sp_cert.pem intermediate_ca.pem root_ca.pem > fullchain.pem
openssl pkcs12 -export -inkey private_key.pem -in fullchain.pem -name <alias> -out sp_keystore.p12 -passout pass:<password>

# 5. Verify — Certificate chain length should be 3
keytool -list -v -keystore sp_keystore.p12 -storepass <password> -storetype PKCS12
```

### Add AMA Root CA to the Keycloak Truststore

For Keycloak to accept certificates from the AMA chain, the root CA certificate must be imported into the JVM truststore running Keycloak.

```shell
# Import AMA root CA into the JVM truststore
keytool -importcert \
  -alias ama-root-ca \
  -keystore $JAVA_HOME/lib/security/cacerts \
  -storepass changeit \
  -file root_ca.pem \
  -noprompt
```

> **Docker/container note:** If Keycloak runs in a container, copy the certificate into the container and import it before starting the service, or mount a custom truststore as a volume and configure it via `JAVA_OPTS`:
> ```
> -Djavax.net.ssl.trustStore=/opt/keycloak/truststore.jks -Djavax.net.ssl.trustStorePassword=<password>
> ```

Restart Keycloak after importing the certificate.

---

## 2. Identity Provider

Go to **Identity Providers → Add provider → FA SAML v1.5.2 IDP**.

### General settings

| Field | Value |
|---|---|
| Alias | `fa-saml-idp` |
| Display name | _(e.g. `Autenticação GOV`)_ |

> The **Redirect URI** field is filled in automatically by Keycloak — this is the **ACS URL** to register with AMA: `https://{keycloak-host}/auth/realms/{realm}/broker/fa-saml-idp/endpoint`

### SAML settings

| Field | Pre-Production | Production |
|---|---|---|
| Service provider entity ID | _(SP Entity ID registered with AMA)_ | _(SP Entity ID registered with AMA)_ |
| Single Sign-On service URL | `https://preprod.autenticacao.gov.pt/fa/Default.aspx` | `https://autenticacao.gov.pt/fa/Default.aspx` |
| Single logout service URL | `https://preprod.autenticacao.gov.pt/fa/Logout.ashx` | `https://autenticacao.gov.pt/fa/Logout.ashx` |
| NameID policy format | `Transient` | `Transient` |
| Principal type | `Attribute` | `Attribute` |
| Principal attribute | `calculatedPrincipalAssertion` | `calculatedPrincipalAssertion` |
| HTTP-POST binding response | `ON` | `ON` |
| HTTP-POST binding for AuthnRequest | `ON` | `ON` |
| HTTP-POST binding logout | `ON` | `ON` |
| Want AuthnRequests signed | `ON` | `ON` |
| Signature algorithm | `RSA_SHA256` | `RSA_SHA256` |
| SAML signature key name | `KEY_ID` | `KEY_ID` |
| Validate Signatures | `ON` | `ON` |
| Validating X509 certificates | _(AMA pre-production signing certificate)_ | _(AMA production signing certificate)_ |
| Force authentication | `ON` | `ON` |

### Advanced settings

| Field | Value |
|---|---|
| First login flow override | _(select the authentication flow configured for FA)_ |
| Sync mode | `Import` |

### FA SAML Extension Configuration

| Field | Value |
|---|---|
| FA AA Level | `3` _(adjust as needed: 1=CC, 2=CMD, 3=CMD via email/Twitter, 4=username/password)_ |
| Requested Attributes | See JSON below |

```json
[
  {"name": "http://interop.gov.pt/MDC/Cidadao/NIC"},
  {"name": "http://interop.gov.pt/MDC/Cidadao/NomeCompleto"},
  {"name": "http://interop.gov.pt/MDC/Cidadao/NomeProprio"},
  {"name": "http://interop.gov.pt/MDC/Cidadao/NomeApelido"},
  {"name": "http://interop.gov.pt/MDC/Cidadao/NIF"},
  {"name": "http://interop.gov.pt/MDC/Cidadao/CorreioElectronico"}
]
```

---

## 3. Identity Provider Mappers

Go to **Identity Providers → fa-saml-idp → Mappers** and create the following four mappers. All use _Mapper Type_: **Attribute Importer**.

| Mapper Name | Attribute Name | User Attribute Name |
|---|---|---|
| `firstName` | `{"name": "http://interop.gov.pt/MDC/Cidadao/NomeProprio", "type": "DIRECT"}` | `firstName` |
| `lastName` | `{"name": "http://interop.gov.pt/MDC/Cidadao/NomeApelido", "type": "DIRECT"}` | `lastName` |
| `username` | `{"name": "calculatedPrincipalAssertion", "type": "DIRECT"}` | `username` |
| `email` | `{"name": "http://interop.gov.pt/MDC/Cidadao/CorreioElectronico", "type": "DIRECT"}` | `email` |

> **Note:** `calculatedPrincipalAssertion` is a synthetic attribute added by the extension from the NIC value. It is an obfuscated identifier used as the Keycloak username, in compliance with GDPR.

---

## 4. User Profile

To prevent Keycloak from prompting the user for an email address when FA does not return the `CorreioElectronico` attribute (e.g. in pre-production test accounts), make email optional in the realm's user profile.

Go to **Realm Settings → User Profile → email** and set **Required** to `OFF`.

> In production, FA returns `CorreioElectronico` and the email mapper populates it automatically, so the user will never see that form.

---

## 5. User Federation

Add a _User Federation_ provider at **User Federation → Add provider → fa-user-storage**.

This ensures FA user data is not permanently stored in the Keycloak database.

---

## 6. Pre-Production vs Production Differences

| | Pre-Production | Production |
|---|---|---|
| FA SSO URL | `preprod.autenticacao.gov.pt/fa/Default.aspx` | `autenticacao.gov.pt/fa/Default.aspx` |
| AMA signing certificate | Pre-production certificate (issued by AMA preprod CA) | Production certificate |
| ACS URL registration | AMA preprod may accept the ACS URL sent in the SAML request itself, even without prior registration | Must be registered with AMA in advance |
| SP Entity ID | As registered with AMA pre-production | As registered with AMA production |
| Email attribute | May be absent in test accounts | Present when the user has it on the Citizen Card |

---

# Debug Extension

## On IntelliJ

![](assets/debug_intellij.png)

## Run Docker Image

### Start Database

```shell
docker run -d -e POSTGRES_PASSWORD=rootpassas -e POSTGRES_USER=pduncapau -e POSTGRES_DB=keycloak -p 5432:5432 postgres:14.2
```

### Start Keycloak

```shell
docker run -it -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -e KC_DB=postgres -e KC_DB_URL=jdbc:postgresql://192.168.1.113:5432/keycloak?ssl=allow -e JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8787" -e KC_DB_USERNAME=pduncapau -e KC_DB_PASSWORD=rootpassas -p 8080:8080 -p 8787:8787 -e KC_HOSTNAME=localhost -v "C:/Trabalho/Desenvolvimento/GIT/pdun/fa-keycloak-extension/usethis/keycloakfaidp-1.5.2.jar:/opt/keycloak/providers/keycloakfaidp-1.5.2.jar" --entrypoint sh ticapp/keycloak:20.0.3-postgres-fa
```

On image

```shell
cd bin
./kc.sh start-dev
```

# Build extension

```shell
mvn clean install -P local
```

Jar will be on directory _usethis_.

# Test OIDC Locally

## Step 1

Goto https://oidcdebugger.com/ and fill:

Authorize URL -> http://localhost:8080/realms/fa/protocol/openid-connect/auth
Redirect URI -> https://oidcdebugger.com/debug
Client ID -> ticapp (replace with the client id created in Keycloak to be used)

Response Type -> Select "code"
Use PKCE? -> Select 
Use SHA256

Code verifier -> DyL07ysHoNkEZfQX71pN55a7x3fwkJ3CI2rF7vAJokw

## Step 2

On Postman 

URL : http://localhost:8080/realms/fa/protocol/openid-connect/token (Method POST)

Send a Body (x-www-form-urlencoded) with:

grant_type -> authorization_code
client_id -> ticapp (replace with the client id created in Keycloak to be used)
client_secret -> credentials (check on client keys in keycloak)
state -> Copy from response of step 1
session_state -> Copy from response of step 1
code -> Copy from response of step 1
redirect_uri -> https://oidcdebugger.com/debug
code_verifier -> DyL07ysHoNkEZfQX71pN55a7x3fwkJ3CI2rF7vAJokw