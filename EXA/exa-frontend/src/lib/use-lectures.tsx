import { useQuery } from '@tanstack/react-query'
import { TMP_LECTURES } from './MOCKS'

export function useLectures() {
    return useQuery({
        queryKey: ['lectures'],
        queryFn: async () => TMP_LECTURES,
    })
}