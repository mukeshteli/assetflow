import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useSignupMutation } from '../hooks/useAuthMutations';
import { useAuth } from '../context/AuthContext';
import { getApiErrorMessage, getApiFieldErrors } from '../utils/apiError';

export default function SignupPage() {
    const [fullName, setFullName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();
    const signupMutation = useSignupMutation();
    const fieldErrors = getApiFieldErrors(signupMutation.error);

    function handleSubmit(event) {
        event.preventDefault();
        signupMutation.mutate(
            { fullName, email, password },
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
            <h1>AssetFlow — create account</h1>
            <p className="auth-hint">Sign up creates an employee account — admin roles assigned later.</p>

            <form onSubmit={handleSubmit}>
                <label htmlFor="fullName">Full name</label>
                <input
                    id="fullName"
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                />
                {fieldErrors?.fullName && <p className="field-error">{fieldErrors.fullName}</p>}

                <label htmlFor="email">Email</label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="name@company.com"
                    required
                />
                {fieldErrors?.email && <p className="field-error">{fieldErrors.email}</p>}

                <label htmlFor="password">Password</label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                {fieldErrors?.password && <p className="field-error">{fieldErrors.password}</p>}

                {signupMutation.isError && !fieldErrors && (
                    <p className="form-error">{getApiErrorMessage(signupMutation.error)}</p>
                )}

                <button type="submit" disabled={signupMutation.isPending}>
                    {signupMutation.isPending ? 'Creating account…' : 'Create account'}
                </button>
            </form>

            <p>
                Already have an account? <Link to="/login">Login</Link>
            </p>
        </div>
    );
}