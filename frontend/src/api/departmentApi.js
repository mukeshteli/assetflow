import axiosClient from "./axiosClient";

const BASE_URL = "/departments";

export const getDepartments = async () => {

    const response = await axiosClient.get(BASE_URL);

    return response.data;

};