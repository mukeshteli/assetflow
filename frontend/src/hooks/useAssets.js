import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
    getAssets,
    getAssetById,
    createAsset,
    updateAsset,
    deleteAsset,
    changeAssetStatus,
    searchAssets
} from "../api/assetApi";

/**
 * Get All Assets
 */
export const useAssets = (page = 0, size = 10) => {

    return useQuery({

        queryKey: ["assets", page, size],

        queryFn: () => getAssets(page, size)

    });

};

/**
 * Search Assets
 */
export const useSearchAssets = (
    keyword,
    page = 0,
    size = 10
) => {

    return useQuery({

        queryKey: [
            "search-assets",
            keyword,
            page,
            size
        ],

        queryFn: () =>
            searchAssets(
                keyword,
                page,
                size
            ),

        enabled: keyword.trim().length > 0

    });

};

/**
 * Get Asset By Id
 */
export const useAsset = (id) => {

    return useQuery({

        queryKey: ["asset", id],

        queryFn: () => getAssetById(id),

        enabled: !!id

    });

};

/**
 * Create Asset
 */
export const useCreateAsset = () => {

    const queryClient = useQueryClient();

    return useMutation({

        mutationFn: createAsset,

        onSuccess: () => {

            queryClient.invalidateQueries({
                queryKey: ["assets"]
            });

            queryClient.invalidateQueries({
                queryKey: ["search-assets"]
            });

        }

    });

};

/**
 * Update Asset
 */
export const useUpdateAsset = () => {

    const queryClient = useQueryClient();

    return useMutation({

        mutationFn: ({ id, asset }) =>
            updateAsset(id, asset),

        onSuccess: () => {

            queryClient.invalidateQueries({
                queryKey: ["assets"]
            });

            queryClient.invalidateQueries({
                queryKey: ["search-assets"]
            });

        }

    });

};

/**
 * Delete Asset
 */
export const useDeleteAsset = () => {

    const queryClient = useQueryClient();

    return useMutation({

        mutationFn: deleteAsset,

        onSuccess: () => {

            queryClient.invalidateQueries({
                queryKey: ["assets"]
            });

            queryClient.invalidateQueries({
                queryKey: ["search-assets"]
            });

        }

    });

};

/**
 * Change Asset Status
 */
export const useChangeStatus = () => {

    const queryClient = useQueryClient();

    return useMutation({

        mutationFn: ({ id, status }) =>
            changeAssetStatus(id, status),

        onSuccess: () => {

            queryClient.invalidateQueries({
                queryKey: ["assets"]
            });

            queryClient.invalidateQueries({
                queryKey: ["search-assets"]
            });

        }

    });

};