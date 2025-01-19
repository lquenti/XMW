import { zodResolver } from '@hookform/resolvers/zod'
import { createLazyFileRoute, useNavigate } from '@tanstack/react-router'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { z } from 'zod'
import { useCourses } from '../../../../lib/use-courses'
import { useExaMutation } from '../../../../lib/use-exa-mutation'
import { AuthorizationState, parseCoursesXml, useAuthorizationState } from '../../../../lib/utils'

const examSchema = z.object({
    course: z.string().min(1, 'Please select a course'),
    date: z.string().min(1, 'Please select a date and time'),
    isOnline: z.boolean(),
    isWritten: z.boolean(),
    roomOrLink: z.string().min(1, 'Please provide a room or link'),
})

type ExamFormData = z.infer<typeof examSchema>

export const Route = createLazyFileRoute('/exa/ui/exams/new')({
    component: NewExamComponent,
})

function NewExamComponent() {
    const navigate = useNavigate()
    const { state: authState } = useAuthorizationState()
    const { data: coursesData, isLoading: coursesLoading } = useCourses()
    const { mutate, isPending } = useExaMutation({
        endpoint: 'http://localhost:8080/exa/exams',
        redirectTo: '/exa/ui/exams',
    })

    const {
        register,
        handleSubmit,
        watch,
        formState: { errors, isSubmitting },
    } = useForm<ExamFormData>({
        resolver: zodResolver(examSchema),
        defaultValues: {
            isOnline: false,
            isWritten: true,
        },
    })

    // Redirect if not authorized
    if (authState !== AuthorizationState.Admin && authState !== AuthorizationState.Lecturer) {
        navigate({ to: '/exa/ui/exams' })
        return null
    }

    if (coursesLoading) {
        return (
            <div className="flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        )
    }

    if (!coursesData) {
        return (
            <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
                No course data available
            </div>
        )
    }

    const courses = parseCoursesXml(coursesData)
    const isOnline = watch('isOnline')

    const onSubmit = async (data: ExamFormData) => {
        try {
            mutate({
                course: data.course,
                date: data.date,
                is_online: data.isOnline ? '1' : '0',
                is_written: data.isWritten ? '1' : '0',
                room_or_link: data.roomOrLink,
            })
        } catch (error) {
            console.error('Error creating exam:', error)
            toast.error('Error creating exam')
        }
    }

    return (
        <div className="max-w-3xl mx-auto p-6">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900">Create New Exam</h1>
                <p className="mt-2 text-sm text-gray-600">Fill in the details below to create a new exam.</p>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="bg-white rounded-xl p-8 shadow-sm space-y-8">
                <div className="grid grid-cols-1 gap-x-6 gap-y-8">
                    <div>
                        <label htmlFor="course" className="block text-sm font-medium leading-6 text-gray-900">
                            Course
                        </label>
                        <div className="relative mt-2">
                            <select
                                id="course"
                                {...register('course')}
                                className="block w-full rounded-md border-0 py-2.5 pl-3 pr-10 text-gray-900 ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                            >
                                <option value="">Select a course</option>
                                {courses.map(course => (
                                    <option key={course.id} value={course.id ?? ''}>
                                        {course.name}
                                    </option>
                                ))}
                            </select>
                            {errors.course && (
                                <p className="mt-2 text-sm text-red-600">{errors.course.message}</p>
                            )}
                        </div>
                    </div>

                    <div>
                        <label htmlFor="date" className="block text-sm font-medium leading-6 text-gray-900">
                            Date and Time
                        </label>
                        <div className="mt-2">
                            <input
                                type="datetime-local"
                                id="date"
                                {...register('date')}
                                className="block w-full rounded-md border-0 py-2.5 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                            />
                            {errors.date && (
                                <p className="mt-2 text-sm text-red-600">{errors.date.message}</p>
                            )}
                        </div>
                    </div>

                    <fieldset>
                        <legend className="text-sm font-medium leading-6 text-gray-900">Exam Type</legend>
                        <div className="mt-4 space-y-4 md:flex md:items-center md:space-y-0 md:space-x-10">
                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="isWritten"
                                    {...register('isWritten')}
                                    className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-600"
                                />
                                <label htmlFor="isWritten" className="ml-3 block text-sm font-medium leading-6 text-gray-900">
                                    Written Exam
                                </label>
                            </div>

                            <div className="flex items-center">
                                <input
                                    type="checkbox"
                                    id="isOnline"
                                    {...register('isOnline')}
                                    className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-600"
                                />
                                <label htmlFor="isOnline" className="ml-3 block text-sm font-medium leading-6 text-gray-900">
                                    Online Exam
                                </label>
                            </div>
                        </div>
                    </fieldset>

                    <div>
                        <label htmlFor="roomOrLink" className="block text-sm font-medium leading-6 text-gray-900">
                            {isOnline ? 'Link to Exam' : 'Room Number'}
                        </label>
                        <div className="mt-2">
                            <div className="relative rounded-md">
                                {isOnline && (
                                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                                        <svg className="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
                                        </svg>
                                    </div>
                                )}
                                <input
                                    type="text"
                                    id="roomOrLink"
                                    {...register('roomOrLink')}
                                    placeholder={isOnline ? 'https://...' : 'e.g., A1.2.1'}
                                    className={`block w-full rounded-md border-0 py-2.5 text-gray-900 ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6 ${isOnline ? 'pl-10' : ''}`}
                                />
                            </div>
                            {errors.roomOrLink && (
                                <p className="mt-2 text-sm text-red-600">{errors.roomOrLink.message}</p>
                            )}
                        </div>
                    </div>
                </div>

                <div className="flex items-center justify-end space-x-4 pt-4 border-t border-gray-200">
                    <button
                        type="button"
                        onClick={() => navigate({ to: '/exa/ui/exams' })}
                        className="rounded-md bg-white px-4 py-2.5 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        disabled={isSubmitting || isPending}
                        className="rounded-md bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isSubmitting || isPending ? 'Creating...' : 'Create Exam'}
                    </button>
                </div>
            </form>
        </div>
    )
} 