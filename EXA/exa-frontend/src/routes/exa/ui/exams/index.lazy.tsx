import { createLazyFileRoute, Link } from '@tanstack/react-router'
import { useState } from 'react'
import { useCourses } from '../../../../lib/use-courses'
import { useExams } from '../../../../lib/use-exams'
import { AuthorizationState, parseCoursesXml, parseExamsXml, useAuthorizationState } from '../../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/exams/')({
  component: RouteComponent,
})

function RouteComponent() {
  const { data, isLoading, error } = useExams()
  const {
    data: coursesData,
    isLoading: coursesLoading,
    error: coursesError,
  } = useCourses()
  const [filter, setFilter] = useState<'all' | 'upcoming' | 'past'>('upcoming')
  const [typeFilter, setTypeFilter] = useState<'all' | 'written' | 'oral'>(
    'all',
  )
  const [locationFilter, setLocationFilter] = useState<
    'all' | 'online' | 'onsite'
  >('all')
  const { state: authState } = useAuthorizationState()

  if (isLoading || coursesLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  if (error || coursesError) {
    return (
      <div className="p-4 bg-red-50 text-red-600 rounded-lg">
        Error: {error?.message || coursesError?.message}
      </div>
    )
  }

  if (!data || !coursesData) {
    return (
      <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
        No exam data available
      </div>
    )
  }

  const exams = parseExamsXml(data)
  const courses = parseCoursesXml(coursesData)

  // Sort exams by date
  const sortedExams = [...exams].sort((a, b) =>
    (a.date ?? '').localeCompare(b.date ?? ''),
  )

  // Filter exams
  const now = new Date()
  const filteredExams = sortedExams.filter((exam) => {
    const examDate = exam.date ? new Date(exam.date) : null
    const isUpcoming = examDate ? examDate >= now : false

    if (filter === 'upcoming' && !isUpcoming) return false
    if (filter === 'past' && isUpcoming) return false

    if (typeFilter !== 'all') {
      if (typeFilter === 'written' && !exam.isWritten) return false
      if (typeFilter === 'oral' && exam.isWritten) return false
    }

    if (locationFilter !== 'all') {
      if (locationFilter === 'online' && !exam.isOnline) return false
      if (locationFilter === 'onsite' && exam.isOnline) return false
    }

    return true
  })

  const canCreateExam = authState === AuthorizationState.Admin || authState === AuthorizationState.Lecturer

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-3xl font-bold text-gray-900">Exams</h1>
          {canCreateExam && (
            <Link
              to="/exa/ui/exams/new"
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
              <span>New Exam</span>
            </Link>
          )}
        </div>

        {/* Filters */}
        <div className="flex flex-wrap gap-4 mb-6">
          <select
            className="px-4 py-2 rounded-lg border border-gray-300 bg-white"
            value={filter}
            onChange={(e) => setFilter(e.target.value as typeof filter)}
          >
            <option value="all">All Exams</option>
            <option value="upcoming">Upcoming</option>
            <option value="past">Past</option>
          </select>

          <select
            className="px-4 py-2 rounded-lg border border-gray-300 bg-white"
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value as typeof typeFilter)}
          >
            <option value="all">All Types</option>
            <option value="written">Written</option>
            <option value="oral">Oral</option>
          </select>

          <select
            className="px-4 py-2 rounded-lg border border-gray-300 bg-white"
            value={locationFilter}
            onChange={(e) =>
              setLocationFilter(e.target.value as typeof locationFilter)
            }
          >
            <option value="all">All Locations</option>
            <option value="online">Online</option>
            <option value="onsite">On-site</option>
          </select>
        </div>

        {/* Results count */}
        <p className="text-gray-600 mb-4">
          Showing {filteredExams.length} exam
          {filteredExams.length !== 1 ? 's' : ''}
        </p>
      </div>

      {/* Exams grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredExams.map((exam) => {
          const course = courses.find((c) => c.id === exam.course)
          const examDate = exam.date ? new Date(exam.date) : null
          const isUpcoming = examDate ? examDate >= now : false

          return (
            <div
              key={exam.id}
              className={`rounded-xl border ${isUpcoming
                ? 'bg-white border-blue-200'
                : 'bg-gray-50 border-gray-200'
                } p-6 shadow-sm hover:shadow-md transition-shadow`}
            >
              <div className="flex justify-between items-start mb-4">
                <h3 className="text-xl font-semibold text-gray-900 flex-1">
                  {course?.name ?? 'Unknown Course'}
                </h3>
                <span
                  className={`px-3 py-1 rounded-full text-sm ${exam.isWritten
                    ? 'bg-purple-100 text-purple-700'
                    : 'bg-green-100 text-green-700'
                    }`}
                >
                  {exam.isWritten ? 'Written' : 'Oral'}
                </span>
              </div>

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
                      d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                    />
                  </svg>
                  {examDate?.toLocaleString() ?? 'Date not set'}
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
                      d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                    />
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                    />
                  </svg>
                  {exam.isOnline ? 'Online' : 'On-site'}
                </div>

                <div className="flex items-center text-gray-600 truncate text-nowrap">
                  <svg
                    className="min-w-5 w-5 h-5 mr-2"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth={2}
                      d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"
                    />
                  </svg>
                  {exam.roomOrLink}
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {filteredExams.length === 0 && (
        <div className="text-center py-12 bg-gray-50 rounded-lg">
          <p className="text-gray-600">No exams match your filters</p>
        </div>
      )}
    </div>
  )
}
