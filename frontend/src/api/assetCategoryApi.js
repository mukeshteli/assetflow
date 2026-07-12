import axiosClient from "./axiosClient";

const BASE_URL = "/categories";

export const getCategories = async () => {

    const response = await axiosClient.get(BASE_URL);

    return response.data;

};