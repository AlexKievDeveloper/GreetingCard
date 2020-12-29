import React from "react";
import GoogleLogin from "react-google-login";
import { userService } from "../../../../services/userService";

export default function LoginWithGoogle(props) {
  const responseGoogle = (response) => {
    console.log("response from Google");
    console.log(response);
    console.log(response.profileObj);
    console.log(JSON.stringify(response));
    const googleProfile = response.profileObj;
    const googleData = {
      googleId: response.googleId,
      imageUrl: googleProfile.imageUrl,
      email: googleProfile.email,
      name: googleProfile.name,
      givenName: googleProfile.givenName,
      familyName: googleProfile.familyName,
    };
    userService.loginWithGoogle(googleData).then((result) => {
        props.handleResult(result, props.appLoginUser);
      });
  };

  const failureGoogle = (response) => {
    console.log("failure response from Google");
    console.log(response);
  };

  return (
    <GoogleLogin
      clientId="929593882430-ino7qk3h8hgaishmfnrrv0b6dgnb6ldh.apps.googleusercontent.com"
      render={(renderProps) => (
        <button
          className="login-button google-button"
          onClick={renderProps.onClick}
          disabled={renderProps.disabled}
        >
          Login with Google
        </button>
      )}
      buttonText="Login"
      onSuccess={responseGoogle}
      onFailure={failureGoogle}
      cookiePolicy="single_host_origin"
    />
  );
}
