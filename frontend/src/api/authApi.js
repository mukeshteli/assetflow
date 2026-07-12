import axiosClient from './axiosClient';

export async function signup({ fullName, email, password }) {
    const { data } = await axiosClient.post('/auth/signup', { fullName, email, password });
    return data;
}

export async function login({ email, password }) {
    const { data } = await axiosClient.post('/auth/login', { email, password });
    return data;

}