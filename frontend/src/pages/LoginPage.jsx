import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useLoginMutation } from '../hooks/useAuthMutations';
import { useAuth } from '../context/AuthContext';
import { getApiErrorMessage } from '../utils/apiError';

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();
    const loginMutation = useLoginMutation();

    function handleSubmit(event) {
        event.preventDefault();
        loginMutation.mutate(
            { email, password },
            {
                onSuccess: (data) => {
                    login(data.token);
                    navigate('/dashboard');
                },
            },
        );
    }

    return (
        <div className="auth-page">
            <h1>AssetFlow — login</h1>

            <form onSubmit={handleSubmit}>
                <label htmlFor="email">Email</label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="name@company.com"
                    required
                />

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '12px' }}>
                  <label htmlFor="password" style={{ margin: 0 }}>Password</label>
                  <a
                    href="#"
                    onClick={(e) => { e.preventDefault(); alert("Please contact your IT administrator (admin@assetflow.com) to reset your password."); }}
                    style={{ fontSize: '13px', color: '#3b82f6', textDecoration: 'none' }}
                  >
                    Forgot password?
                  </a>
                </div>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                {loginMutation.isError && (
                    <p className="form-error">{getApiErrorMessage(loginMutation.error)}</p>
                )}

                <button type="submit" disabled={loginMutation.isPending}>
                    {loginMutation.isPending ? 'Logging in…' : 'Login'}
                </button>
            </form>

            <p style={{ marginTop: '20px' }}>
                New here? <Link to="/signup">Create account</Link>
            </p>
            <p className="auth-hint">Note: Sign up creates an employee account. Admin roles are assigned later by system administrators.</p>
        </div>
    );
}