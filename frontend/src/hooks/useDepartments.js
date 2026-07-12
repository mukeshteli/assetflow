import { useQuery } from "@tanstack/react-query";
import { getDepartments } from "../api/departmentApi";

export const useDepartments = () => {

    return useQuery({

        queryKey: ["departments"],

        queryFn: getDepartments

    });

};