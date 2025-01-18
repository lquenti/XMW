import { useQuery } from "@tanstack/react-query";
import { TMP_COURSES } from "./MOCKS";

const useCourses = () =>
    useQuery({
        queryKey: ['courses'],
        queryFn: () => TMP_COURSES,
    });

export { useCourses };
