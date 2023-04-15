package com.kailas.dpm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
public class FinishReg {

     String name;
     String credname;
     PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>  credential;

}
/*
@Data
class Credential{
    String type;
    String id;
    Response response;
    ClientExtensionResults clientExtensionResults;
}
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Response{
    String attestationObject;
    String clientDataJSON;
    List<String>  transports;
}
@Data
class ClientExtensionResults{
    CredProps credProps;
}
@Data
class CredProps{
    Boolean rk;
}
*
 */