import '../styles/Login.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login } from '../services/loginApi';

const Login = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
    });

    const navigator = useNavigate();

    const handleChange = (e: any) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = async (e: any) => {
        e.preventDefault();

        const response = await login(formData.username, formData.password);

        if (response) {
            localStorage.setItem('token', response.token);
            navigator('/chat');
        } else {
            console.error('Login failed');
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
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
        </div>
    );
}

export default Login;
