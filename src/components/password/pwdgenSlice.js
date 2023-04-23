import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { initialCheckStatus} from "../../helpers/custom";


export const pwdProfileGetAction = createAsyncThunk("getPwdProfile", async ( domain, { getState }) => {
    const base_url=process.env.REACT_APP_API_URL;
    const state = getState();
    const token  = state.login.token;
    console.log("profile get action  token is   "+token);
    let profile = await   fetch(base_url+'/genpass/user/profile?domain='+domain, {
        headers: {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        },
        method: 'GET'
    }).catch((error) => {

        console.log("error loading profile");
    })
    profile = await initialCheckStatus(profile);
    return profile;
});

export const pwdgenAction = createAsyncThunk("pwdgen", async (profile, { getState }) => {
    const base_url=process.env.REACT_APP_API_URL;
    const state = getState();
    const token  = state.login.token;
    console.log("profileSaveAction token is   "+token);
    let password = await fetch(base_url+'/genpass/user/password', {
        headers: {
            'Authorization': 'Bearer '+token,
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(profile)
    });
    password = await initialCheckStatus(password);
    let result={'domain':profile.domain,'password':password.password ,'revision':password.revision};
    return result;
});

const pwdInitState = {
    domain: '',
    password: '',
    revision:'1'
};

const passwordSlice = createSlice({
    name: "password-reducer",
    initialState: {
        pwd: pwdInitState,
        dprofile: undefined
      /*  dprofile:{
            domain: '',
            lowerCase: true,
            upperCase: true,
            digits: true,
            symbols:true,
            exclude:'',
            length:8,
            revision:1,
            init:true
        }*/
    },
    reducers: {
        "generate": (state,action) => {
            state.pwd = action.payload;

        },
        "getPassword": (state,action) => {
            return state.pwd;
        },
        "deleteProfilePassword": ( state) => {
            state.pwd = {};
            state.dprofile={};

        },
        "cleanDprofile": ( state) => {
            state.dprofile={};

        },
        "showErrorMessage": ( state,action) => {
            state.errorMsg=action.payload;

        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(pwdProfileGetAction.fulfilled, (state, action) => {
                state.dprofile = action.payload;
                state.pwd.password='';
                state.pwd.domain='';
                state.pwd.revision=action.payload.revision;
                state.pwd_profile_fetchStatus = 'success';
                console.log("password profile loaded   " +  state.dprofile );
             })
            .addCase(pwdProfileGetAction.pending, (state) => {
                state.pwd_profile_fetchStatus = 'pwd profile loading';
            })
            .addCase(pwdProfileGetAction.rejected, (state) => {
                state.pwd = undefined;
                state.pwd_profile_fetchStatus = 'pwd profile load error';
            })
            .addCase(pwdgenAction.fulfilled, (state, action) => {
                state.pwd = action.payload;
                state.fetchStatus = 'success';
                console.log("password generated   " + state.pwd.password);
            })
            .addCase(pwdgenAction.pending, (state) => {
                state.fetchStatus = 'loading';
            })
            .addCase(pwdgenAction.rejected, (state) => {
                state.pwd = undefined;
                state.fetchStatus = 'error';
            })
    },
});
export const { deleteProfilePassword,cleanDprofile,cleanPwd} = passwordSlice.actions;
export default passwordSlice;