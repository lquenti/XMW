import { createLazyFileRoute, useNavigate } from '@tanstack/react-router'
import { AuthorizationState, useAuthorizationState } from '../../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/exams/new')({
    component: NewExamComponent,
})

function NewExamComponent() {
    const navigate = useNavigate()
    const { state: authState } = useAuthorizationState()

    // Redirect non-admin/non-lecturer users
    if (authState !== AuthorizationState.Admin && authState !== AuthorizationState.Lecturer) {
        navigate({ to: '/exa/ui/exams' })
        return null
    }

    return (
        <div className="max-w-4xl mx-auto p-6">
            <h1 className="text-3xl font-bold text-gray-900 mb-8">New Exam</h1>
            {/* Exam form will be implemented here */}
            <div className="bg-white shadow-sm ring-1 ring-gray-900/5 sm:rounded-xl md:col-span-2">
                <div className="px-4 py-6 sm:p-8">
                    <div className="grid max-w-2xl grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-6">
                        <div className="col-span-full">
                            <p className="text-gray-500 italic">Exam form coming soon...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
} 