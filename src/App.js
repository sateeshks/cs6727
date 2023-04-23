import {useDispatch, useSelector} from "react-redux";
import Login from "./components/login/Login";
import Profile from "./components/profile/Profile";
import Logout from "./components/login/Logout";
import Registration from "./components/registration/Registration";
import {  Routes, Route, NavLink} from "react-router-dom";
import Pwdgen from "./components/password/pwdgen";
import About from "./components/about/About";


function App() {

  const { token } = useSelector((state) => state.login);

  return (
    <main className='container'>

      <header>
        <div style={{height: "100px"}}>
        <nav>
          <ul>
            <li><NavLink to="/"><strong>DPM</strong></NavLink></li>
          </ul>

          <ul>
            {(token && (
              <>
                <li><NavLink to="passwordgen">Password</NavLink></li>
                <li><NavLink to="profile">Profile</NavLink></li>
                <li><NavLink to="logout">Logout</NavLink></li>
              </>
            ))}
            <li><NavLink to="about">About</NavLink></li>
          </ul>
        </nav>
        </div>
      </header>

      <main>
        <Routes>
          <Route path="/" Component={Login} />
          <Route path="/login" Component={Login} />
          <Route path="/profile" Component={Profile} />
          <Route path="/passwordgen" Component={Pwdgen} />
          <Route path="/register" Component={Registration} />
          <Route path="/logout" Component={Logout} />
          <Route path="/about" Component={About} />
        </Routes>
      </main>


      <footer>
        <h6 style={{textAlign: "center"}}>&copy; Protected website</h6>
      </footer>

    </main>
  );
}

export default App;
