import { createLazyFileRoute, Link } from '@tanstack/react-router'
import { useCourses } from '../../../../lib/use-courses'
import { useLecturers } from '../../../../lib/use-lecturers'
import { parseCoursesXml, parseLecturersXml } from '../../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/lecturers/')({
  component: LecturersOverviewComponent,
})

function LecturersOverviewComponent() {
  const { data: lecturersData, isLoading: lecturersLoading } = useLecturers()
  const { data: coursesData, isLoading: coursesLoading } = useCourses()

  if (lecturersLoading || coursesLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  if (!lecturersData || !coursesData) {
    return (
      <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
        No data available
      </div>
    )
  }

  const lecturers = parseLecturersXml(lecturersData)
  const courses = parseCoursesXml(coursesData)

  // Group lecturers by faculty
  const lecturersByFaculty = lecturers.reduce((acc, lecturer) => {
    const faculty = lecturer.faculty ?? 'Other'
    if (!acc[faculty]) {
      acc[faculty] = []
    }
    acc[faculty].push(lecturer)
    return acc
  }, {} as Record<string, typeof lecturers>)

  // Sort faculties alphabetically
  const sortedFaculties = Object.keys(lecturersByFaculty).sort()

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="mb-8">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Lecturers</h1>
        </div>

        <div className="space-y-8">
          {sortedFaculties.map(faculty => (
            <div key={faculty} className="bg-white rounded-xl p-6 shadow-sm">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 pb-2 border-b">
                {faculty}
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {lecturersByFaculty[faculty].map(lecturer => {
                  const lecturerCourses = courses.filter(c => c.lecturer === lecturer.id)

                  return (
                    <Link
                      key={lecturer.id}
                      to="/exa/ui/lecturers/$lecturerId"
                      params={{ lecturerId: lecturer.id ?? '' }}
                      className="block p-4 rounded-lg border border-gray-200 hover:border-blue-300 transition-colors"
                    >
                      <div className="flex items-start space-x-4">
                        <img
                          src={`https://i.pravatar.cc/150?u=${lecturer.id}`}
                          alt={`${lecturer.firstname} ${lecturer.name}`}
                          className="w-16 h-16 rounded-full object-cover"
                        />
                        <div>
                          <h3 className="text-lg font-semibold text-gray-900">
                            {lecturer.firstname} {lecturer.name}
                          </h3>
                          <div className="mt-1 space-y-1">
                            <div className="text-sm text-gray-600">
                              {lecturer.username}
                            </div>
                            <div className="text-sm text-gray-600">
                              {lecturerCourses.length} {lecturerCourses.length === 1 ? 'course' : 'courses'}
                            </div>
                          </div>
                        </div>
                      </div>
                    </Link>
                  )
                })}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
