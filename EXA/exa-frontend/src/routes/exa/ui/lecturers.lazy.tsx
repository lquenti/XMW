import { createLazyFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { useLecturers } from '../../../lib/use-lecturers'
import { parseLecturersXml } from '../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/lecturers')({
  component: RouteComponent,
})

function RouteComponent() {
  const { data, isLoading, error } = useLecturers()
  const [facultyFilter, setFacultyFilter] = useState<string>('all')

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="p-4 bg-red-50 text-red-600 rounded-lg">
        Error: {error.message}
      </div>
    )
  }

  if (!data) {
    return (
      <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
        No lecturer data available
      </div>
    )
  }

  const lecturers = parseLecturersXml(data)

  // Get unique faculties for filter
  const faculties = Array.from(new Set(lecturers.map(lecturer => lecturer.faculty ?? 'Other'))).sort()

  // Filter lecturers by faculty if needed
  const filteredLecturers = facultyFilter === 'all'
    ? lecturers
    : lecturers.filter(lecturer => (lecturer.faculty ?? 'Other') === facultyFilter)

  // Group lecturers by faculty
  const lecturersByFaculty = filteredLecturers.reduce<Record<string, typeof lecturers>>((acc, lecturer) => {
    const faculty = lecturer.faculty ?? 'Other'
    acc[faculty] = acc[faculty] || []
    acc[faculty].push(lecturer)
    return acc
  }, {})

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Lecturers</h1>

        {/* Filters */}
        <div className="flex flex-wrap gap-4 mb-6">
          <select
            className="px-4 py-2 rounded-lg border border-gray-300 bg-white"
            value={facultyFilter}
            onChange={(e) => setFacultyFilter(e.target.value)}
          >
            <option value="all">All Faculties</option>
            {faculties.map(faculty => (
              <option key={faculty} value={faculty}>{faculty}</option>
            ))}
          </select>
        </div>

        {/* Results count */}
        <p className="text-gray-600 mb-4">
          Showing {filteredLecturers.length} lecturer{filteredLecturers.length !== 1 ? 's' : ''}
        </p>
      </div>

      {/* Lecturers by faculty */}
      <div className="space-y-8">
        {Object.entries(lecturersByFaculty).map(([faculty, facultyLecturers]) => (
          <div key={faculty} className="bg-white rounded-xl p-6 shadow-sm">
            <h2 className="text-2xl font-bold text-gray-900 mb-4">{faculty}</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {facultyLecturers.map(lecturer => (
                <div
                  key={lecturer.id}
                  className="bg-gray-50 rounded-lg p-6 border border-gray-200 hover:shadow-md transition-shadow"
                >
                  <div className="flex items-start space-x-4">
                    <img
                      src={`https://i.pravatar.cc/150?u=${lecturer.id}`}
                      alt={`${lecturer.firstname} ${lecturer.name}`}
                      className="w-16 h-16 rounded-full object-cover"
                    />
                    <div>
                      <h3 className="text-xl font-semibold text-gray-900">
                        {lecturer.firstname} {lecturer.name}
                      </h3>
                      <div className="mt-2 space-y-2">
                        <div className="flex items-center text-gray-600">
                          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                          </svg>
                          {lecturer.faculty ?? 'No Faculty'}
                        </div>
                        <div className="flex items-center text-gray-600">
                          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                          </svg>
                          {lecturer.username}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {filteredLecturers.length === 0 && (
        <div className="text-center py-12 bg-gray-50 rounded-lg">
          <p className="text-gray-600">No lecturers match your filters</p>
        </div>
      )}
    </div>
  )
}
