import { useQuery } from '@tanstack/react-query'
import { TMP_LECTURES } from './MOCKS'

export function useLectures() {
    return useQuery({
        queryKey: ['lectures'],
        queryFn: () => import.meta.env.PROD ? fetch('http://localhost:8080/exa/lectures').then(res => res.text()) : TMP_LECTURES,
    })
}