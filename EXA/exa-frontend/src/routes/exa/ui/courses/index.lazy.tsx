import { createLazyFileRoute, Link } from '@tanstack/react-router'
import { useState } from 'react'
import { useCourses } from '../../../../lib/use-courses'
import { useLecturers } from '../../../../lib/use-lecturers'
import { useSemesters } from '../../../../lib/use-semesters'
import { AuthorizationState, parseCoursesXml, parseLecturersXml, parseSemestersXml, useAuthorizationState } from '../../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/courses/')({
  component: RouteComponent,
})

function RouteComponent() {
  const { data, isLoading, error } = useCourses()
  const { data: semesters, isLoading: semestersLoading, error: semestersError } = useSemesters()
  const { data: lecturers, isLoading: lecturersLoading, error: lecturersError } = useLecturers()
  const [facultyFilter, setFacultyFilter] = useState<string>('all')
  const { state: authState } = useAuthorizationState()

  if (isLoading || semestersLoading || lecturersLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  if (error || semestersError || lecturersError) {
    return (
      <div className="p-4 bg-red-50 text-red-600 rounded-lg">
        Error: {error?.message || semestersError?.message || lecturersError?.message}
      </div>
    )
  }

  if (!data || !semesters || !lecturers) {
    return (
      <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
        No course data available
      </div>
    )
  }

  const courses = parseCoursesXml(data)
  const semestersList = parseSemestersXml(semesters)
  const lecturersList = parseLecturersXml(lecturers)

  // Get unique faculties for filter
  const faculties = Array.from(new Set(courses.map(course => course.faculty ?? ''))).sort()

  // Filter courses by faculty if needed
  const filteredCourses = facultyFilter === 'all'
    ? courses
    : courses.filter(course => course.faculty === facultyFilter)

  // Group courses by semester
  const coursesBySemester = filteredCourses.reduce<Record<string, typeof courses>>((acc, course) => {
    if (course.semester) {
      acc[course.semester] = acc[course.semester] || []
      acc[course.semester].push(course)
    }
    return acc
  }, {})

  const canCreateCourse = authState === AuthorizationState.Admin || authState === AuthorizationState.Lecturer

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-3xl font-bold text-gray-900">Courses</h1>
          {canCreateCourse && (
            <Link
              to="/exa/ui/courses/new"
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2"
            >
              <svg
                className="w-5 h-5"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 6v6m0 0v6m0-6h6m-6 0H6"
                />
              </svg>
              <span>New Course</span>
            </Link>
          )}
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4 mb-6">
          <select
            className="px-4 py-2 rounded-lg border border-gray-300 bg-white"
            value={facultyFilter}
            onChange={(e) => setFacultyFilter(e.target.value)}
          >
            <option value="all">All Faculties</option>
            {faculties.map((faculty) => (
              <option key={faculty} value={faculty}>
                {faculty}
              </option>
            ))}
          </select>
        </div>

        {/* Results count */}
        <p className="text-gray-600 mb-4">
          Showing {filteredCourses.length} course
          {filteredCourses.length !== 1 ? 's' : ''}
        </p>
      </div>

      {/* Courses by semester */}
      <div className="space-y-8">
        {Object.entries(coursesBySemester).map(
          ([semester, semesterCourses]) => (
            <div key={semester} className="bg-white rounded-xl p-6 shadow-sm">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">
                {semestersList.find((s) => s.id === semester)?.name}
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {semesterCourses.map((course) => {
                  const lecturer = lecturersList.find(
                    (l) => l.id === course.lecturer,
                  )
                  return (
                    <Link
                      key={course.id}
                      to={`/exa/ui/courses/${course.id}`}
                      className="block bg-gray-50 rounded-lg p-6 border border-gray-200 hover:shadow-md transition-shadow"
                    >
                      <h3 className="text-xl font-semibold text-gray-900 mb-4">
                        {course.name}
                      </h3>
                      <div className="space-y-3">
                        <div className="flex items-center text-gray-600">
                          <svg
                            className="w-5 h-5 mr-2"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"
                            />
                          </svg>
                          {course.faculty}
                        </div>

                        <div className="flex items-center text-gray-600">
                          <svg
                            className="w-5 h-5 mr-2"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
                            />
                          </svg>
                          Max Students: {course.maxStudents}
                        </div>

                        <div className="flex items-center text-gray-600">
                          <svg
                            className="w-5 h-5 mr-2"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                            />
                          </svg>
                          {lecturer
                            ? `${lecturer.firstname} ${lecturer.name}`
                            : 'Unknown Lecturer'}
                        </div>
                      </div>
                    </Link>
                  )
                })}
              </div>
            </div>
          ),
        )}
      </div>

      {filteredCourses.length === 0 && (
        <div className="text-center py-12 bg-gray-50 rounded-lg">
          <p className="text-gray-600">No courses match your filters</p>
        </div>
      )}
    </div>
  )
}
