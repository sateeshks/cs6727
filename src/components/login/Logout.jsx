import {useEffect} from "react";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import {logout} from "./loginSlice";
import {deleteProfile } from "../profile/ProfileSlice";
import {deleteProfilePassword} from "../password/pwdgenSlice";



const Logout = () => {
    //Logout before login
    const dispatch = useDispatch();
    const navigate = useNavigate();
    
    useEffect( () => {
            dispatch(deleteProfile());
            dispatch(logout());
            dispatch(deleteProfilePassword())
        navigate("/");
    }); //Not sure

}
export default Logout;