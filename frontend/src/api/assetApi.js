import axiosClient from "./axiosClient";

const BASE_URL = "/assets";

/**
 * Get paginated assets
 */
export const getAssets = async (page = 0, size = 10) => {
    const response = await axiosClient.get(BASE_URL, {
        params: {
            page,
            size
        }
    });

    return response.data;
};

/**
 * Search Assets
 */
export const searchAssets = async (
    keyword,
    page = 0,
    size = 10
) => {

    const response = await axiosClient.get(
        `${BASE_URL}/search`,
        {
            params: {
                keyword,
                page,
                size
            }
        }
    );

    return response.data;

};

/**
 * Get Asset By Id
 */
export const getAssetById = async (id) => {
    const response = await axiosClient.get(`${BASE_URL}/${id}`);
    return response.data;
};

/**
 * Register Asset
 */
export const createAsset = async (asset) => {
    const response = await axiosClient.post(BASE_URL, asset);
    return response.data;
};

/**
 * Update Asset
 */
export const updateAsset = async (id, asset) => {
    const response = await axiosClient.put(`${BASE_URL}/${id}`, asset);
    return response.data;
};

/**
 * Delete Asset
 */
export const deleteAsset = async (id) => {
    await axiosClient.delete(`${BASE_URL}/${id}`);
};

/**
 * Change Asset Status
 */
export const changeAssetStatus = async (id, status) => {
    const response = await axiosClient.patch(
        `${BASE_URL}/${id}/status`,
        null,
        {
            params: {
                status
            }
        }
    );

    return response.data;
};