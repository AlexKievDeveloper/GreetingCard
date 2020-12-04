import React, {Component} from 'react'
import './style.css';
import CardCommandRow from '../../components/Cards/CardCommandRow'
import Block from '../../components/Blocks/Block';
import {cardService} from '../../services/cardService';
import { userContext } from '../../context/userContext';

export class CreateEditCard extends Component {
    constructor(props) {
        super(props);
        this.state = {
            blocks: [],
            userIdCardAdmin:0
        }

        this.deleteBlock = this.deleteBlock.bind(this);
    }

    componentDidMount() {
        const id = this.getIdFromPath();
        cardService.getCard(id)
            .then((cardsData) => {this.setState({blocks : cardsData.congratulationList, userIdCardAdmin: cardsData.user.id});
                                 }     
            );
    }

    getIdFromPath = () => this.props.location.pathname.replace("/edit_card/", "");

    deleteBlock(idBlock) {
        const newBlocks = this.state.blocks.filter(block => block.id !== idBlock);
        this.setState({blocks:newBlocks});
    } 

    render() {

        const idCard = this.getIdFromPath();

        let congragulations = this.state.blocks.map((block) => (<Block {...this.props} key={block.id} block={block} idCard={idCard} onDeleteBlock={this.deleteBlock}/>));

        return (
            <div className="main-functions">
                <userContext.Consumer>
                  {({ userId }) => (
                        <CardCommandRow {...this.props} 
                                idCard={idCard}
                                isMyCard={userId === this.state.userIdCardAdmin}/>
                   )}
                </userContext.Consumer>
                <main className="card-container">
                    {congragulations}
                </main>
            </div>
        )
    }
}

export default CreateEditCard
