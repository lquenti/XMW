import { useQuery } from "@tanstack/react-query";
import { TMP_SEMESTERS } from "./MOCKS";

export const useSemesters = () =>
    useQuery({
        queryKey: ['semesters'],
        queryFn: () => TMP_SEMESTERS,
    });
