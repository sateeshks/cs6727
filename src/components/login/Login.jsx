import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {loginAction, loginFailed} from "./loginSlice";
import "../../App.css"
import {profileGetAction} from "../profile/ProfileSlice";


const Login = () => {
    const { user } = useSelector((state) => state.register);
    const { token, fetchStatus } = useSelector((state) => state.login);
    const [submitted, setSubmitted] = useState(false);
    const [inputs, setInputs] = useState({
        username: user?.name,
        email: user?.email

    });

    function handleChange(e) {
        const { name, value } = e.target;
        setInputs(inputs => ({ ...inputs, [name]: value }));
    }

    const dispatch = useDispatch();
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setSubmitted(true);
        try {
            await dispatch(loginAction(inputs));  //TODO for now comments as state is not updated on first time login
            setSubmitted(false);
            await dispatch(profileGetAction("default"));
            navigate("/profile");
        } catch (error) {
            console.error("login error", error);
            await dispatch(loginFailed(error.message));
            setSubmitted(false);
        }
    }

    return (
        <>

                <form name="login-form" onSubmit={handleSubmit}>
                    <div className="grid" >
                    <label htmlFor="username">
                        Username
                        <input type="text" name="username" placeholder="Username" required value={inputs.username} onChange={handleChange} />
                    </label>
                    </div>
                    
                    <label htmlFor="email">
                        Email
                        <input type="email" name="email" placeholder="Email" required value={inputs.email} onChange={handleChange} />
                    </label>
                    <div className="grid">
                    <button type="submit">Login</button>
                    <Link to="/register" role="button" className='secondary' style={{ height: "63px" }}>Register</Link>
                    </div>
                    {submitted ? <progress></progress>:<></> }
                </form>
                <div>
                    {(<h4 className="error">{fetchStatus}</h4>)}
                </div>

        </>
    );
};

export default Login;