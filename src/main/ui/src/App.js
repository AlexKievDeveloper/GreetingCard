import "./App.css";
import React from "react";
import { BrowserRouter as Router } from "react-router-dom";
import Header from "./containers/Header";
import { userService } from "./services/userService";
import { userContext } from "./context/userContext";
import SwitchRoute from "./components/SwithRoute";

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      user: "",
      userId: 0,
    };

    this.logout = this.logout.bind(this);
    this.login = this.login.bind(this);
  }

  componentDidMount() {
    const userLogined = userService.getUser();
    this.setState({
      user: userLogined.user,
      userId: parseInt(userLogined.userId),
    });
  }

  login(name, password) {
    const login = name;
    return userService.login(login, password).then((result) => {
      if (result.hasOwnProperty("message")) {
        return result;
      } else {
        let userId = result.userId;
        this.setState({ user: login, userId: userId });
        userService.setUserId(userId);
      }
    });
  }

  logout() {
    userService.logout();
    this.setState({ user: "" });
  }

  render() {
    const userContextValue = {
      user: this.state.user,
      userId: this.state.userId,
      loginUser: this.login,
    };

    return (
      <div className="wrapper">
        <userContext.Provider value={userContextValue}>
          <Router>
            <Header userName={this.state.user} logoutCall={this.logout} />
            <SwitchRoute userName={this.state.user} />
          </Router>
        </userContext.Provider>
      </div>
    );
  }
}

export default App;
