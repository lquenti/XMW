import { useQuery } from '@tanstack/react-query'
import { TMP_LECTURERS } from './MOCKS'

export function useLecturers() {
  return useQuery({
    queryKey: ['lecturers'],
    queryFn: async () => TMP_LECTURERS,
  })
} 