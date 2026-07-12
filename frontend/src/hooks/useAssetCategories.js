import { useQuery } from "@tanstack/react-query";
import { getCategories } from "../api/assetCategoryApi";

export const useAssetCategories = () => {

    return useQuery({

        queryKey: ["categories"],

        queryFn: getCategories

    });

};