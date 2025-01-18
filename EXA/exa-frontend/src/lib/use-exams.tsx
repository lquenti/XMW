import { useQuery } from "@tanstack/react-query";
import { TMP_EXAMS } from "./MOCKS";

const useExams = () =>
    useQuery({
        queryKey: ['exams'],
        queryFn: () => TMP_EXAMS,
    });

export { useExams };
