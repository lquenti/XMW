import { useQuery } from '@tanstack/react-query'
import { TMP_LECTURERS } from './MOCKS'

export function useLecturers() {
  return useQuery({
    queryKey: ['lecturers'],
    queryFn: () => import.meta.env.PROD ? fetch('http://localhost:8080/exa/lecturers').then(res => res.text()) : TMP_LECTURERS,
  })
} 