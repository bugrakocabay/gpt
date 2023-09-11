import '../styles/Register.css';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { register } from '../services';

function Register() {
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

        const response = await register(formData.username, formData.password);

        if (response) {
            navigator('/login');
        } else {
            console.error('Registration failed');
        }
    };

    return (
        <div className="register-container">
            <h2>Register</h2>
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
                <button type="submit">Register</button>
            </form>
            <p className="register-message">
                Already have an account? <a href="/login">Login here</a>.
            </p>
        </div>
    );
}

export default Register;
