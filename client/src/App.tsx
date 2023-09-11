import {Chat, Login, Register} from "./routes"
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/chat" Component={Chat} />
                <Route path="/login" Component={Login} />
                <Route path="/register" Component={Register} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
