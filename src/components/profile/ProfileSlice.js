import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { initialCheckStatus } from "../../helpers/custom";


export const profileGetAction = createAsyncThunk("getProfile", async ( domain, { getState }) => {
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
export const profileSaveAction = createAsyncThunk("save_profile", async (profile, { getState }) => {
    const base_url=process.env.REACT_APP_API_URL;
    const state = getState();
    const token  = state.login.token;
    console.log("profileSaveAction token is   "+token);
    const savedprofile = await fetch(base_url+'/genpass/user/saveprofile', {
        headers: {
            'Authorization': 'Bearer '+token,
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(profile)
        });
     const profileReturned = await initialCheckStatus(savedprofile);
     return profileReturned;
    });

const profileSlice = createSlice({
    name: "profile-reducer",
    initialState: {
        sprofile: {
            domain: 'default',
            lowerCase: true,
            upperCase: true,
            digits: true,
            symbols:true,
            exclude:'',
            length:8,
            revision:1,
            init:true
        }
    },
    reducers: {
        "save": (action, state) => {
            state.sprofile = action.payload.body;
        },
        "getProfile": (action, state) => {
            return state.sprofile;
        },
        "setProfile": (action, state) => {
            state.sprofile =state.payload;
        },
        "deleteProfile": (state) => {
            state.sprofile = {};
        },
    },
    extraReducers: (builder) => {
        builder
            .addCase(profileGetAction.fulfilled, (state, action) => {
                if (action.payload.domain) {
                    state.sprofile = action.payload;
                    state.profile_fetch = 'sucess';
                }
                console.log("profile  is loaded  " + state.profile);
            }).addCase(profileGetAction.pending, (state) => {
                 state.profile_fetch = 'loading';
              })
            .addCase(profileGetAction.rejected, (state) => {
                state.profile_fetch = 'error';
            })
            .addCase(profileSaveAction.fulfilled, (state, action) => {
                state.sprofile = action.payload;
                state.fetchStatus = 'success';
                console.log("profile  is saved  " + state.profile);
            })
            .addCase(profileSaveAction.pending, (state) => {
                state.fetchStatus = 'loading';
            })
            .addCase(profileSaveAction.rejected, (state) => {
                state.sprofile = undefined;
                state.fetchStatus = 'error';
            })
    },
});
export const { save, getProfile, setProfile, deleteProfile} = profileSlice.actions;
export default profileSlice;