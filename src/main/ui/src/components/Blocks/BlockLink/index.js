import React from 'react'
import './style.css';

export default function BlockLink(props) {
    const type = props.type;
    const link = props.link;
    
    if (type === 'VIDEO') {
        const youtube_link = 'https://www.youtube.com/embed/' + link;
        return (
            <div className="optional-element optional-element_video">
                <iframe width="560px"
                        height="300px"
                        src={youtube_link}
                        frameBorder="0"
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                        allowFullScreen>
                </iframe>
            </div>
        )
    } else if (type === "AUDIO") {
        return <audio controls className="optional-element optional-element_audio">
            <source src={link} type="audio/mp3"/>
        </audio>
    } else {
        return (
            <div className="optional-element">
                <img src={link} className="optional-element_image" alt=""/>
            </div>
        )
    }
}
