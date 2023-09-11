import '../styles/Login.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services';


const Login = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
    });

    const [errorMessage, setErrorMessage] = useState(''); // State for error message

    const navigator = useNavigate();

    const handleChange = (e: any) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e: any) => {
        e.preventDefault();

        const response = await login(formData.username, formData.password);

        if (response?.status === 200) {
            const body = await response.json();
            localStorage.setItem('token', body.token);
            navigator('/chat');
        } else {
            const errorBody = await response?.json();
            setErrorMessage(errorBody.message);
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                {/* Error message */}
                {errorMessage && <div className="error-message">{errorMessage}</div>}

                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit">Login</button>
            </form>
            <p className="register-message">
                Don't have an account? <a href="/register">Create one</a>.
            </p>
        </div>
    );
};

export default Login;

