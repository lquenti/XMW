import { useQuery } from "@tanstack/react-query";
import { TMP_COURSES } from "./MOCKS";

const useCourses = () =>
    useQuery({
        queryKey: ['courses'],
        queryFn: () => import.meta.env.PROD ? fetch('http://localhost:8080/exa/courses').then(res => res.text()) : TMP_COURSES,
    });

export { useCourses };
