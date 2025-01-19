import { useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from '@tanstack/react-router'
import { toast } from 'sonner'

export const useExaMutation = ({
    endpoint,
    redirectTo,
}: {
    endpoint: string
    redirectTo: string
}) => {
    const navigate = useNavigate()
    const queryClient = useQueryClient()
    return useMutation({
        mutationFn: async (data: Record<string, string>) => {
            const formData = new URLSearchParams(data)

            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData.toString(),
            })

            if (!response.ok) {
                throw new Error('Failed to create course')
            }

            return response.text()
        },
        onSuccess: () => {
            navigate({ to: redirectTo })
            toast.success('Operation successful')
            queryClient.invalidateQueries()
        },
        onError: (error) => {
            console.error('Error creating course:', error)
            toast.error('Operation failed')
        },
    })
}
