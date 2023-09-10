import {Chat, Login} from "./routes"
import { BrowserRouter, Routes, Route } from "react-router-dom";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/chat" Component={Chat} />
                <Route path="/login" Component={Login} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
