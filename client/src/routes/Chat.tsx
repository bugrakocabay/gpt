import "../styles/App.css";
import "../styles/normal.css";
import { useState, useEffect, useRef } from "react";
import { ChatMessage } from "../components";
import { fetchChatById, fetchChatList, createChat, deleteChat, postChatMessage, editAliasRequest } from "../services";
import { ChatDb, ChatLog } from "../interfaces";
const userInfo = JSON.parse(localStorage.getItem("userInfo")!);

const Chat = () => {
    const [input, setInput] = useState("");
    const [chatLog, setChatLog] = useState<ChatLog[]>([]);
    const [chatId, setChatId] = useState<string | null>(null);
    const [chatList, setChatList] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const chatLogRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        fetchChatList(userInfo.id)
            .then((responseChatList) => {
                if (responseChatList?.message?.includes("JWT expired")) {
                    localStorage.removeItem("token");
                    localStorage.removeItem("userInfo");
                    window.location.reload();
                }                
                setChatList(responseChatList);
            })
            .catch((error) => {
                console.error("Error fetching chat list:", error);
            });
    }, []);

    useEffect(() => {
        if (chatLogRef.current) {
            chatLogRef.current.scrollTop = chatLogRef.current.scrollHeight;
        }
    }, [chatLog]);

    function clearChat() {
        setChatLog([]);
        setChatId(null);
    }

    async function handleSubmit(e: any) {
        e.preventDefault();
        let chatLogNew = [...chatLog, { user: "me", message: input }];
        setInput("");
        setIsLoading(true);
        try {
            if (!chatId) {
                createChat(userInfo.id)
                    .then(async (id) => {
                        const response = await postChatMessage(id, input.trim());
                        setChatLog([
                            ...chatLogNew,
                            { user: "gpt", message: `${response.message}` },
                        ]);

                        const responseChatList = await fetchChatList(userInfo.id);
                        setChatList(responseChatList);
                        setChatId(id);
                    })
                    .catch((error) => {
                        console.error(error);
                    })
                    .finally(() => {
                        setIsLoading(false);
                    });
            } else {
                const response = await postChatMessage(chatId, input.trim());
                setChatLog([...chatLogNew, { user: "gpt", message: `${response.message}` }]);
                setIsLoading(false);
            }
        } catch (error) {
            console.error(error);
            setIsLoading(false);
        }
    }

    async function handleDeleteClick(e: any, id: string) {
        e.stopPropagation();
        try {
            await deleteChat(id);
            const responseChatList = await fetchChatList(userInfo.id);
            setChatList(responseChatList);
        } catch (error) {
            console.error(error);
        }
    }

    async function handleLogoutClick() {
        localStorage.removeItem("token");
        localStorage.removeItem("userInfo");
        window.location.reload();
    }

    async function updateChatLogWithSelectedChat(id: string) {
        const selectedChat: ChatDb = await fetchChatById(id);

        let chatLogNew: ChatLog[] = [];
        selectedChat.message.forEach((message) => {
            chatLogNew.push({ user: "me", message: `${message.message}` });
            chatLogNew.push({ user: "gpt", message: `${message.response}` });
        });
        setChatLog(chatLogNew);
        setChatId(selectedChat.conversationId);
    }

    async function handleEditClick(e: any, convId: string) {
        e.stopPropagation();
        const alias = prompt("Enter a new alias for this chat:");
        if (alias) {
            try {
                await editAliasRequest(convId, alias);
                const responseChatList = await fetchChatList(userInfo.id);
                setChatList(responseChatList);
            } catch (error) {
                console.error(error);
            }
        }
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
                            key={chat.alias ? chat.alias : `${chat._id.slice(0, 15)}...`}
                            className="chat-list-item"
                            onClick={() => {
                                updateChatLogWithSelectedChat(chat.conversationId);
                            }}
                        >
                            {chat.alias ? chat.alias : `${chat._id.slice(0, 15)}...`}
                            <div className="button-container">
                                <span
                                    className="edit-button"
                                    onClick={(e) => handleEditClick(e, chat.conversationId)}
                                ></span>
                                <span
                                    className="close-button"
                                    onClick={(e) => handleDeleteClick(e, chat.conversationId)}
                                ></span>
                            </div>
                        </div>
                    ))}
                </div>

                {/* User Info and Logout Button */}
                <div className="user-info">
                    <p>User: {userInfo.username}</p>
                    <span className="logout-button" onClick={handleLogoutClick}>
                        <button>Logout</button>
                    </span>
                </div>
            </aside>
            <section className="chatbox">
                <div className="chat-log" ref={chatLogRef}>
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
                        {isLoading && <img src="loading.gif" alt="loading" />}
                    </form>
                </div>
            </section>
        </div>
    );
};

export default Chat;
