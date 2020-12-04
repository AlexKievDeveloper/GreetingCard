import React, { Component } from "react";
import './style.css';
import { formValidator } from "../../forms/user/formValidator";
import { cardService } from "../../services/cardService";
import CardUser from "./CardUser";
import CardUsersCommandRow from "./CardUsersCommandRow";

export default class CardUsers extends Component {
  constructor(props) {
    super(props);
    this.state = {
      cardId: 0,
      users: [],
      usersIdToDelete: [],
    };
    this.addUser = this.addUser.bind(this);
    this.uncheckSelected = this.uncheckSelected.bind(this);
    this.deleteChosen = this.deleteChosen.bind(this);
    this.onClickDelete = this.onClickDelete.bind(this);
  }

  componentDidMount() {
    const cardId = this.props.location.pathname.replace("/card_users/", "");
    this.setState({ cardId: cardId });
    this.readUsers(cardId);
  }

  uncheckSelected() {
    this.setState({ usersIdToDelete: [] });
  }

  deleteChosen(event) {
    event.preventDefault();
    let usersIdToDelete = this.state.usersIdToDelete;
    if (usersIdToDelete.length > 0) {
        cardService.deleteUsers(this.state.cardId, usersIdToDelete)
                   .then(() => this.readUsers(this.state.cardId));
    } else {
           alert("Nothing selected to delete");
    }
  }

  onClickDelete(event) {
    const nameCheckBox = event.target.name;
    const userIdStr = nameCheckBox.replace("delete_", "");
    const userId = parseInt(userIdStr);
    const usersIdToDelete = this.state.usersIdToDelete;
    if (event.target.checked) {
       usersIdToDelete.push(userId); 
       this.setState({usersIdToDelete:usersIdToDelete});
    } else {
        const usersIdToDeleteNew = usersIdToDelete.filter((id) => id!== userId); 
        console.log(usersIdToDeleteNew);
        this.setState({usersIdToDelete:usersIdToDeleteNew});
    }
  }

  readUsers(cardId) {
    cardService.getUsers(cardId).then((usersData) => {
      this.setState({ users: usersData });
    });
  }

  addUser(login) {
    if (!formValidator.isValid("login", login)) {
      alert("login is not correct!");
      return;
    }
    cardService.addUser(this.state.cardId, login).then(() => this.readUsers(this.state.cardId));
  }

  render() {
    return (
      <div className="main-functions">
        <CardUsersCommandRow
          addUserFunction={this.addUser}
          clearChoiceFunction={this.uncheckSelected}
          deleteUsersFunction={this.deleteChosen}
        />
        <main className="container">
          <form action="" className="form__column" id="list-of-collaborators">
            <div className="users__column">
              {this.state.users.map((user) => (
                <CardUser
                  key={user.id}
                  user={user}
                  checked={this.state.usersIdToDelete.indexOf(user.id) >= 0}
                  onClickDeleteCheckBox = {this.onClickDelete}
                />
              ))}
            </div>
          </form>
        </main>
      </div>
    );
  }
}
