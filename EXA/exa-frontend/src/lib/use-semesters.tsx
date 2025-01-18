import { useQuery } from "@tanstack/react-query";
import { TMP_SEMESTERS } from "./MOCKS";

export const useSemesters = () =>
    useQuery({
        queryKey: ['semesters'],
        queryFn: () => import.meta.env.PROD ? fetch('http://localhost:8080/exa/semesters').then(res => res.text()) : TMP_SEMESTERS,
    });