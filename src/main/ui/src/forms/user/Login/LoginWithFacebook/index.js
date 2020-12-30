import React from "react";
import FacebookLogin from "react-facebook-login/dist/facebook-login-render-props";
import { userService } from "../../../../services/userService";

export default function LoginWithFacebook(props) {
  
    const responseFacebook = (response) => {
    console.log("response from Facebook");
    console.log(response);
    let facebookData = {
      email: response.email,
      name: response.name,
      userID: response.userID,
    };
    console.log(JSON.stringify(facebookData));
    userService.loginWithFacebook(facebookData).then((result) => {
      props.handleResult(result, props.appLoginUser);
    });
  };


  return (
    <FacebookLogin
      appId="3424105121040662"
      autoLoad={false}
      fields="name,email,picture"
      callback={responseFacebook}
      render={(renderProps) => (
        <button
          className="login-button facebook-button"
          onClick={renderProps.onClick}
        >
          Login with Facebook
        </button>
      )}
    />
  );
}
