import { Chat, Login, Register } from "./routes";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

const isAuthenticated = () => {
    const token = localStorage.getItem("token");
    return !!token;
};

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route
                    path="/chat"
                    element={isAuthenticated() ? <Chat /> : <Navigate to="/login" />}
                />
                <Route
                    path="/login"
                    element={isAuthenticated() ? <Navigate to="/chat" /> : <Login />}
                />
                <Route path="/register" Component={Register} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
