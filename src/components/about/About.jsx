import {useEffect} from "react";
import {deleteProfilePassword} from "../password/pwdgenSlice";
import {useDispatch} from "react-redux";

const About = () => {
    const dispatch = useDispatch();
    useEffect( () => {
        dispatch(deleteProfilePassword());
    }, [] );
    return (
        <>
        <p>About DPM </p>
            <div>

                <p>The DPM is a unique password manager tool that will not store passwords, including the user's root password typically required to authenticate the user with the account. We achieve this by a mixture of FIDO2 'webauthn' protocol for user auth with the tool itself (kind of password less approach) for the user root account. And using an idempotence logic to produce the passwords for any given domain with a given configuration.</p>
                <p>First, let's talk about Idempotence. Idempotence is a concept in computer science that refers to the property of a function or operation that can be repeated multiple times without changing the result beyond the initial execution. In the password generation context, Idempotence is the ability to generate the same password multiple times based on a given set of configuration parameters. A password manager with idempotence password generation capability can repeatedly cause the same password based on the exact configuration parameters.</p>
                <p>To achieve idempotence password generation, the password manager typically uses a hashing algorithm to generate a unique password based on the input configuration parameters. The hashing algorithm takes the configuration parameters as input and produces a fixed-length output: the password. The input parameters will always have the same production, ensuring the idempotence property. The configuration parameters used to generate passwords may include factors such as password length, complexity, and character sets. For example, a password manager may allow users to configure the size of the password, the number of uppercase and lowercase letters, and the inclusion of special characters.</p>
                <p>The idempotence password generation capability can improve the user experience by allowing users to generate and reuse the same password when needed without having to remember a new password each time. It can also help organizations enforce consistent password policies and reduce the risk of weak or easily guessable passwords.</p>
                <p>With this approach, the DPM tool will not have any crown jewel data "Passwords" stored or synced on and from the server, thus reducing the attack surface.</p>
                <p>In summary, idempotence password generation capability is an essential feature of this tool that allows for the generation of the same password repeatedly based on a given set of configuration parameters.</p>

            </div>
        </>
    );
}

export default About;