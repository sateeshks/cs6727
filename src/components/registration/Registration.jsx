
import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {cleanReg, registrationAction, registrationFailed} from "./registrationSlice";
import { logout} from "../login/loginSlice";
function Registration() {

    const [submitted, setSubmitted] = useState(false);
    const { user, fetchStatus } = useSelector((state) => state.register);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    //const []
    const [newUser, setNewUser] = useState({
        firstName: '',
        lastName: '',
        name: '',
        email: '',
        credname: ''
    });

    useEffect( () => {
        dispatch(cleanReg())
        dispatch(logout());
    }, []);

    function handleChange(e) {
        const { name, value } = e.target;
        setNewUser(newUser => ({ ...newUser, [name]: value }));
    }


    async function handleSubmit(e) {
        e.preventDefault();
        setSubmitted(true);
        if (newUser.firstName && newUser.lastName && newUser.name && newUser.email && newUser.credname) {
            try {
                await dispatch(registrationAction(newUser));
                navigate("/login");
            } catch (error) {
                await dispatch(registrationFailed(error.message));
                console.log("registration error");
            }
        }
    }

    return (

        <form name="form" onSubmit={handleSubmit}>
            <div className="form-group">
                <label htmlFor="FirstName">First Name</label>
                <input type="text" name="firstName" required value={newUser.firstName} onChange={handleChange} {...(submitted && !newUser.firstName && { "aria-invalid": true })} />

            </div>
            <div className="form-group">
                <label htmlFor="LastName">Last Name</label>
                <input type="text" name="lastName" required value={newUser.lastName} onChange={handleChange} {...(submitted && !newUser.lastName && { "aria-invalid": true })} />

            </div>
            <div className="form-group">
                <label htmlFor="newUserName">Username</label>
                <input type="text" name="name" required value={newUser.name} onChange={handleChange} {...(submitted && !newUser.name && { "aria-invalid": true })} />

            </div>
            <div className="form-group">
                <label htmlFor="Email">email</label>
                <input type="email" name="email" required value={newUser.email} onChange={handleChange} {...(submitted && !newUser.email && { "aria-invalid": true })} />

            </div>
            <div className="form-group">
                <label htmlFor="DeviceName">Device Name</label>
                <input type="text" name="credname" required value={newUser.credname} onChange={handleChange} {...(submitted && !newUser.credname && { "aria-invalid": true })} />

            </div>
            <div className="form-group">
                <div className="grid">
                    <button className="btn btn-primary" type='submit'>
                        Register
                    </button>
                    <Link to="/logout" role="button" className='secondary' style={{ height: "63px" }}>Cancel</Link>
                </div>
            </div>
            <div>
               {/* <label htmlFor="error" name="errorm">{fetchStatus}</label>*/}
                {(<h4 className="error">{fetchStatus}</h4>)}
            </div>
        </form>
    );
}

export default Registration;