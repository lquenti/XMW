import { useQuery } from '@tanstack/react-query'
import { TMP_MODULES } from './MOCKS'

export function useModules() {
    return useQuery({
        queryKey: ['modules'],
        queryFn: () => import.meta.env.PROD ? fetch('http://localhost:8080/exa/modules').then(res => res.text()) : TMP_MODULES,
    })
}