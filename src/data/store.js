import { configureStore } from "@reduxjs/toolkit";
import loginSlice from "../components/login/loginSlice";
import registrationSlice from "../components/registration/registrationSlice";
import ProfileSlice from "../components/profile/ProfileSlice"
import passwordSlice from "../components/password/pwdgenSlice";

/*const reducer = combineReducers({
    // here we will be adding reducers
})*/
const store = configureStore({
    reducer: {
        login: loginSlice.reducer,
        register: registrationSlice.reducer,
        profile: ProfileSlice.reducer,
        passwordgenrator:passwordSlice.reducer
    },
    middleware: (getDefaultMiddleware) => getDefaultMiddleware(),
});

export default store;