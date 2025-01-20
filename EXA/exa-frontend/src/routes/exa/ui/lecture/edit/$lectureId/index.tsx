import { zodResolver } from '@hookform/resolvers/zod'
import { createFileRoute, useNavigate, useParams } from '@tanstack/react-router'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { z } from 'zod'
import { useExaMutation } from '../../../../../../lib/use-exa-mutation'
import { useCourses } from '../../../../../../lib/use-courses'
import { useLectures } from '../../../../../../lib/use-lectures'
import { AuthorizationState, useAuthorizationState, parseCoursesXml, parseLecturesXml } from '../../../../../../lib/utils'

const lectureSchema = z.object({
    start: z.string().min(1, 'Start time is required'),
    end: z.string().min(1, 'End time is required'),
    room: z.string().min(1, 'Room is required'),
}).refine((data) => {
    const start = new Date(data.start)
    const end = new Date(data.end)
    return end > start
}, {
    message: "End time must be after start time",
    path: ["end"]
})

type LectureFormData = z.infer<typeof lectureSchema>

export const Route = createFileRoute('/exa/ui/lecture/edit/$lectureId/')({
    component: EditLectureComponent,
})

function EditLectureComponent() {
    const { lectureId } = useParams({ from: '/exa/ui/lecture/edit/$lectureId/' })
    const navigate = useNavigate()
    const { state: authState } = useAuthorizationState()
    const { data: coursesData, isLoading: coursesLoading } = useCourses()
    const { data: lecturesData, isLoading: lecturesLoading } = useLectures()
    const { mutate, isPending } = useExaMutation({
        endpoint: `http://localhost:8080/exa/lectures`,
        redirectTo: '/exa/ui/courses',
    })

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<LectureFormData>({
        resolver: zodResolver(lectureSchema),
        defaultValues: async () => {
            if (!lecturesData) return { start: '', end: '', room: '' }
            const lectures = parseLecturesXml(lecturesData)
            const lecture = lectures.find(l => l.id === lectureId)
            if (!lecture) return { start: '', end: '', room: '' }
            return {
                start: lecture.start || '',
                end: lecture.end || '',
                room: lecture.roomOrLink || '',
            }
        }
    })

    // Redirect non-admin/non-lecturer users
    if (authState !== AuthorizationState.Admin && authState !== AuthorizationState.Lecturer) {
        navigate({ to: '/exa/ui/courses' })
        return null
    }

    // Show loading state while fetching data
    if (coursesLoading || lecturesLoading) {
        return (
            <div className="flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        )
    }

    if (!coursesData || !lecturesData) {
        return (
            <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
                Failed to load required data
            </div>
        )
    }

    const lectures = parseLecturesXml(lecturesData)
    const lecture = lectures.find(l => l.id === lectureId)
    const courses = parseCoursesXml(coursesData)
    const course = courses.find(c => c.id === lecture?.course)

    if (!lecture || !course) {
        navigate({ to: '/exa/ui/courses' })
        return null
    }

    const onSubmit = async (data: LectureFormData) => {
        try {
            mutate({
                id: lecture.id || '',
                course: lecture.course || '',
                start: data.start,
                end: data.end,
                room: data.room,
            })
        } catch (error) {
            console.error('Error updating lecture:', error)
            toast.error('Error updating lecture')
        }
    }

    return (
        <div className="max-w-4xl mx-auto p-6">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900">Edit Lecture</h1>
                <p className="mt-2 text-sm text-gray-600">Update the details below to modify the lecture for {course.name}.</p>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="bg-white shadow-sm ring-1 ring-gray-900/5 sm:rounded-xl">
                <div className="px-4 py-6 sm:p-8">
                    <div className="grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-2">
                        <div>
                            <label htmlFor="start" className="block text-sm font-medium leading-6 text-gray-900">
                                Start Time
                            </label>
                            <div className="mt-2">
                                <input
                                    type="datetime-local"
                                    id="start"
                                    {...register('start')}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                />
                                {errors.start && (
                                    <p className="mt-2 text-sm text-red-600">{errors.start.message}</p>
                                )}
                            </div>
                        </div>

                        <div>
                            <label htmlFor="end" className="block text-sm font-medium leading-6 text-gray-900">
                                End Time
                            </label>
                            <div className="mt-2">
                                <input
                                    type="datetime-local"
                                    id="end"
                                    {...register('end')}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                />
                                {errors.end && (
                                    <p className="mt-2 text-sm text-red-600">{errors.end.message}</p>
                                )}
                            </div>
                        </div>

                        <div className="sm:col-span-2">
                            <label htmlFor="room" className="block text-sm font-medium leading-6 text-gray-900">
                                Room
                            </label>
                            <div className="mt-2">
                                <input
                                    type="text"
                                    id="room"
                                    {...register('room')}
                                    placeholder="e.g., A1.2.1"
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                />
                                {errors.room && (
                                    <p className="mt-2 text-sm text-red-600">{errors.room.message}</p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                <div className="flex items-center justify-end gap-x-4 border-t border-gray-900/10 px-4 py-4 sm:px-8">
                    <button
                        type="button"
                        onClick={() => navigate({ to: '/exa/ui/courses/$courseId', params: { courseId: course.id || '' } })}
                        className="rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        disabled={isSubmitting || isPending}
                        className="rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isSubmitting || isPending ? 'Saving...' : 'Save Changes'}
                    </button>
                </div>
            </form>
        </div>
    )
}
