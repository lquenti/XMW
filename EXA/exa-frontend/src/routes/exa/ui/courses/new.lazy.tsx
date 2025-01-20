import { zodResolver } from '@hookform/resolvers/zod'
import { createLazyFileRoute, useNavigate } from '@tanstack/react-router'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { z } from 'zod'
import { useExaMutation } from '../../../../lib/use-exa-mutation'
import { useLecturers } from '../../../../lib/use-lecturers'
import { useSemesters } from '../../../../lib/use-semesters'
import { AuthorizationState, useAuthorizationState, parseSemestersXml, parseLecturersXml } from '../../../../lib/utils'

const courseSchema = z.object({
    name: z.string().min(1, 'Course name is required'),
    faculty: z.string().min(1, 'Faculty is required'),
    maxStudents: z.string()
        .min(1, 'Maximum number of students is required')
        .refine((val) => !isNaN(Number(val)), 'Must be a valid number')
        .refine((val) => Number(val) > 0, 'Must be greater than 0'),
    semester: z.string().min(1, 'Semester is required'),
    lecturer: z.string().min(1, 'Lecturer is required'),
})

type CourseFormData = z.infer<typeof courseSchema>

export const Route = createLazyFileRoute('/exa/ui/courses/new')({
    component: NewCourseComponent,
})

function NewCourseComponent() {
    const navigate = useNavigate()
    const { state: authState } = useAuthorizationState()
    const { data: semestersData, isLoading: semestersLoading } = useSemesters()
    const { data: lecturersData, isLoading: lecturersLoading } = useLecturers()
    const { mutate, isPending } = useExaMutation({
        endpoint: 'http://localhost:8080/exa/courses',
        redirectTo: '/exa/ui/courses',
    })

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<CourseFormData>({
        resolver: zodResolver(courseSchema),
        defaultValues: {
            maxStudents: '30', // Default value for max students
        }
    })

    // Redirect non-admin/non-lecturer users
    if (authState !== AuthorizationState.Admin && authState !== AuthorizationState.Lecturer) {
        navigate({ to: '/exa/ui/courses' })
        return null
    }

    // Show loading state while fetching data
    if (semestersLoading || lecturersLoading) {
        return (
            <div className="flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        )
    }

    if (!semestersData || !lecturersData) {
        return (
            <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
                Failed to load required data
            </div>
        )
    }

    const semesters = parseSemestersXml(semestersData)
    const lecturers = parseLecturersXml(lecturersData)

    const onSubmit = async (data: CourseFormData) => {
        try {
            mutate({
                name: data.name,
                faculty: data.faculty,
                max_students: data.maxStudents,
                semester: data.semester,
                lecturer: data.lecturer,
            })
        } catch (error) {
            console.error('Error creating course:', error)
            toast.error('Error creating course')
        }
    }

    return (
        <div className="max-w-4xl mx-auto p-6">
            <div className="mb-8">
                <h1 className="text-3xl font-bold text-gray-900">Create New Course</h1>
                <p className="mt-2 text-sm text-gray-600">Fill in the details below to create a new course.</p>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="bg-white shadow-sm ring-1 ring-gray-900/5 sm:rounded-xl">
                <div className="px-4 py-6 sm:p-8">
                    <div className="grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-2">
                        <div className="sm:col-span-2">
                            <label htmlFor="name" className="block text-sm font-medium leading-6 text-gray-900">
                                Course Name
                            </label>
                            <div className="mt-2">
                                <input
                                    type="text"
                                    id="name"
                                    {...register('name')}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                />
                                {errors.name && (
                                    <p className="mt-2 text-sm text-red-600">{errors.name.message}</p>
                                )}
                            </div>
                        </div>

                        <div>
                            <label htmlFor="faculty" className="block text-sm font-medium leading-6 text-gray-900">
                                Faculty
                            </label>
                            <div className="mt-2">
                                <input
                                    type="text"
                                    id="faculty"
                                    {...register('faculty')}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                />
                                {errors.faculty && (
                                    <p className="mt-2 text-sm text-red-600">{errors.faculty.message}</p>
                                )}
                            </div>
                        </div>

                        <div>
                            <label htmlFor="maxStudents" className="block text-sm font-medium leading-6 text-gray-900">
                                Maximum Students
                            </label>
                            <div className="mt-2">
                                <input
                                    type="number"
                                    id="maxStudents"
                                    min="1"
                                    {...register('maxStudents')}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                />
                                {errors.maxStudents && (
                                    <p className="mt-2 text-sm text-red-600">{errors.maxStudents.message}</p>
                                )}
                            </div>
                        </div>

                        <div>
                            <label htmlFor="lecturer" className="block text-sm font-medium leading-6 text-gray-900">
                                Lecturer
                            </label>
                            <div className="mt-2">
                                <select
                                    id="lecturer"
                                    {...register('lecturer')}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                >
                                    <option value="">Select a lecturer</option>
                                    {lecturers.map(lecturer => (
                                        <option key={lecturer.id ?? ''} value={lecturer.id ?? ''}>
                                            {lecturer.firstname} {lecturer.name} ({lecturer.faculty || 'No Faculty'})
                                        </option>
                                    ))}
                                </select>
                                {errors.lecturer && (
                                    <p className="mt-2 text-sm text-red-600">{errors.lecturer.message}</p>
                                )}
                            </div>
                        </div>

                        <div>
                            <label htmlFor="semester" className="block text-sm font-medium leading-6 text-gray-900">
                                Semester
                            </label>
                            <div className="mt-2">
                                <select
                                    id="semester"
                                    {...register('semester')}
                                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                                >
                                    <option value="">Select a semester</option>
                                    {semesters.map(semester => (
                                        <option key={semester.id ?? ''} value={semester.id ?? ''}>
                                            {semester.name ?? ''}
                                        </option>
                                    ))}
                                </select>
                                {errors.semester && (
                                    <p className="mt-2 text-sm text-red-600">{errors.semester.message}</p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                <div className="flex items-center justify-end gap-x-4 border-t border-gray-900/10 px-4 py-4 sm:px-8">
                    <button
                        type="button"
                        onClick={() => navigate({ to: '/exa/ui/courses' })}
                        className="rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        disabled={isSubmitting || isPending}
                        className="rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isSubmitting || isPending ? 'Creating...' : 'Create Course'}
                    </button>
                </div>
            </form>
        </div>
    )
} 