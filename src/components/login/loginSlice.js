import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { initialCheckStatus, base64urlToUint8array, uint8arrayToBase64url} from "../../helpers/custom";


export const loginAction = createAsyncThunk("login", async (params) => {
 const base_url=process.env.REACT_APP_API_URL;
    const response = await fetch(base_url+'/genpass/login', {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(params)
    });
    const credentialGetJson = await initialCheckStatus(response);
    const credentialGetOptions = {
        publicKey: {
            ...credentialGetJson.publicKey,
            allowCredentials: credentialGetJson.publicKey.allowCredentials
                && credentialGetJson.publicKey.allowCredentials.map(credential => ({
                    ...credential,
                    id: base64urlToUint8array(credential.id),
                })),
            challenge: base64urlToUint8array(credentialGetJson.publicKey.challenge),
            extensions: credentialGetJson.publicKey.extensions,
        }
    };
    console.log("1 first ");
    console.log("12 first second");
    console.log("credentialGetOptions :"+credentialGetOptions);
    console.log("2 second");
    if("credentials" in navigator) {
        console.log("Credentials available");
    }else{
        console.log("Credentials not available");
    }
    let publicKeyCredential = await  navigator.credentials.get(credentialGetOptions);
    console.log("3 third");
    console.log("publicKeyCredential :"+publicKeyCredential);
    const encodedResult = {
        type: publicKeyCredential.type,
        id: publicKeyCredential.id,
        response: {
            authenticatorData: uint8arrayToBase64url(publicKeyCredential.response.authenticatorData),
            clientDataJSON: uint8arrayToBase64url(publicKeyCredential.response.clientDataJSON),
            signature: uint8arrayToBase64url(publicKeyCredential.response.signature),
            userHandle: publicKeyCredential.response.userHandle && uint8arrayToBase64url(publicKeyCredential.response.userHandle),
        },
        clientExtensionResults: publicKeyCredential.getClientExtensionResults(),
    };
    const finishLogin = {
        credential: JSON.stringify(encodedResult),
        username: params.username
    };
    console.log(JSON.stringify(finishLogin))
    const welcomeResp = await fetch(base_url+"/genpass/welcome", {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(finishLogin)
    });
    const tokenResp = await initialCheckStatus(welcomeResp);
    //localStorage.setItem("token", tokenResp.token);
    return tokenResp.token;
});

const initState = {
    token: "",
    fetchStatus: ""
};

const loginSlice = createSlice({
    name: "login-reducer",
    initialState: initState,
    reducers: {
        "login": (state, action) => {
            state.token = action.payload;
        },
        "getToken": (state) => {
            return state.token;
        },
        "logout": (state) => {
            state.fetchStatus = "";
            state.token = "";
        },
        "loginFailed": (state, action) => {
            state.fetchStatus = action.payload;
        }
    },
    extraReducers: (builder) => {
        builder
          .addCase(loginAction.fulfilled, (state, action) => {
            state.token = action.payload;
             state.fetchStatus = 'success';
          })
          .addCase(loginAction.pending, (state) => {
            state.fetchStatus = 'Processing...';
          })
          .addCase(loginAction.rejected, (state, action) => {
            state.token = "";
            state.fetchStatus = 'Error!!! Please try after sometime';
            switch(action.error?.message) {
                case "400":
                    state.fetchStatus = "Error!!! Bad request";
                    break;
                case "401":
                    state.fetchStatus = "Error!!! Login failed.";
                    break;
                case "404":
                    state.fetchStatus = "User not found. Please Register";
                    break;
                case "412":
                    state.fetchStatus = "User name or email not matched .";
                    break;
                case "500":
                    state.fetchStatus = "Server Error!!!";
                    break;
                default:
                    state.fetchStatus = "Unknown error!!";
            }
            throw Error(state.fetchStatus);
          })
      },
});
export const { login, getToken, logout , loginFailed } = loginSlice.actions;

export default loginSlice;