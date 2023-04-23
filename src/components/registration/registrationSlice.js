import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { initialCheckStatus, base64urlToUint8array, uint8arrayToBase64url, displayError, followRedirect, throwError, checkStatus, WebAuthServerError } from "../../helpers/custom";
import loginSlice from "../login/loginSlice";

export const registrationAction = createAsyncThunk("register", async (user) => {
    const base_url=process.env.REACT_APP_API_URL;
    const response = await fetch(base_url+'/genpass/register', {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(user)
    });
    const credentialCreateJson = await initialCheckStatus(response);
    const credentialCreateOptions ={
        publicKey: {
            ...credentialCreateJson.publicKey,
            challenge: base64urlToUint8array(credentialCreateJson.publicKey.challenge),
            user: {
                ...credentialCreateJson.publicKey.user,
                id: base64urlToUint8array(credentialCreateJson.publicKey.user.id),
            },
            excludeCredentials: credentialCreateJson.publicKey.excludeCredentials.map(credential => ({
                ...credential,
                id: base64urlToUint8array(credential.id),
            })),
            extensions: credentialCreateJson.publicKey.extensions,
        },
    };
    const publicKeyCredential = await navigator.credentials.create(credentialCreateOptions);
    const encodedResult = {
        type: publicKeyCredential.type,
        id: publicKeyCredential.id,
        response: {
            attestationObject: uint8arrayToBase64url(publicKeyCredential.response.attestationObject),
            clientDataJSON: uint8arrayToBase64url(publicKeyCredential.response.clientDataJSON),
            transports: publicKeyCredential.response.getTransports && publicKeyCredential.response.getTransports() || [],
        },
        clientExtensionResults: publicKeyCredential.getClientExtensionResults(),
    };
    user.credential = encodedResult;
    console.log(JSON.stringify(user));
    const finshAuthResp = await fetch(base_url+'/genpass/finishauth', {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(user)
    });
    const finshAuth = await initialCheckStatus(finshAuthResp);
    //localStorage.setItem("token", tokenResp.token);
    return finshAuth;


});
const initState = {
    user: undefined,
    fetchStatus: ""
};

const registrationSlice = createSlice({
    name: "register-reducer",
    initialState: initState,
    reducers: {
        "register": (state,action) => {
            state.user = action.payloadl;
        },
        "getUser": ( state,action) => {
            return state.user;
        },
        "registrationFailed": (state, action) => {
            state.fetchStatus = action.payload;
        },"cleanReg": ( state) => {
            state.user = undefined;
            state.fetchStatus="";
        },

    },
    extraReducers: (builder) => {
        builder
            .addCase(registrationAction.fulfilled, (state, action) => {
                state.user = action.payload.user;
                state.fetchStatus = 'success';
                console.log("user registered is " + state.user.name);
            })
            .addCase(registrationAction.pending, (state) => {
                state.fetchStatus = 'loading';
            })
            .addCase(registrationAction.rejected, (state,action) => {
                state.user = undefined;
                state.fetchStatus = 'error';
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
                    case "409":
                        state.fetchStatus = "User name all ready exist .";
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
export const { registrationFailed, register, getUser,cleanReg } = registrationSlice.actions;
export default registrationSlice;