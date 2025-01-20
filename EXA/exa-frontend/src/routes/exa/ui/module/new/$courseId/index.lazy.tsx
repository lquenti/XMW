import { zodResolver } from '@hookform/resolvers/zod'
import {
  createLazyFileRoute,
  useNavigate,
  useParams,
} from '@tanstack/react-router'
import { useForm } from 'react-hook-form'
import { toast } from 'sonner'
import { z } from 'zod'
import { useExaMutation } from '../../../../../../lib/use-exa-mutation'
import { useCourses } from '../../../../../../lib/use-courses'
import {
  AuthorizationState,
  useAuthorizationState,
  parseCoursesXml,
} from '../../../../../../lib/utils'

const moduleSchema = z.object({
  credits: z
    .string()
    .min(1, 'Credits are required')
    .refine((val) => !isNaN(Number(val)), 'Must be a valid number')
    .refine((val) => Number(val) > 0, 'Must be greater than 0'),
  name: z.string().min(1, 'Module name is required'),
  studies: z.string().min(1, 'Studies are required'),
  description: z.string().optional(),
})

type ModuleFormData = z.infer<typeof moduleSchema>

export const Route = createLazyFileRoute('/exa/ui/module/new/$courseId/')({
  component: NewModuleComponent,
})

function NewModuleComponent() {
  const { courseId } = useParams({ from: '/exa/ui/module/new/$courseId/' })
  const navigate = useNavigate()
  const { state: authState } = useAuthorizationState()
  const { data: coursesData, isLoading: coursesLoading } = useCourses()
  const { mutate, isPending } = useExaMutation({
    endpoint: 'http://localhost:8080/exa/modules',
    redirectTo: `/exa/ui/courses/${courseId}`,
  })

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ModuleFormData>({
    resolver: zodResolver(moduleSchema),
    defaultValues: {
      description: 'No description provided',
    },
  })

  // Redirect non-admin/non-lecturer users
  if (
    authState !== AuthorizationState.Admin &&
    authState !== AuthorizationState.Lecturer
  ) {
    navigate({ to: '/exa/ui/courses' })
    return null
  }

  // Show loading state while fetching data
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
        Failed to load required data
      </div>
    )
  }

  const courses = parseCoursesXml(coursesData)
  const course = courses.find((c) => c.id === courseId)

  if (!course) {
    navigate({ to: '/exa/ui/courses' })
    return null
  }

  const onSubmit = async (data: ModuleFormData) => {
    try {
      mutate({
        course: courseId,
        credits: data.credits,
        name: data.name,
        studies: data.studies.split(',').map(s => s.trim()).filter(Boolean).join(','),
        description: data.description || 'No description provided',
      })
    } catch (error) {
      console.error('Error creating module:', error)
      toast.error('Error creating module')
    }
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">Create New Module</h1>
        <p className="mt-2 text-sm text-gray-600">
          Fill in the details below to create a new module for {course.name}.
        </p>
      </div>

      <form
        onSubmit={handleSubmit(onSubmit)}
        className="bg-white shadow-sm ring-1 ring-gray-900/5 sm:rounded-xl"
      >
        <div className="px-4 py-6 sm:p-8">
          <div className="grid grid-cols-1 gap-x-6 gap-y-8 sm:grid-cols-2">
            <div className="sm:col-span-2">
              <label
                htmlFor="name"
                className="block text-sm font-medium leading-6 text-gray-900"
              >
                Module Name
              </label>
              <div className="mt-2">
                <input
                  type="text"
                  id="name"
                  {...register('name')}
                  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                />
                {errors.name && (
                  <p className="mt-2 text-sm text-red-600">
                    {errors.name.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <label
                htmlFor="credits"
                className="block text-sm font-medium leading-6 text-gray-900"
              >
                Credits
              </label>
              <div className="mt-2">
                <input
                  type="number"
                  id="credits"
                  min="1"
                  {...register('credits')}
                  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                />
                {errors.credits && (
                  <p className="mt-2 text-sm text-red-600">
                    {errors.credits.message}
                  </p>
                )}
              </div>
            </div>

            <div>
              <label
                htmlFor="studies"
                className="block text-sm font-medium leading-6 text-gray-900"
              >
                Studies
              </label>
              <div className="mt-2">
                <input
                  type="text"
                  id="studies"
                  {...register('studies')}
                  placeholder="Enter study programs (comma-separated)"
                  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                />
                {errors.studies && (
                  <p className="mt-2 text-sm text-red-600">
                    {errors.studies.message}
                  </p>
                )}
                <p className="mt-1 text-sm text-gray-500">Separate multiple study programs with commas (e.g., "CS, IT, SE")</p>
              </div>
            </div>

            <div className="sm:col-span-2">
              <label
                htmlFor="description"
                className="block text-sm font-medium leading-6 text-gray-900"
              >
                Description
              </label>
              <div className="mt-2">
                <textarea
                  id="description"
                  rows={4}
                  {...register('description')}
                  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-blue-600 sm:text-sm sm:leading-6"
                />
                {errors.description && (
                  <p className="mt-2 text-sm text-red-600">
                    {errors.description.message}
                  </p>
                )}
              </div>
            </div>
          </div>
        </div>

        <div className="flex items-center justify-end gap-x-4 border-t border-gray-900/10 px-4 py-4 sm:px-8">
          <button
            type="button"
            onClick={() =>
              navigate({
                to: '/exa/ui/courses/$courseId',
                params: { courseId },
              })
            }
            className="rounded-md bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isSubmitting || isPending}
            className="rounded-md bg-blue-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-blue-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isSubmitting || isPending ? 'Creating...' : 'Create Module'}
          </button>
        </div>
      </form>
    </div>
  )
}
