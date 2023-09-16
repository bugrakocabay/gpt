import Message from "./Message";

interface ChatDb {
    _id: string;
    conversationId: string;
    message: Message[];
    status: boolean;
    alias: string;
}

export default ChatDb;