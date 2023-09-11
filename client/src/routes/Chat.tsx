import "../styles/App.css";
import "../styles/normal.css";
import { useState, useEffect } from "react";
import { ChatMessage } from "../components";
import { fetchChatList, createChat, deleteChat, postChatMessage } from "../services";
import { ChatDb, ChatLog } from "../interfaces";

const Chat = () => {
    const [input, setInput] = useState("");
    const [chatLog, setChatLog] = useState<ChatLog[]>([]);
    const [chatId, setChatId] = useState<string | null>(null);
    const [chatList, setChatList] = useState([]);

    useEffect(() => {
        fetchChatList()
            .then((responseChatList) => {
                setChatList(responseChatList);
            })
            .catch((error) => {
                console.error("Error fetching chat list:", error);
            });
    }, []);

    function clearChat() {
        setChatLog([]);
        setChatId(null);
    }

    async function handleSubmit(e: any) {
        e.preventDefault();
        let chatLogNew = [...chatLog, { user: "me", message: input }];
        setInput("");
        try {
            if (!chatId) {
                createChat()
                    .then(async (id) => {
                        const response = await postChatMessage(id, input.trim());
                        setChatLog([
                            ...chatLogNew,
                            { user: "gpt", message: `${response.message}` },
                        ]);

                        const responseChatList = await fetchChatList();
                        setChatList(responseChatList);
                    })
                    .catch((error) => {
                        console.error(error);
                    });
            } else {
                const response = await postChatMessage(chatId, input.trim());
                setChatLog([...chatLogNew, { user: "gpt", message: `${response.message}` }]);
            }
        } catch (error) {
            console.error(error);
        }
    }

    async function handleDeleteClick(e: any, id: string) {
        e.stopPropagation();
        try {
            await deleteChat(id);
            const responseChatList = await fetchChatList();
            setChatList(responseChatList);
        } catch (error) {
            console.error(error);
        }
    }

    function updateChatLogWithSelectedChat(id: string) {
        const selectedChat: ChatDb = chatList.find((chat: ChatDb) => chat.conversationId === id)!;

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
                        <div
                            key={chat._id}
                            className="chat-list-item"
                            onClick={() => updateChatLogWithSelectedChat(chat.conversationId)}
                        >
                            {chat._id}
                            <span
                                className="close-button"
                                onClick={(e) => handleDeleteClick(e, chat.conversationId)}
                            >
                                X
                            </span>
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
};

export default Chat;