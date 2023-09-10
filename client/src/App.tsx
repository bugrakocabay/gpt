import "./App.css";
import "./normal.css";
import { useState, useEffect } from "react";
import ReactMarkdown from "react-markdown";
import { v4 as uuidv4 } from "uuid";

function App() {
    const [input, setInput] = useState("");
    const [chatLog, setChatLog] = useState<ChatLog[]>([]);
    const [chatId, setChatId] = useState<string | null>(null);
    const [chatList, setChatList] = useState([]);

    useEffect(() => {
        async function fetchChatList() {
            try {
                const response = await fetch("http://localhost:8080/chat");
                const data = await response.json();
                setChatList(data);
            } catch (error) {
                console.error(error);
            }
        }

        fetchChatList();
    }, []);

    function clearChat() {
        setChatLog([]);
        setChatId(null);
    }

    async function handleSubmit(e: any) {
        e.preventDefault();
        let chatLogNew = [...chatLog, { user: "me", message: `${input}` }];
        setInput("");
        try {
            if (!chatId) {
                createChat()
                    .then(async (id) => {
                        const response = await fetch("http://localhost:8080/chat/message", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                            },
                            body: JSON.stringify({
                                id: id,
                                message: input.trim(),
                            }),
                        });
                        const body = await response.json();
                        setChatLog([...chatLogNew, { user: "gpt", message: `${body.message}` }]);

                        const responseChatList = await fetch("http://localhost:8080/chat");
                        const data = await responseChatList.json();
                        setChatList(data);
                    })
                    .catch((error) => {
                        console.error(error);
                    });
            } else {
                const response = await fetch("http://localhost:8080/chat/message", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        id: chatId,
                        message: input.trim(),
                    }),
                });
                const body = await response.json();
                setChatLog([...chatLogNew, { user: "gpt", message: `${body.message}` }]);
            }
        } catch (error) {
            console.error(error);
        }
    }

    async function createChat() {
        const id = uuidv4();
        setChatId(id);
        const response = await fetch("http://localhost:8080/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ id }),
        });
        await response.json();
        return id;
    }

    async function handleDeleteClick(e: any, id: string) {
        e.stopPropagation();
        try {
            const response = await fetch(`http://localhost:8080/chat/${id}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                },
            });
            await response.json();
            const responseChatList = await fetch("http://localhost:8080/chat");
            const data = await responseChatList.json();
            setChatList(data);
        } catch (error) {
            console.error(error);
        }
    }

    function updateChatLogWithSelectedChat(id: string) {
        const selectedChat: ChatDb = chatList.find((chat: ChatDb) => chat.conversationId === id)!;
        console.log(selectedChat);

        let chatLogNew: ChatLog[] = [];
        selectedChat.message.forEach((message) => {
            chatLogNew.push({ user: "me", message: `${message.message}` });
            chatLogNew.push({ user: "gpt", message: `${message.response}` });
        });
        setChatLog(chatLogNew);
        setChatId(selectedChat.conversationId);
    }

    return (
        <div className="App">
            <aside className="sidemenu">
                <div className="side-menu-button" onClick={clearChat}>
                    <span>+</span>
                    New Chat
                </div>
                <div className="chat-list">
                    {chatList.map((chat: ChatDb) => (
                        <div key={chat._id} className="chat-list-item" onClick={() => updateChatLogWithSelectedChat(chat.conversationId)}>
                            {chat._id}
                            <span className="close-button" onClick={(e) => handleDeleteClick(e, chat.conversationId)}>X</span>
                        </div>
                    ))}
                </div>
            </aside>
            <section className="chatbox">
                <div className="chat-log">
                    {chatLog.map((message, index) => (
                        <ChatMessage key={index} message={message} />
                    ))}
                </div>
                <div className="chat-input-holder">
                    <form onSubmit={handleSubmit}>
                        <input
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            className="chat-input-textarea"
                            placeholder="Type your message here"
                        ></input>
                    </form>
                </div>
            </section>
        </div>
    );
}

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

export default App;

interface ChatLog {
    user: string;
    message: string;
}

interface ChatDb {
    _id: string;
    conversationId: string;
    message: Message[];
}

interface Message {
    message: string;
    response: string;
}