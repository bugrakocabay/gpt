import ReactMarkdown from "react-markdown";

const ChatMessage = ({ message }: any) => {
    return (
        <div className={`chat-message ${message.user === "gpt" && "chatgpt"}`}>
            <div className="chat-message-center">
                <div className={`avatar ${message.user === "gpt" && "chatgpt"}`} />
                <ReactMarkdown>{message.message}</ReactMarkdown>
            </div>
        </div>
    );
};

export default ChatMessage;