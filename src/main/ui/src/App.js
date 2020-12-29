import "./App.css";
import React from "react";
import { BrowserRouter as Router } from "react-router-dom";
import Header from "./containers/Header";
import { userService } from "./services/userService";
import { userContext } from "./context/userContext";
import SwitchRoute from "./components/SwithRoute";
import { LanguageProvider } from "./components/Language/LanguageProvider";

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

  login(user) {
    let userId = user.userId;
    this.setState({ user: user.login, userId: userId });
    userService.setUserId(userId);
    userService.setLanguage(user.userLanguage);
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
        <LanguageProvider>
          <userContext.Provider value={userContextValue}>
            <Router>
              <Header userName={this.state.user} logoutCall={this.logout} />
              <SwitchRoute userName={this.state.user} />
            </Router>
          </userContext.Provider>
        </LanguageProvider>
      </div>
    );
  }
}

export default App;
