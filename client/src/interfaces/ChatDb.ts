import Message from "./Message";

interface ChatDb {
    _id: string;
    conversationId: string;
    message: Message[];
}

export default ChatDb;