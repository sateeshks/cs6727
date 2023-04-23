import { useNavigate } from "react-router-dom";
import React, {useEffect, useState} from 'react';
import { profileSaveAction } from "./ProfileSlice";
import { useDispatch, useSelector } from "react-redux";
import { deleteProfilePassword} from "../password/pwdgenSlice";



const Profile = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { sprofile } = useSelector((state) => state.profile);
    // const [profile, setProfile] = useState( sprofile);

    const [submitted, setSubmitted] = useState(false);
    //local object
    
    const [profile, setProfile] = useState({
        domain: sprofile.domain,
        lowerCase: sprofile.lowerCase,
        upperCase: sprofile.upperCase,
        digits: sprofile.digits,
        symbols: sprofile.symbols,
        exclude: sprofile.exclude,
        revision: 1,
        length: sprofile.length
    });
    useEffect( () => {
         dispatch(deleteProfilePassword());
    }, [] );
    function handleChange(e) {
        const { name, value } = e.target;
        setProfile(user => ({ ...user, [name]: value }));
    }

    function handleCheckbox(e) {
        const { name, value } = e.target;
        const newVal = !profile[name];
        setProfile(user => ({ ...user, [name]: newVal }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        console.log("Profile save submit called ");
        try {
            setSubmitted(true);
            await dispatch(profileSaveAction(profile));
            navigate("/passwordgen");
        } catch (error) {
            console.log("profile save  error");
        }

    }



    return (
        <>
        <h4>Default Profile</h4>
        <form name="form" onSubmit={handleSubmit}>
            <label htmlFor="domain">
                <span>Domain</span>
                <input type="text" id="domain" value={profile.domain} readOnly={true} />
            </label>
            <label htmlFor="isLower">

                <input type="checkbox" name="lowerCase" checked={profile.lowerCase} onChange={handleCheckbox} />
                <span> Include Lower Case</span>
            </label>
            <label htmlFor="isUpper">

                <input type="checkbox" name="upperCase" checked={profile.upperCase} onChange={handleCheckbox} />
                <span> Include Upper Case</span>
            </label>
            <label htmlFor="isSymbols">

                <input type="checkbox" name="symbols" checked={profile.symbols} onChange={handleCheckbox} />
                <span> Include Symbols </span>
                <span> !@#$%^&* []  </span>
            </label>
            <label htmlFor="isDigits">

                <input type="checkbox" name="digits" checked={profile.digits} onChange={handleCheckbox} />
                <span> Include Digits</span>
            </label>
            <label htmlFor="pwdLength">
                <span> Password Length  </span>
                <input type="number" name="length" min={6} value={profile.length} onChange={handleChange} />
            </label>

            <label htmlFor="excludechars">
                Exclude characters
                <input type="text" name="exclude" value={profile.exclude} placeholder="Exclude Characters" onChange={handleChange} />
            </label>
            <button role="button" type="submit">Save</button>

        </form>
        </>
    );
};

export default Profile;