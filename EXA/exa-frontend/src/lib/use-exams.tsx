import { useQuery } from "@tanstack/react-query";
import { TMP_EXAMS } from "./MOCKS";

const useExams = () =>
    useQuery({
        queryKey: ['exams'],
        queryFn: () => import.meta.env.PROD ? fetch('http://localhost:8080/exa/exams').then(res => res.text()) : TMP_EXAMS,
    });

export { useExams };
