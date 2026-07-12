import { useMutation } from '@tanstack/react-query';
import { signup, login } from '../api/authApi';

export function useSignupMutation() {
    return useMutation({ mutationFn: signup });
}

export function useLoginMutation() {
    return useMutation({ mutationFn: login });
}