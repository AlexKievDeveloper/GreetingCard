import "./App.css";
import React from "react";
import { BrowserRouter as Router } from "react-router-dom";
import Header from "./containers/Header";
import { userService } from "./services/userService";
import { userContext } from "./context/userContext";
import SwitchRoute from "./components/SwithRoute";
import { LanguageProvider } from "./components/Language/LanguageProvider";
import SockJsClient from "react-stomp";
import config from "./services/config";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            user: "",
            userId: 0,
            clientConnected: false,
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
    userService.setUser(user.login)
  }

  logout() {
    userService.logout();
    this.setState({ user: "" });
  }

    onMessageReceive = (msg) => {
        console.log("REACT RECEIVED MESSAGE ")
        console.log('First:');
        console.log(msg);
        console.log('Second:');
        console.log(msg.body);
        console.log('Third:');
        console.log(JSON.parse(msg.body).message)
    }


    sendMessage = (selfMsg) => {
        try {
            this.clientRef.sendMessage("/app/request", JSON.stringify(selfMsg));
            return true;
        } catch (e) {
            return false;
        }
    }

    render() {
        const userContextValue = {
            user: this.state.user,
            userId: this.state.userId,
            loginUser: this.login,
        };
      const wsSourceUrl = config.wsSourceUrl;
      return (
          <div className="wrapper">
            {this.state.user &&
            <SockJsClient url={wsSourceUrl} topics={["/topic/" + this.state.userId, "/topic/greetings"]}
                          onMessage={this.onMessageReceive} ref={(client) => {
              this.clientRef = client
            }}
                          onConnect={() => {
                            console.log("Connect start!");
                            console.log("USER LOGIN: " + this.state.user);//DEMO
                            console.log("USER ID: " + this.state.userId);//20
                            this.sendMessage({"message": "Hello server! I am React. Lets connect?"});
                            this.setState({clientConnected: true})
                          }}

                          onDisconnect={() => {
                            console.log("Disconnect")
                            this.setState({clientConnected: false})
                          }}
                          debug={false}/>}


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