import { useDispatch, useSelector } from "react-redux";
import React, { useState, useEffect } from 'react';
import { pwdgenAction,pwdProfileGetAction } from "./pwdgenSlice";



const Pwdgen = () => {

    const dispatch = useDispatch();
    const { sprofile } = useSelector((state) => state.profile);
    const { pwd, dprofile } = useSelector((state) => state.passwordgenrator);
    const [pwdprofile, setPwdprofile] = useState(sprofile);
    const [dmsg ,setDmsg] = useState();

    useEffect( () => {
            if (dprofile?.domain) {
                console.log(dprofile);
                setPwdprofile(dprofile);
            }else{
                setPwdprofile(pwdprofile => ({ ...pwdprofile, domain: '' }));
            }

        }, [dprofile] );

    useEffect( () => {
        console.log("use effect pwd "+pwd.revision);
        console.log("use effect  pwdprofile  revision "+pwdprofile.revision);
        setPwdprofile(pwdprofile => ({ ...pwdprofile, revision: pwd.revision}));

    }, [pwd] );

    const [submitted, setSubmitted] = useState(false);

    function handleChange(e) {
        const { name, value } = e.target;
        setPwdprofile(pwdprofile => ({ ...pwdprofile, [name]: value }));
    }

    function handleChanged(e) {
        const { name, value } = e.target;
        setPwdprofile(pwdprofile => ({ ...pwdprofile, [name]: value }));
        if (name === 'domain')
            handleChangeDomain(value).then(r => console.log("doamin loaded"));
    }

    function handleCheckbox(e) {
        const { name } = e.target;
        const newVal = !pwdprofile[name];
        setPwdprofile(user => ({ ...user, [name]: newVal }));
    }

    function handleRevision(e) {
        console.log("latest revision from server "+pwd.revision);
        const { name, value } = e.target;
        const currentRevision = pwd ? parseInt(pwd.revision) : parseInt(sprofile.revision);
        const nextRevision = currentRevision + 1;
        if (value == currentRevision) {
            setPwdprofile(pwdprofile => ({ ...pwdprofile, [name]: currentRevision }));
        } else if (value >= nextRevision) {
            setPwdprofile(pwdprofile => ({ ...pwdprofile, [name]: nextRevision }));
        }
    }

    async function handleChangeDomain(value) {
        setDmsg('');
        await dispatch(pwdProfileGetAction(value));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        if (pwdprofile.domain === 'default') {
            setDmsg("Canot use domain name 'default'");
            return;
      }
        console.log("pwd gen submit called ");
        setSubmitted(true);
        try {
            console.log("pwd gen submit called -values " +JSON.stringify(pwdprofile) );
            await dispatch(pwdgenAction(pwdprofile));
            setSubmitted(false);
        } catch (error) {
            //TODO add error message
            // console.log("profile save  error");
        }


    }


    return (
        <>
        <h4>Password Generator</h4>
        <form name="form" onSubmit={handleSubmit}>
            <label htmlFor="domain">
                <span>Domain</span>
                <input type="text" name="domain"   required={true} value={pwdprofile.domain} onChange={handleChange} onBlur={handleChanged}  />
            </label>
            <label htmlFor="isLower">

                <input type="checkbox" name="lowerCase" checked={pwdprofile.lowerCase} onChange={handleCheckbox} />
                <span> Include Lower Case</span>
            </label>
            <label htmlFor="isUpper">

                <input type="checkbox" name="upperCase" checked={pwdprofile.upperCase} onChange={handleCheckbox} />
                <span> Include Upper Case</span>
            </label>
            <label htmlFor="isSymbols">

                <input type="checkbox" name="symbols" checked={pwdprofile.symbols} onChange={handleCheckbox} />
                <span> Include Symbols </span>
                <span> !@#$%^&* []  </span>
            </label>
            <label htmlFor="isDigits">

                <input type="checkbox" name="digits" checked={pwdprofile.digits} onChange={handleCheckbox} />
                <span> Include Digits</span>
            </label>
            <label htmlFor="pwdLength">
                <span> Password Length  </span>
                <input type="number" name="length" min={6} value={pwdprofile.length} onChange={handleChange} />
            </label>

            <label htmlFor="excludechars">
                Exclude characters
                <input type="text" name="exclude" value={pwdprofile.exclude} placeholder="Exclude Characters" onChange={handleChange} />
            </label>
            <label htmlFor="revision">
                <span> Revision  </span>
                <input type="number" name="revision" min={1} value={pwdprofile.revision} onChange={handleRevision} />
            </label>
            <button type="submit">Generate Password</button>
            {submitted ? <progress></progress>:<></> }
            <label htmlFor="Generated Password:">
                <span>Generated Password : </span>
                <div>
                <input type="text" name="password" value={pwd.password} placeholder="Generated Password" readOnly={true} />
                <a href='#' role="button" className="secondary" onClick={() => {navigator.clipboard.writeText(pwd.password)}}>Copy</a>
                </div>
            </label>
            <div>
                {(<h4 className="error">{dmsg}</h4>)}
            </div>
        </form>
        </>
    );
};

export default Pwdgen;