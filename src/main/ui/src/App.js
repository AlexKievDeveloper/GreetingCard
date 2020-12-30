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
import Notification from "./components/Notification";

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      user: "",
      userId: 0,
      clientConnected: false,
      messages: [],
      isNewMessage: false
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
    userService.setUser(user.login);
  }

  logout() {
    userService.logout();
    this.setState({ user: "" });
  }

  onMessageReceive = (msg) => {
    let messages = this.state.messages;
    messages.push(msg.message);
    this.setState({ messages: messages, isNewMessage:true });
  };

  sendMessage = (selfMsg) => {
    try {
      this.clientRef.sendMessage("/app/request", JSON.stringify(selfMsg));
      return true;
    } catch (e) {
      return false;
    }
  };

  closeNotification = () => {
    this.setState({ isNewMessage:false });
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
        {this.state.user && (
          <SockJsClient
            url={wsSourceUrl}
            topics={["/topic/" + this.state.userId, "/topic/greetings"]}
            onMessage={this.onMessageReceive}
            ref={(client) => {
              this.clientRef = client;
            }}
            onConnect={() => {
              console.log("Connect start!");
              this.setState({ clientConnected: true });
            }}
            onDisconnect={() => {
              console.log("Disconnect");
              this.setState({ clientConnected: false });
            }}
            debug={false}
          />
        )}

        <LanguageProvider>
          <userContext.Provider value={userContextValue}>
            <Router>
              <Header userName={this.state.user} logoutCall={this.logout} />
              <SwitchRoute userName={this.state.user} />
            </Router>
          </userContext.Provider>
          {(this.state.messages.length > 0) && 
          <Notification message={this.state.messages[this.state.messages.length-1]}
                        isShow={this.state.isNewMessage}
                        onClose={this.closeNotification}
           />
        }
        </LanguageProvider>
      </div>
    );
  }
}

export default App;
